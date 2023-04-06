package sample.cluster.stats

import org.apache.pekko.actor.testkit.typed.scaladsl.TestProbe
import org.apache.pekko.actor.typed.scaladsl.AskPattern._
import org.apache.pekko.actor.typed.scaladsl.adapter._
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.actor.typed.scaladsl.Routers
import org.apache.pekko.actor.typed.ActorRef
import org.apache.pekko.actor.typed.Behavior
import org.apache.pekko.actor.typed.Props
import org.apache.pekko.actor.typed.SpawnProtocol
import org.apache.pekko.cluster.Cluster
import org.apache.pekko.cluster.ClusterEvent.CurrentClusterState
import org.apache.pekko.cluster.ClusterEvent.MemberUp
import org.apache.pekko.cluster.typed.ClusterSingleton
import org.apache.pekko.cluster.typed.ClusterSingletonSettings
import org.apache.pekko.cluster.typed.SingletonActor
import org.apache.pekko.remote.testkit.MultiNodeConfig
import org.apache.pekko.remote.testkit.MultiNodeSpec
import org.apache.pekko.testkit.ImplicitSender
import org.apache.pekko.util.Timeout
import com.typesafe.config.ConfigFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import scala.concurrent.duration._
import scala.concurrent.Await
import scala.concurrent.Future

object StatsSampleSingleMasterSpecConfig extends MultiNodeConfig {
  // register the named roles (nodes) of the test
  // note that this is not the same thing as cluster node roles
  val first = role("first")
  val second = role("second")
  val third = role("third")

  // this configuration will be used for all nodes
  // note that no fixed host names and ports are used
  commonConfig(ConfigFactory.parseString("""
    pekko.loglevel = INFO
    pekko.actor.provider = cluster
    pekko.cluster.roles = [compute]
    """).withFallback(ConfigFactory.load()))

}

// need one concrete test class per node
class StatsSampleSingleMasterSpecMultiJvmNode1 extends StatsSampleSingleMasterSpec
class StatsSampleSingleMasterSpecMultiJvmNode2 extends StatsSampleSingleMasterSpec
class StatsSampleSingleMasterSpecMultiJvmNode3 extends StatsSampleSingleMasterSpec

abstract class StatsSampleSingleMasterSpec extends MultiNodeSpec(StatsSampleSingleMasterSpecConfig)
    with AnyWordSpecLike with Matchers with BeforeAndAfterAll with ImplicitSender {

  import StatsSampleSingleMasterSpecConfig._

  override def initialParticipants = roles.size

  override def beforeAll() = multiNodeSpecBeforeAll()

  override def afterAll() = multiNodeSpecAfterAll()

  implicit val typedSystem = system.toTyped

  var singletonProxy: ActorRef[StatsService.Command] = _

  "The stats sample with single master" must {
    "illustrate how to startup cluster" in within(15.seconds) {
      Cluster(system).subscribe(testActor, classOf[MemberUp])
      expectMsgClass(classOf[CurrentClusterState])

      val firstAddress = node(first).address
      val secondAddress = node(second).address
      val thirdAddress = node(third).address

      Cluster(system).join(firstAddress)

      receiveN(3).collect { case MemberUp(m) => m.address }.toSet should be(
        Set(firstAddress, secondAddress, thirdAddress))

      Cluster(system).unsubscribe(testActor)

      val singletonSettings = ClusterSingletonSettings(typedSystem).withRole("compute")
      singletonProxy = ClusterSingleton(typedSystem).init(
        SingletonActor(
          Behaviors.setup[StatsService.Command] { ctx =>
            // just run some local workers for this test
            val workersRouter = ctx.spawn(Routers.pool(2)(StatsWorker()), "WorkersRouter")
            StatsService(workersRouter)
          },
          "StatsService").withSettings(singletonSettings))

      testConductor.enter("all-up")
    }

    "show usage of the statsServiceProxy" in within(20.seconds) {
      // eventually the service should be ok,
      // service and worker nodes might not be up yet
      awaitAssert {
        system.log.info("Trying a request")
        val probe = TestProbe[StatsService.Response]()
        singletonProxy ! StatsService.ProcessText("this is the text that will be analyzed", probe.ref)
        val response = probe.expectMessageType[StatsService.JobResult](3.seconds)
        response.meanWordLength should be(3.875 +- 0.001)
      }

      testConductor.enter("done")
    }
  }

}
