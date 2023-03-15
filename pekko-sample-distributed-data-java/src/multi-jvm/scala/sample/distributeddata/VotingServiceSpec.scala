package sample.distributeddata

import java.math.BigInteger
import scala.concurrent.duration._
import org.apache.pekko.actor.testkit.typed.scaladsl.TestProbe
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.scaladsl.adapter._
import org.apache.pekko.cluster.ddata.typed.javadsl.DistributedData
import org.apache.pekko.cluster.ddata.typed.javadsl.Replicator.GetReplicaCount
import org.apache.pekko.cluster.ddata.typed.javadsl.Replicator.ReplicaCount
import org.apache.pekko.cluster.typed.{ Cluster, Join }
import org.apache.pekko.remote.testconductor.RoleName
import org.apache.pekko.remote.testkit.MultiNodeConfig
import org.apache.pekko.remote.testkit.MultiNodeSpec
import com.typesafe.config.ConfigFactory

object VotingServiceSpec extends MultiNodeConfig {
  val node1 = role("node-1")
  val node2 = role("node-2")
  val node3 = role("node-3")

  commonConfig(ConfigFactory.parseString("""
    pekko.loglevel = INFO
    pekko.actor.provider = "cluster"
    pekko.log-dead-letters-during-shutdown = off
    """))

}

class VotingServiceSpecMultiJvmNode1 extends VotingServiceSpec
class VotingServiceSpecMultiJvmNode2 extends VotingServiceSpec
class VotingServiceSpecMultiJvmNode3 extends VotingServiceSpec

class VotingServiceSpec extends MultiNodeSpec(VotingServiceSpec) with STMultiNodeSpec {
  import VotingServiceSpec._

  override def initialParticipants = roles.size

  implicit val typedSystem: ActorSystem[Nothing] = system.toTyped
  val cluster = Cluster(typedSystem)

  def join(from: RoleName, to: RoleName): Unit = {
    runOn(from) {
      cluster.manager ! Join(node(to).address)
    }
    enterBarrier(from.name + "-joined")
  }

  "Demo of a replicated voting" must {

    "join cluster" in within(20.seconds) {
      join(node1, node1)
      join(node2, node1)
      join(node3, node1)

      awaitAssert {
        val probe = TestProbe[ReplicaCount]()
        DistributedData(typedSystem).replicator ! GetReplicaCount(probe.ref)
        probe.expectMessage(ReplicaCount(roles.size))
      }
      enterBarrier("after-1")
    }

    "count votes correctly" in within(15.seconds) {
      import VotingService._
      val votingService = system.spawn(VotingService.create(), "votingService")
      val N = 1000
      runOn(node1) {
        votingService ! VotingService.Open.INSTANCE
        for (n ← 1 to N) {
          votingService ! new Vote("#" + ((n % 20) + 1))
        }
      }
      runOn(node2, node3) {
        // wait for it to open
        val p = TestProbe[Votes]()
        awaitAssert {
          votingService.tell(new GetVotes(p.ref))
          p.expectMessageType[Votes](3.seconds).open should be(true)
        }
        for (n ← 1 to N) {
          votingService ! new Vote("#" + ((n % 20) + 1))
        }
      }
      enterBarrier("voting-done")
      runOn(node3) {
        votingService ! VotingService.Close.INSTANCE
      }

      val expected = (1 to 20).map(n => "#" + n -> BigInteger.valueOf(3L * N / 20)).toMap
      awaitAssert {
        val p = TestProbe[Votes]()
        votingService ! new GetVotes(p.ref)
        val votes = p.expectMessageType[Votes](3.seconds)
        votes.open should be (false)
        import scala.collection.JavaConverters._
        votes.result.asScala.toMap should be (expected)
      }

      enterBarrier("after-2")
    }
  }

}

