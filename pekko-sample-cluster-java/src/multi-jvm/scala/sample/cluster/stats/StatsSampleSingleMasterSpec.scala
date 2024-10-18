package sample.cluster.stats

import org.apache.pekko
import pekko.actor.testkit.typed.scaladsl.TestProbe
import pekko.actor.typed.{ ActorRef, ActorSystem }
import pekko.actor.typed.scaladsl.adapter._
import pekko.actor.typed.scaladsl.{ Behaviors, Routers }
import pekko.cluster.Cluster
import pekko.cluster.ClusterEvent.{ CurrentClusterState, MemberUp }
import pekko.cluster.typed.{ ClusterSingleton, ClusterSingletonSettings, SingletonActor }
import pekko.remote.testkit.{ MultiNodeConfig, MultiNodeSpec }
import com.typesafe.config.ConfigFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import scala.concurrent.duration._

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
    with AnyWordSpecLike with Matchers with BeforeAndAfterAll {

  import StatsSampleSingleMasterSpecConfig._

  override def initialParticipants = roles.size

  override def beforeAll() = multiNodeSpecBeforeAll()

  override def afterAll() = multiNodeSpecAfterAll()

  implicit val typedSystem: ActorSystem[Nothing] = system.toTyped

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
            val workersRouter = ctx.spawn(Routers.pool(2)(StatsWorker.create()), "WorkersRouter")
            StatsService.create(workersRouter)
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
        singletonProxy ! new StatsService.ProcessText("this is the text that will be analyzed", probe.ref)
        val response = probe.expectMessageType[StatsService.JobResult](3.seconds)
        response.meanWordLength should be(3.875 +- 0.001)
      }

      testConductor.enter("done")
    }
  }

}
