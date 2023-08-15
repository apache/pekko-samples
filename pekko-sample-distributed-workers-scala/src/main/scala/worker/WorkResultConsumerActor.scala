package worker

import org.apache.pekko.actor.typed.Behavior
import org.apache.pekko.actor.typed.scaladsl.{ActorContext, Behaviors}
import org.apache.pekko.persistence.typed.PublishedEvent

object WorkResultConsumerActor {
  def apply(): Behavior[PublishedEvent] =
    Behaviors.setup { context =>
      context.log.info("WorkResultConsumerActor started")

      Behaviors.receiveMessage { message =>
        handleReceivedEvent(context, message)
        Behaviors.same
      }
    }

  private def handleReceivedEvent(context: ActorContext[PublishedEvent], event: PublishedEvent): Unit = {
    event.event match {
      case WorkState.WorkInProgressReset | WorkState.WorkCompleted | WorkState.WorkStarted | WorkState.WorkAccepted =>
        context.log.info(s"Received published event [${event.event.getClass.getCanonicalName}]: {}", event)
    }
  }
}