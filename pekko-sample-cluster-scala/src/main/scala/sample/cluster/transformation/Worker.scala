package sample.cluster.transformation

import org.apache.pekko.actor.typed.ActorRef
import org.apache.pekko.actor.typed.Behavior
import org.apache.pekko.actor.typed.receptionist.Receptionist
import org.apache.pekko.actor.typed.receptionist.ServiceKey
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import sample.cluster.CborSerializable

//#worker
object Worker {

  val WorkerServiceKey = ServiceKey[Worker.TransformText]("Worker")

  sealed trait Command
  final case class TransformText(text: String, replyTo: ActorRef[TextTransformed]) extends Command with CborSerializable
  final case class TextTransformed(text: String) extends CborSerializable

  def apply(): Behavior[Command] =
    Behaviors.setup { ctx =>
      // each worker registers themselves with the receptionist
      ctx.log.info("Registering myself with receptionist")
      ctx.system.receptionist ! Receptionist.Register(WorkerServiceKey, ctx.self)

      Behaviors.receiveMessage {
        case TransformText(text, replyTo) =>
          replyTo ! TextTransformed(text.toUpperCase)
          Behaviors.same
      }
    }
}
//#worker
