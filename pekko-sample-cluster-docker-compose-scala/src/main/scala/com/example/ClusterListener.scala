package com.example

import org.apache.pekko.actor.typed.Behavior
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.cluster.ClusterEvent
import org.apache.pekko.cluster.ClusterEvent._
import org.apache.pekko.cluster.typed.Cluster
import org.apache.pekko.cluster.typed.Subscribe

object ClusterListener {
  def apply(): Behavior[ClusterEvent.ClusterDomainEvent] =
    Behaviors.setup { ctx =>
      ctx.log.debug("starting up cluster listener...")
      Cluster(ctx.system).subscriptions ! Subscribe(ctx.self, classOf[ClusterEvent.ClusterDomainEvent])

      Behaviors.receiveMessagePartial {
        case MemberUp(member) =>
          ctx.log.debug("Member is Up: {}", member.address)
          Behaviors.same
        case UnreachableMember(member) =>
          ctx.log.debug("Member detected as unreachable: {}", member)
          Behaviors.same
        case MemberRemoved(member, previousStatus) =>
          ctx.log.debug("Member is Removed: {} after {}",
            member.address, previousStatus)
          Behaviors.same
        case LeaderChanged(member) =>
          ctx.log.info("Leader changed: " + member)
          Behaviors.same
        case any: MemberEvent =>
          ctx.log.info("Member Event: " + any.toString)
          Behaviors.same
      }
    }
}
