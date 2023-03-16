package sample.cluster.simple

import org.apache.pekko.actor.typed.ActorRef
import org.apache.pekko.actor.typed.Behavior
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.cluster.ClusterEvent.MemberEvent
import org.apache.pekko.cluster.ClusterEvent.MemberRemoved
import org.apache.pekko.cluster.ClusterEvent.MemberUp
import org.apache.pekko.cluster.ClusterEvent.ReachabilityEvent
import org.apache.pekko.cluster.ClusterEvent.ReachableMember
import org.apache.pekko.cluster.ClusterEvent.UnreachableMember
import org.apache.pekko.cluster.typed.Cluster
import org.apache.pekko.cluster.typed.Subscribe

object ClusterListener {

  sealed trait Event
  // internal adapted cluster events only
  private final case class ReachabilityChange(reachabilityEvent: ReachabilityEvent) extends Event
  private final case class MemberChange(event: MemberEvent) extends Event

  def apply(): Behavior[Event] = Behaviors.setup { ctx =>
    val memberEventAdapter: ActorRef[MemberEvent] = ctx.messageAdapter(MemberChange)
    Cluster(ctx.system).subscriptions ! Subscribe(memberEventAdapter, classOf[MemberEvent])

    val reachabilityAdapter = ctx.messageAdapter(ReachabilityChange)
    Cluster(ctx.system).subscriptions ! Subscribe(reachabilityAdapter, classOf[ReachabilityEvent])

    Behaviors.receiveMessage { message =>
      message match {
        case ReachabilityChange(reachabilityEvent) =>
          reachabilityEvent match {
            case UnreachableMember(member) =>
              ctx.log.info("Member detected as unreachable: {}", member)
            case ReachableMember(member) =>
              ctx.log.info("Member back to reachable: {}", member)
          }

        case MemberChange(changeEvent) =>
          changeEvent match {
            case MemberUp(member) =>
              ctx.log.info("Member is Up: {}", member.address)
            case MemberRemoved(member, previousStatus) =>
              ctx.log.info("Member is Removed: {} after {}",
                member.address, previousStatus)
            case _: MemberEvent => // ignore
          }
      }
      Behaviors.same
    }
  }
}