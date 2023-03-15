package sample.distributeddata

import scala.concurrent.duration._
import org.apache.pekko.actor.testkit.typed.scaladsl.TestProbe
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.scaladsl.adapter._
import org.apache.pekko.cluster.ddata.Replicator
import org.apache.pekko.cluster.ddata.typed.scaladsl.DistributedData
import org.apache.pekko.cluster.ddata.typed.scaladsl.Replicator.GetReplicaCount
import org.apache.pekko.cluster.ddata.typed.scaladsl.Replicator.ReplicaCount
import org.apache.pekko.cluster.typed.{ Cluster, Join }
import org.apache.pekko.remote.testconductor.RoleName
import org.apache.pekko.remote.testkit.MultiNodeConfig
import org.apache.pekko.remote.testkit.MultiNodeSpec
import com.typesafe.config.ConfigFactory
import scala.collection.JavaConverters._

object ShoppingCartSpec extends MultiNodeConfig {
  val node1 = role("node-1")
  val node2 = role("node-2")
  val node3 = role("node-3")

  commonConfig(ConfigFactory.parseString("""
    pekko.loglevel = INFO
    pekko.actor.provider = "cluster"
    pekko.log-dead-letters-during-shutdown = off
    pekko.actor.serialization-bindings {
      "sample.distributeddata.ShoppingCart$LineItem" = jackson-cbor
    }
    """))

}

class ShoppingCartSpecMultiJvmNode1 extends ShoppingCartSpec
class ShoppingCartSpecMultiJvmNode2 extends ShoppingCartSpec
class ShoppingCartSpecMultiJvmNode3 extends ShoppingCartSpec

class ShoppingCartSpec extends MultiNodeSpec(ShoppingCartSpec) with STMultiNodeSpec {
  import ShoppingCartSpec._
  import ShoppingCart._

  override def initialParticipants = roles.size

  implicit val typedSystem: ActorSystem[Nothing] = system.toTyped
  val cluster = Cluster(typedSystem)
  val shoppingCart = system.spawnAnonymous(ShoppingCart.create("user-1"))

  def join(from: RoleName, to: RoleName): Unit = {
    runOn(from) {
      cluster.manager ! Join(node(to).address)
    }
    enterBarrier(from.name + "-joined")
  }

  "Demo of a replicated shopping cart" must {
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

    "handle updates directly after start" in within(15.seconds) {
      runOn(node2) {
        shoppingCart ! new ShoppingCart.AddItem(new LineItem("1", "Apples", 2))
        shoppingCart ! new ShoppingCart.AddItem(new LineItem("2", "Oranges", 3))
      }
      enterBarrier("updates-done")

      awaitAssert {
        val probe = TestProbe[Cart]()
        shoppingCart ! new ShoppingCart.GetCart(probe.ref)
        val cart = probe.expectMessageType[Cart]
        cart.items.asScala.toSet should be(Set(
            new LineItem("1", "Apples", 2), new LineItem("2", "Oranges", 3)))
      }

      enterBarrier("after-2")
    }

    "handle updates from different nodes" in within(5.seconds) {
      runOn(node2) {
        shoppingCart ! new ShoppingCart.AddItem(new LineItem("1", "Apples", 5))
        shoppingCart ! new ShoppingCart.RemoveItem("2")
      }
      runOn(node3) {
        shoppingCart ! new ShoppingCart.AddItem(new LineItem("3", "Bananas", 4))
      }
      enterBarrier("updates-done")

      awaitAssert {
        val probe = TestProbe[Cart]()
        shoppingCart ! new ShoppingCart.GetCart(probe.ref)
        val cart = probe.expectMessageType[Cart]
        cart.items.asScala.toSet should be(
            Set(new LineItem("1", "Apples", 7), new LineItem("3", "Bananas", 4)))
      }

      enterBarrier("after-3")
    }

  }

}

