package worker

import org.apache.pekko.actor.typed.Behavior
import org.apache.pekko.actor.typed.scaladsl.{ ActorContext, Behaviors }
import org.apache.pekko.persistence.typed.PublishedEvent
import worker.WorkState.{ WorkAccepted, WorkCompleted, WorkInProgressReset, WorkStarted }

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
    val actualEvent = event.event
    event.event match {
      case WorkInProgressReset =>
        context.log.info(s"Received published event [${actualEvent.getClass.getCanonicalName}]: {}", event)
      case WorkCompleted(workId) =>
        context.log.info(s"Received published event [${actualEvent.getClass.getCanonicalName}]: workId {}", workId)
      case WorkStarted(workId) =>
        context.log.info(s"Received published event [${actualEvent.getClass.getCanonicalName}]: workId {}", workId)
      case WorkAccepted(workId) =>
        context.log.info(s"Received published event [${actualEvent.getClass.getCanonicalName}]: workId {}", workId)
      case _ => context.log.warn("Message not supported")
    }
  }
}
