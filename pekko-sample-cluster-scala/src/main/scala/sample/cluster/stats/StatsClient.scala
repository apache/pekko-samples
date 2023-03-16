/*
 * Copyright (C) 2009-2019 Lightbend Inc. <https://www.lightbend.com>
 */
package sample.cluster.stats

import org.apache.pekko.actor.typed.ActorRef
import org.apache.pekko.actor.typed.Behavior
import org.apache.pekko.actor.typed.scaladsl.Behaviors

import scala.concurrent.duration._

object StatsClient {

  sealed trait Event
  private case object Tick extends Event
  private case class ServiceResponse(result: StatsService.Response) extends Event

  def apply(service: ActorRef[StatsService.ProcessText]): Behavior[Event] =
    Behaviors.setup { ctx =>
      Behaviors.withTimers { timers =>
        timers.startTimerWithFixedDelay(Tick, Tick, 2.seconds)
        val responseAdapter = ctx.messageAdapter(ServiceResponse)

        Behaviors.receiveMessage {
          case Tick =>
            ctx.log.info("Sending process request")
            service ! StatsService.ProcessText("this is the text that will be analyzed", responseAdapter)
            Behaviors.same
          case ServiceResponse(result) =>
            ctx.log.info("Service result: {}", result)
            Behaviors.same
        }
      }
    }

}

