package sample.distributeddata;

import static org.apache.pekko.cluster.ddata.typed.javadsl.Replicator.readLocal;
import static org.apache.pekko.cluster.ddata.typed.javadsl.Replicator.writeLocal;

import java.util.Optional;
import scala.Option;

import org.apache.pekko.actor.typed.ActorRef;
import org.apache.pekko.actor.typed.Behavior;
import org.apache.pekko.actor.typed.javadsl.ActorContext;
import org.apache.pekko.actor.typed.javadsl.Behaviors;
import org.apache.pekko.cluster.ddata.Key;
import org.apache.pekko.cluster.ddata.LWWMap;
import org.apache.pekko.cluster.ddata.LWWMapKey;
import org.apache.pekko.cluster.ddata.SelfUniqueAddress;
import org.apache.pekko.cluster.ddata.typed.javadsl.DistributedData;
import org.apache.pekko.cluster.ddata.typed.javadsl.Replicator.Get;
import org.apache.pekko.cluster.ddata.typed.javadsl.Replicator.GetResponse;
import org.apache.pekko.cluster.ddata.typed.javadsl.Replicator.GetSuccess;
import org.apache.pekko.cluster.ddata.typed.javadsl.Replicator.NotFound;
import org.apache.pekko.cluster.ddata.typed.javadsl.Replicator.Update;
import org.apache.pekko.cluster.ddata.typed.javadsl.Replicator.UpdateResponse;
import org.apache.pekko.cluster.ddata.typed.javadsl.ReplicatorMessageAdapter;

public class ReplicatedCache {

  public interface Command {}

  public record PutInCache(String key, String value) implements Command {}

  public record GetFromCache(String key, ActorRef<Cached> replyTo) implements Command {}

  public record Cached(String key, Optional<String> value) {
    @Override
    public String toString() {
      return "Cached [key=" + key + ", value=" + value + "]";
    }
  }

  public record Evict(String key) implements Command {}

  private interface InternalCommand extends Command {}

  private record InternalGetResponse(String key, ActorRef<Cached> replyTo, GetResponse<LWWMap<String, String>> rsp)
      implements InternalCommand {}

  private record InternalUpdateResponse(UpdateResponse<LWWMap<String, String>> rsp)
      implements InternalCommand {}

  public static Behavior<Command> create() {
    return Behaviors.setup(context ->
        DistributedData.withReplicatorMessageAdapter(
            (ReplicatorMessageAdapter<Command, LWWMap<String, String>> replicator) ->
                new ReplicatedCache(context, replicator).createBehavior()));
  }

  private final ReplicatorMessageAdapter<Command, LWWMap<String, String>> replicator;
  private final SelfUniqueAddress node;

  public ReplicatedCache(
      ActorContext<Command> context,
      ReplicatorMessageAdapter<Command, LWWMap<String, String>> replicator
  ) {
    this.replicator = replicator;
    node = DistributedData.get(context.getSystem()).selfUniqueAddress();
  }

  public Behavior<Command> createBehavior() {
    return Behaviors
        .receive(Command.class)
        .onMessage(PutInCache.class, cmd -> receivePutInCache(cmd.key(), cmd.value()))
        .onMessage(Evict.class, cmd -> receiveEvict(cmd.key()))
        .onMessage(GetFromCache.class, cmd -> receiveGetFromCache(cmd.key(), cmd.replyTo()))
        .onMessage(InternalGetResponse.class, this::onInternalGetResponse)
        .onMessage(InternalUpdateResponse.class, notUsed -> Behaviors.same())
        .build();
  }

  private Behavior<Command> receivePutInCache(String key, String value) {
    replicator.askUpdate(
        askReplyTo ->
            new Update<>(
                dataKey(key),
                LWWMap.empty(),
                writeLocal(),
                askReplyTo,
                curr -> curr.put(node, key, value)
            ),
        InternalUpdateResponse::new);

    return Behaviors.same();
  }

  private Behavior<Command> receiveEvict(String key) {
    replicator.askUpdate(
        askReplyTo ->
            new Update<>(
                dataKey(key),
                LWWMap.empty(),
                writeLocal(),
                askReplyTo,
                curr -> curr.remove(node, key)
            ),
        InternalUpdateResponse::new);

    return Behaviors.same();
  }

  private Behavior<Command> receiveGetFromCache(String key, ActorRef<Cached> replyTo) {
    replicator.askGet(
        askReplyTo -> new Get<>(dataKey(key), readLocal(), askReplyTo),
        rsp -> new InternalGetResponse(key, replyTo, rsp));

    return Behaviors.same();
  }

  private Behavior<Command> onInternalGetResponse(InternalGetResponse msg) {
    if (msg.rsp() instanceof GetSuccess<LWWMap<String, String>> success) {
      Option<String> valueOption = success.get(dataKey(msg.key())).get(msg.key());
      Optional<String> valueOptional = Optional.ofNullable(valueOption.isDefined() ? valueOption.get() : null);
      msg.replyTo().tell(new Cached(msg.key(), valueOptional));
    } else if (msg.rsp() instanceof NotFound) {
      msg.replyTo().tell(new Cached(msg.key(), Optional.empty()));
    }
    return Behaviors.same();
  }

  private Key<LWWMap<String, String>> dataKey(String entryKey) {
    return LWWMapKey.create("cache-" + Math.abs(entryKey.hashCode() % 100));
  }

}
