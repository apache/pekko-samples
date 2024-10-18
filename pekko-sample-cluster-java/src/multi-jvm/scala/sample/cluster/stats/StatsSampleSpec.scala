package sample.cluster.stats

import org.apache.pekko
import pekko.actor.testkit.typed.scaladsl.TestProbe
import pekko.actor.typed.ActorSystem
import pekko.actor.typed.receptionist.Receptionist
import pekko.actor.typed.scaladsl.adapter._
import pekko.cluster.Cluster
import pekko.cluster.ClusterEvent.CurrentClusterState
import pekko.cluster.ClusterEvent.MemberUp
import pekko.remote.testkit.MultiNodeConfig
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

object StatsSampleSpecConfig extends MultiNodeConfig {
  // register the named roles (nodes) of the test
  // note that this is not the same thing as cluster node roles
  val first = role("first")
  val second = role("second")
  val third = role("thrid")

  // this configuration will be used for all nodes
  // note that no fixed host names and ports are used
  commonConfig(ConfigFactory.parseString("""
    pekko.actor.provider = cluster
    pekko.cluster.roles = [compute]
    """).withFallback(ConfigFactory.load()))

}
// need one concrete test class per node
class StatsSampleSpecMultiJvmNode1 extends StatsSampleSpec
class StatsSampleSpecMultiJvmNode2 extends StatsSampleSpec
class StatsSampleSpecMultiJvmNode3 extends StatsSampleSpec

import org.apache.pekko.remote.testkit.MultiNodeSpec
import org.apache.pekko.testkit.ImplicitSender
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

abstract class StatsSampleSpec extends MultiNodeSpec(StatsSampleSpecConfig)
    with AnyWordSpecLike with Matchers with BeforeAndAfterAll
    with ImplicitSender {

  import StatsSampleSpecConfig._

  override def initialParticipants = roles.size

  override def beforeAll() = multiNodeSpecBeforeAll()

  override def afterAll() = multiNodeSpecAfterAll()

  implicit val typedSystem: ActorSystem[Nothing] = system.toTyped

  "The stats sample" must {

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

      testConductor.enter("all-up")
    }

    "show usage of the statsService from one node" in within(15.seconds) {
      runOn(first, second) {
        val worker = system.spawn(StatsWorker.create(), "StatsWorker")
        val service = system.spawn(StatsService.create(worker), "StatsService")
        typedSystem.receptionist ! Receptionist.Register(App.STATS_SERVICE_KEY, service)
      }
      runOn(third) {
        assertServiceOk()
      }

      testConductor.enter("done-2")
    }

    def assertServiceOk(): Unit = {
      // eventually the service should be ok,
      // first attempts might fail because worker actors not started yet
      awaitAssert {
        val probe = TestProbe[AnyRef]()
        typedSystem.receptionist ! Receptionist.Find(App.STATS_SERVICE_KEY, probe.ref)
        val App.STATS_SERVICE_KEY.Listing(actors) = probe.expectMessageType[Receptionist.Listing]
        actors should not be empty

        actors.head ! new StatsService.ProcessText("this is the text that will be analyzed", probe.ref)
        probe.expectMessageType[StatsService.JobResult].meanWordLength should be(
          3.875 +- 0.001)
      }
    }

    "show usage of the statsService from all nodes" in within(15.seconds) {
      assertServiceOk()
      testConductor.enter("done-3")
    }

  }

}
