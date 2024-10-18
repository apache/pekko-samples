package sample.cluster.client.grpc

import java.util.concurrent.TimeUnit

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration._

import org.apache.pekko.actor.ActorRef
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.actor.ExtendedActorSystem
import org.apache.pekko.actor.Extension
import org.apache.pekko.actor.ExtensionId
import org.apache.pekko.actor.ExtensionIdProvider
import org.apache.pekko.cluster.Cluster
import org.apache.pekko.cluster.pubsub.DistributedPubSub
import org.apache.pekko.cluster.pubsub.DistributedPubSubMediator
import org.apache.pekko.event.Logging
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.HttpConnectionContext
import org.apache.pekko.http.scaladsl.model.HttpRequest
import org.apache.pekko.http.scaladsl.model.HttpResponse
import org.apache.pekko.stream.Materializer

object ClusterClientReceptionist extends ExtensionId[ClusterClientReceptionist] with ExtensionIdProvider {
  override def get(system: ActorSystem): ClusterClientReceptionist =
    super.get(system)

  override def lookup = ClusterClientReceptionist

  override def createExtension(system: ExtendedActorSystem): ClusterClientReceptionist =
    new ClusterClientReceptionist(system)
}

/**
 * Extension that starts gRPC service and accompanying `org.apache.pekko.cluster.pubsub.DistributedPubSubMediator`
 * with settings defined in config section `sample.cluster.client.grpc.receptionist`.
 * The `org.apache.pekko.cluster.pubsub.DistributedPubSubMediator` is started by the `org.apache.pekko.cluster.pubsub.DistributedPubSub`
 * extension.
 */
final class ClusterClientReceptionist(system: ExtendedActorSystem) extends Extension {

  val settings: ClusterReceptionistSettings = ClusterReceptionistSettings(system)
  private val role: Option[String] = settings.role

  private val log = Logging(system, classOf[ClusterClientReceptionist])

  /**
   * Returns true if this member is not tagged with the role configured for the
   * receptionist.
   */
  def isTerminated: Boolean =
    Cluster(system).isTerminated || !role.forall(Cluster(system).selfRoles.contains)

  /**
   * Register the actors that should be reachable for the clients in this [[DistributedPubSubMediator]].
   */
  private def pubSubMediator: ActorRef = DistributedPubSub(system).mediator

  /**
   * Register an actor that should be reachable for the clients.
   * The clients can send messages to this actor with `Send` or `SendToAll` using
   * the path elements of the `ActorRef`, e.g. `"/user/myservice"`.
   */
  def registerService(actor: ActorRef): Unit =
    pubSubMediator ! DistributedPubSubMediator.Put(actor)

  /**
   * A registered actor will be automatically unregistered when terminated,
   * but it can also be explicitly unregistered before termination.
   */
  def unregisterService(actor: ActorRef): Unit =
    pubSubMediator ! DistributedPubSubMediator.Remove(actor.path.toStringWithoutAddress)

  /**
   * Register an actor that should be reachable for the clients to a named topic.
   * Several actors can be registered to the same topic name, and all will receive
   * published messages.
   * The client can publish messages to this topic with `Publish`.
   */
  def registerSubscriber(topic: String, actor: ActorRef): Unit =
    pubSubMediator ! DistributedPubSubMediator.Subscribe(topic, actor)

  /**
   * A registered subscriber will be automatically unregistered when terminated,
   * but it can also be explicitly unregistered before termination.
   */
  def unregisterSubscriber(topic: String, actor: ActorRef): Unit =
    pubSubMediator ! DistributedPubSubMediator.Unsubscribe(topic, actor)

  private val server: Future[Http.ServerBinding] = {
    log.info("Starting ClusterClientReceptionist gRPC server at {}", settings.hostPort)

    implicit val sys: ActorSystem = system
    implicit val materializer: Materializer = Materializer(sys)

    val serialization = new ClusterClientSerialization(system)

    val service: HttpRequest => Future[HttpResponse] =
      ClusterClientReceptionistServiceHandler(
        new ClusterClientReceptionistGrpcImpl(settings, pubSubMediator, serialization)(materializer, log))

    Http().bindAndHandleAsync(
      service,
      interface = settings.hostPort.hostname,
      settings.hostPort.port,
      connectionContext = HttpConnectionContext())
  }

  server.onComplete { result =>
    log.info("ClusterClientReceptionist gRPC server stopped: {}", result)
  }(system.dispatcher)

}

object ClusterReceptionistSettings {

  /**
   * Create settings from the default configuration
   * `sample.cluster.client.grpc.receptionist`.
   */
  def apply(system: ActorSystem): ClusterReceptionistSettings = {
    val config = system.settings.config.getConfig("sample.cluster.client.grpc.receptionist")
    new ClusterReceptionistSettings(
      hostPort = HostPort(config.getString("canonical.hostname"), config.getInt("canonical.port")),
      role = roleOption(config.getString("role")),
      bufferSize = config.getInt("buffer-size"),
      askSendTimeout = config.getDuration("ask-send-timeout", TimeUnit.MILLISECONDS).millis)
  }

  private def roleOption(role: String): Option[String] =
    if (role == "") None else Option(role)

}

final case class ClusterReceptionistSettings(
    hostPort: HostPort,
    role: Option[String],
    bufferSize: Int,
    askSendTimeout: FiniteDuration)

object HostPort {
  def fromString(s: String): HostPort = {
    val parts = s.split(":")
    HostPort(parts(0), parts(1).toInt)
  }
}

final case class HostPort(hostname: String, port: Int) {
  override def toString: String = s"$hostname:$port"
}
