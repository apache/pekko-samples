package worker

import java.io.File
import java.util.concurrent.CountDownLatch

import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.cluster.typed.Cluster
import org.apache.pekko.persistence.cassandra.testkit.CassandraLauncher
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.pekko.cluster.typed.SelfUp
import org.apache.pekko.cluster.typed.Subscribe

object Main {

  // note that 7345 and 7355 are expected to be seed nodes though, even if
  // the back-end starts at 2000
  val backEndPortRange = 2000 to 2999

  val frontEndPortRange = 3000 to 3999

  def main(args: Array[String]): Unit = {
    args.headOption match {

      case None =>
        startClusterInSameJvm()

      case Some(portString) if portString.matches("""\d+""") =>
        val port = portString.toInt
        if (backEndPortRange.contains(port)) start(port, "back-end")
        else if (frontEndPortRange.contains(port)) start(port, "front-end")
        else start(port, "worker", args.lift(1).map(_.toInt).getOrElse(1))

      case Some("cassandra") =>
        startCassandraDatabase()
        println("Started Apache Cassandra, press Ctrl + C to kill")
        new CountDownLatch(1).await()

    }
  }

  def startClusterInSameJvm(): Unit = {
    startCassandraDatabase()
    // two backend nodes
    start(7345, "back-end")
    start(7355, "back-end")
    // two front-end nodes
    start(3000, "front-end")
    start(3001, "front-end")
    // two worker nodes with two worker actors each
    start(5001, "worker", 2)
    start(5002, "worker", 2)
  }

  def start(port: Int, role: String, workers: Int = 2): Unit = {
    ActorSystem(
      Behaviors.setup[SelfUp](ctx => {
        val cluster = Cluster(ctx.system)
        cluster.subscriptions ! Subscribe(ctx.self, classOf[SelfUp])
        Behaviors.receiveMessage {
          case SelfUp(_) =>
            ctx.log.info("Node is up")
            if (cluster.selfMember.hasRole("back-end")) {
              WorkManagerSingleton.init(ctx.system)
            }
            if (cluster.selfMember.hasRole("front-end")) {
              val workManagerProxy = WorkManagerSingleton.init(ctx.system)
              ctx.spawn(FrontEnd(workManagerProxy), "front-end")
            }
            if (cluster.selfMember.hasRole("worker")) {
              (1 to workers).foreach(n => ctx.spawn(Worker(), s"worker-$n"))
            }
            Behaviors.same
        }
      }),
      "ClusterSystem",
      config(port, role)
    )
  }

  def config(port: Int, role: String): Config =
    ConfigFactory.parseString(s"""
      pekko.remote.artery.canonical.port=$port
      pekko.cluster.roles=[$role]
    """).withFallback(ConfigFactory.load())

  /**
    * To make the sample easier to run we kickstart a Apache Cassandra instance to
    * act as the journal. Apache Cassandra is a great choice of backend for Apache Pekko Persistence but
    * in a real application a pre-existing Apache Cassandra cluster should be used.
    */
  def startCassandraDatabase(): Unit = {
    val databaseDirectory = new File("target/cassandra-db")
    CassandraLauncher.start(
      databaseDirectory,
      CassandraLauncher.DefaultTestConfigResource,
      clean = false,
      port = 9042
    )

    // shut the cassandra instance down when the JVM stops
    sys.addShutdownHook {
      CassandraLauncher.stop()
    }
  }

}
