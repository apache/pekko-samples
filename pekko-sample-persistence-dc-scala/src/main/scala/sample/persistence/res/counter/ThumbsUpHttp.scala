package sample.persistence.res.counter

import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.cluster.sharding.typed.ReplicatedSharding
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.model.{ ContentTypes, HttpEntity, HttpResponse, StatusCodes }
import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.persistence.typed.ReplicaId
import org.apache.pekko.stream.scaladsl.Source
import org.apache.pekko.util.{ ByteString, Timeout }
import sample.persistence.res.counter.ThumbsUpCounter.State

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{ Failure, Success }

object ThumbsUpHttp {

  def route(selfReplica: ReplicaId, res: ReplicatedSharding[ThumbsUpCounter.Command])(
      implicit system: ActorSystem[_]): Route = {

    import org.apache.pekko.http.scaladsl.server.Directives._

    implicit val timeout: Timeout = Timeout(10.seconds)
    implicit val ec: ExecutionContext = system.executionContext

    pathPrefix("thumbs-up") {
      concat(
        // example: curl http://127.0.0.1:22551/thumbs-up/a
        get {
          path(Segment) { resourceId =>
            onComplete(res.entityRefsFor(resourceId)(selfReplica).ask[State](replyTo =>
              ThumbsUpCounter.GetUsers(resourceId, replyTo))) {
              case Success(state) =>
                val s = Source.fromIterator(() => state.users.iterator)
                  .intersperse("\n")
                  .map(ByteString(_))
                complete(HttpResponse(entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`, s)))
              case Failure(ex) => complete(StatusCodes.InternalServerError, ex.toString)
            }
          }
        },
        // example: curl -X POST http://127.0.0.1:22551/thumbs-up/a/u1
        post {
          path(Segment / Segment) { (resourceId, userId) =>
            onComplete(res.entityRefsFor(resourceId)(selfReplica).ask[Long](replyTo =>
              ThumbsUpCounter.GiveThumbsUp(resourceId, userId, replyTo))) {
              case Success(i)  => complete(i.toString)
              case Failure(ex) => complete(StatusCodes.BadRequest, ex.toString)
            }
          }
        })
    }

  }
}
