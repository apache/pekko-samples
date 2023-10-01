package sample.grpckubernetes

import scala.concurrent.Future
import org.apache.pekko.NotUsed
import org.apache.pekko.event.LoggingAdapter
import org.apache.pekko.stream.Materializer
import org.apache.pekko.stream.scaladsl.BroadcastHub
import org.apache.pekko.stream.scaladsl.Keep
import org.apache.pekko.stream.scaladsl.MergeHub
import org.apache.pekko.stream.scaladsl.Sink
import org.apache.pekko.stream.scaladsl.Source


class GreeterServiceImpl(materializer: Materializer, log: LoggingAdapter) extends GreeterService {


  private implicit val mat: Materializer = materializer

  val (inboundHub: Sink[HelloRequest, NotUsed], outboundHub: Source[HelloReply, NotUsed]) =
    MergeHub.source[HelloRequest]
      .map(request => HelloReply(s"Hello, ${request.name}"))
      .toMat(BroadcastHub.sink[HelloReply])(Keep.both)
      .run()

  override def sayHello(request: HelloRequest): Future[HelloReply] = {
    log.info("sayHello {}", request)
    Future.successful(HelloReply(s"Hello, ${request.name}"))
  }

  override def sayHelloToAll(in: Source[HelloRequest, NotUsed]): Source[HelloReply, NotUsed] = {
    log.info("sayHelloToAll")
    in.runWith(inboundHub)
    outboundHub
  }
}
