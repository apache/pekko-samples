package sample.distributeddata

import scala.concurrent.duration._
import org.apache.pekko.actor.testkit.typed.scaladsl.TestProbe
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.eventstream.EventStream
import org.apache.pekko.actor.typed.scaladsl.adapter._
import org.apache.pekko.cluster.ddata.Replicator
import org.apache.pekko.cluster.ddata.typed.scaladsl.DistributedData
import org.apache.pekko.cluster.ddata.typed.scaladsl.Replicator.GetReplicaCount
import org.apache.pekko.cluster.ddata.typed.scaladsl.Replicator.ReplicaCount
import org.apache.pekko.cluster.typed.{ Cluster, Join, Leave }
import org.apache.pekko.remote.testconductor.RoleName
import org.apache.pekko.remote.testkit.MultiNodeConfig
import org.apache.pekko.remote.testkit.MultiNodeSpec
import com.typesafe.config.ConfigFactory

object ReplicatedMetricsSpec extends MultiNodeConfig {
  val node1 = role("node-1")
  val node2 = role("node-2")
  val node3 = role("node-3")

  commonConfig(ConfigFactory.parseString("""
    pekko.loglevel = INFO
    pekko.actor.provider = "cluster"
    pekko.log-dead-letters-during-shutdown = off
    """))

}

class ReplicatedMetricsSpecMultiJvmNode1 extends ReplicatedMetricsSpec
class ReplicatedMetricsSpecMultiJvmNode2 extends ReplicatedMetricsSpec
class ReplicatedMetricsSpecMultiJvmNode3 extends ReplicatedMetricsSpec

class ReplicatedMetricsSpec extends MultiNodeSpec(ReplicatedMetricsSpec) with STMultiNodeSpec {
  import ReplicatedMetricsSpec._
  import ReplicatedMetrics._

  override def initialParticipants = roles.size

  implicit val typedSystem: ActorSystem[Nothing] = system.toTyped
  val cluster = Cluster(typedSystem)
  system.spawnAnonymous(ReplicatedMetrics(1.second, 3.seconds))

  def join(from: RoleName, to: RoleName): Unit = {
    runOn(from) {
      cluster.manager ! Join(node(to).address)
    }
    enterBarrier(from.name + "-joined")
  }

  "Demo of a replicated metrics" must {
    "join cluster" in within(20.seconds) {
      join(node1, node1)
      join(node2, node1)
      join(node3, node1)

      awaitAssert {
        val probe = TestProbe[ReplicaCount]()
        DistributedData(typedSystem).replicator ! GetReplicaCount(probe.ref)
        probe.expectMessage(Replicator.ReplicaCount(roles.size))
      }
      enterBarrier("after-1")
    }

    "replicate metrics" in within(30.seconds) {
      val probe = TestProbe[UsedHeap]()
      typedSystem.eventStream ! EventStream.Subscribe(probe.ref)
      awaitAssert {
        probe.expectMessageType[UsedHeap](1.second).percentPerNode.size should be(3)
      }
      probe.expectMessageType[UsedHeap].percentPerNode.size should be(3)
      probe.expectMessageType[UsedHeap].percentPerNode.size should be(3)
      enterBarrier("after-2")
    }

    "cleanup removed node" in within(30.seconds) {
      val node3Address = node(node3).address
      runOn(node1) {
        cluster.manager ! Leave(node3Address)
      }
      runOn(node1, node2) {
        val probe = TestProbe[UsedHeap]()
        typedSystem.eventStream ! EventStream.Subscribe(probe.ref)
        awaitAssert {
          probe.expectMessageType[UsedHeap](1.second).percentPerNode.size should be(2)
        }
        probe.expectMessageType[UsedHeap].percentPerNode should not contain (
          nodeKey(node3Address))
      }
      enterBarrier("after-3")
    }

  }

}
