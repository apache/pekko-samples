package sample.cluster.client.grpc;

import org.apache.pekko.actor.AbstractExtensionId;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.actor.ExtendedActorSystem;
import org.apache.pekko.actor.Extension;
import org.apache.pekko.actor.ExtensionIdProvider;
import org.apache.pekko.cluster.Cluster;
import org.apache.pekko.cluster.pubsub.DistributedPubSub;
import org.apache.pekko.cluster.pubsub.DistributedPubSubMediator;
import org.apache.pekko.event.Logging;
import org.apache.pekko.event.LoggingAdapter;
import org.apache.pekko.http.javadsl.ConnectHttp;
import org.apache.pekko.http.javadsl.Http;
import org.apache.pekko.http.javadsl.ServerBinding;
import org.apache.pekko.http.javadsl.model.HttpRequest;
import org.apache.pekko.http.javadsl.model.HttpResponse;
import org.apache.pekko.japi.function.Function;
import org.apache.pekko.stream.Materializer;
import org.apache.pekko.stream.SystemMaterializer;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

class ClusterClientReceptionistExtension extends AbstractExtensionId<ClusterClientReceptionist>
        implements ExtensionIdProvider {
    public static final ClusterClientReceptionistExtension INSTANCE = new ClusterClientReceptionistExtension();

    private ClusterClientReceptionistExtension() {
    }

    public ClusterClientReceptionistExtension lookup() {
        return ClusterClientReceptionistExtension.INSTANCE;
    }

    public ClusterClientReceptionist createExtension(ExtendedActorSystem system) {
        return new ClusterClientReceptionist(system);
    }
}

/**
 * Extension that starts gRPC service and accompanying `org.apache.pekko.cluster.pubsub.DistributedPubSubMediator`
 * with settings defined in config section `sample.cluster.client.grpc.receptionist`.
 * The `org.apache.pekko.cluster.pubsub.DistributedPubSubMediator` is started by the `org.apache.pekko.cluster.pubsub.DistributedPubSub`
 * extension.
 */
final class ClusterClientReceptionist implements Extension {

    public static ClusterClientReceptionist get(ActorSystem system) {
        return ClusterClientReceptionistExtension.INSTANCE.get(system);
    }

    private final ExtendedActorSystem system;
    private final LoggingAdapter log;
    public final ClusterReceptionistSettings settings;
    private final Optional<String> role;

    public ClusterClientReceptionist(ExtendedActorSystem system) {
        this.system = system;
        this.log = Logging.getLogger(system, getClass());
        this.settings = ClusterReceptionistSettings.create(system);
        this.role = settings.role;

        log.info("Starting ClusterClientReceptionist gRPC server at {}", settings.hostPort);

        ClusterClientSerialization serialization = new ClusterClientSerialization(system);

        Materializer materializer = SystemMaterializer.get(system).materializer();

        Function<HttpRequest, CompletionStage<HttpResponse>> service = ClusterClientReceptionistServiceHandlerFactory.create(
                new ClusterClientReceptionistGrpcImpl(settings, pubSubMediator(), serialization, materializer, log),
                system);
        CompletionStage<ServerBinding> bound =
                Http.get(system)
                        .newServerAt(settings.hostPort.hostname, settings.hostPort.port)
                        .bind(service)
                        .whenComplete((result, exc) -> {
                            if (exc == null)
                                log.info("ClusterClientReceptionist gRPC server stopped successfully");
                            else
                                log.info("ClusterClientReceptionist gRPC server stopped after failure: {}", exc);
                        });
    }

    /**
     * Returns true if this member is not tagged with the role configured for the
     * receptionist.
     */
    public boolean isTerminated() {
        return Cluster.get(system).isTerminated() ||
                (role.isPresent() && !Cluster.get(system).getSelfRoles().contains(role.get()));
    }

    /**
     * Register the actors that should be reachable for the clients in this `DistributedPubSubMediator`.
     */
    private ActorRef pubSubMediator() {
        return DistributedPubSub.get(system).mediator();
    }

    /**
     * Register an actor that should be reachable for the clients.
     * The clients can send messages to this actor with `Send` or `SendToAll` using
     * the path elements of the `ActorRef`, e.g. `"/user/myservice"`.
     */
    public void registerService(ActorRef actor) {
        pubSubMediator().tell(new DistributedPubSubMediator.Put(actor), ActorRef.noSender());
    }

    /**
     * A registered actor will be automatically unregistered when terminated,
     * but it can also be explicitly unregistered before termination.
     */
    public void unregisterService(ActorRef actor) {
        pubSubMediator().tell(new DistributedPubSubMediator.Remove(actor.path().toStringWithoutAddress()), ActorRef.noSender());
    }

    /**
     * Register an actor that should be reachable for the clients to a named topic.
     * Several actors can be registered to the same topic name, and all will receive
     * published messages.
     * The client can publish messages to this topic with `Publish`.
     */
    public void registerSubscriber(String topic, ActorRef actor) {
        pubSubMediator().tell(new DistributedPubSubMediator.Subscribe(topic, actor), ActorRef.noSender());
    }

    /**
     * A registered subscriber will be automatically unregistered when terminated,
     * but it can also be explicitly unregistered before termination.
     */
    public void unregisterSubscriber(String topic, ActorRef actor) {
        pubSubMediator().tell(new DistributedPubSubMediator.Unsubscribe(topic, actor), ActorRef.noSender());
    }

}

