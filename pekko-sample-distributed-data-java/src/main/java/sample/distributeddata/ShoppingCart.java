package sample.distributeddata;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

import org.apache.pekko.actor.typed.ActorRef;
import org.apache.pekko.actor.typed.Behavior;
import org.apache.pekko.actor.typed.javadsl.ActorContext;
import org.apache.pekko.actor.typed.javadsl.Behaviors;
import org.apache.pekko.cluster.ddata.Key;
import org.apache.pekko.cluster.ddata.LWWMap;
import org.apache.pekko.cluster.ddata.LWWMapKey;
import org.apache.pekko.cluster.ddata.ReplicatedData;
import org.apache.pekko.cluster.ddata.SelfUniqueAddress;
import org.apache.pekko.cluster.ddata.typed.javadsl.DistributedData;
import org.apache.pekko.cluster.ddata.typed.javadsl.Replicator.GetFailure;
import org.apache.pekko.cluster.ddata.typed.javadsl.Replicator.GetResponse;
import org.apache.pekko.cluster.ddata.typed.javadsl.Replicator.GetSuccess;
import org.apache.pekko.cluster.ddata.typed.javadsl.Replicator.NotFound;
import org.apache.pekko.cluster.ddata.typed.javadsl.Replicator.ReadConsistency;
import org.apache.pekko.cluster.ddata.typed.javadsl.Replicator.ReadMajority;
import org.apache.pekko.cluster.ddata.typed.javadsl.Replicator.Update;
import org.apache.pekko.cluster.ddata.typed.javadsl.Replicator.UpdateFailure;
import org.apache.pekko.cluster.ddata.typed.javadsl.Replicator.UpdateResponse;
import org.apache.pekko.cluster.ddata.typed.javadsl.Replicator.UpdateSuccess;
import org.apache.pekko.cluster.ddata.typed.javadsl.Replicator.UpdateTimeout;
import org.apache.pekko.cluster.ddata.typed.javadsl.Replicator.WriteConsistency;
import org.apache.pekko.cluster.ddata.typed.javadsl.Replicator.WriteMajority;
import org.apache.pekko.cluster.ddata.typed.javadsl.Replicator.Get;
import org.apache.pekko.cluster.ddata.typed.javadsl.Replicator;
import org.apache.pekko.cluster.ddata.typed.javadsl.ReplicatorMessageAdapter;

public class ShoppingCart {

  //#read-write-majority
  private final WriteConsistency writeMajority = 
      new WriteMajority(Duration.ofSeconds(3));
  private final static ReadConsistency readMajority = 
      new ReadMajority(Duration.ofSeconds(3));
  //#read-write-majority

  public interface Command {}

  public record GetCart(ActorRef<Cart> replyTo) implements Command {}

  public record AddItem(LineItem item) implements Command {}

  public record RemoveItem(String productId) implements Command {}

  public record Cart(Set<LineItem> items) {}

  public record LineItem(String productId, String title, int quantity) {
    @Override
    public String toString() {
      return "LineItem [productId=" + productId + ", title=" + title + ", quantity=" + quantity + "]";
    }
  }

  private interface InternalCommand extends Command {}

  private record InternalGetResponse(GetResponse<LWWMap<String, LineItem>> rsp, ActorRef<Cart> replyTo)
      implements InternalCommand {}

  private record InternalUpdateResponse<A extends ReplicatedData>(UpdateResponse<A> rsp)
      implements InternalCommand {}

  private record InternalRemoveItem(String productId, GetResponse<LWWMap<String, LineItem>> rsp)
      implements InternalCommand {}

  public static Behavior<Command> create(String userId) {
    return Behaviors.setup(context ->
        DistributedData.withReplicatorMessageAdapter(
            (ReplicatorMessageAdapter<Command, LWWMap<String, LineItem>> replicator) ->
                new ShoppingCart(context, replicator, userId).createBehavior()));
  }

  private final ReplicatorMessageAdapter<Command, LWWMap<String, LineItem>> replicator;
  private final Key<LWWMap<String, LineItem>> dataKey;
  private final SelfUniqueAddress node;

  public ShoppingCart(
      ActorContext<Command> context,
      ReplicatorMessageAdapter<Command, LWWMap<String, LineItem>> replicator,
      String userId
  ) {
    this.replicator = replicator;
    this.dataKey = LWWMapKey.create("cart-" + userId);
    node = DistributedData.get(context.getSystem()).selfUniqueAddress();
  }

  public Behavior<Command> createBehavior() {
    return Behaviors
        .receive(Command.class)
        .onMessage(GetCart.class, this::onGetCart)
        .onMessage(InternalGetResponse.class, this::onInternalGetResponse)
        .onMessage(AddItem.class, this::onAddItem)
        .onMessage(RemoveItem.class, this::onRemoveItem)
        .onMessage(InternalRemoveItem.class, this::onInternalRemoveItem)
        .onMessage(InternalUpdateResponse.class, this::onInternalUpdateResponse)
        .build();
  }

  //#get-cart
  private Behavior<Command> onGetCart(GetCart command) {
    replicator.askGet(
        askReplyTo -> new Get<>(dataKey, readMajority, askReplyTo),
        rsp -> new InternalGetResponse(rsp, command.replyTo()));

    return Behaviors.same();
  }

  private Behavior<Command> onInternalGetResponse(InternalGetResponse msg) {
    if (msg.rsp() instanceof GetSuccess<LWWMap<String, LineItem>> success) {
      LWWMap<String, LineItem> data = success.get(dataKey);
      msg.replyTo().tell(new Cart(new HashSet<>(data.getEntries().values())));
    } else if (msg.rsp() instanceof NotFound) {
      msg.replyTo().tell(new Cart(new HashSet<>()));
    } else if (msg.rsp() instanceof GetFailure) {
      // ReadMajority failure, try again with local read
      replicator.askGet(
          askReplyTo -> new Get<>(dataKey, Replicator.readLocal(), askReplyTo),
          rsp -> new InternalGetResponse(rsp, msg.replyTo())
      );
    }
    return Behaviors.same();
  }
  //#get-cart

  //#add-item
  private Behavior<Command> onAddItem(AddItem command) {
    replicator.askUpdate(
        askReplyTo ->
            new Update<>(
                dataKey,
                LWWMap.empty(),
                writeMajority,
                askReplyTo,
                cart -> updateCart(cart, command.item())
            ),
        InternalUpdateResponse::new);

    return Behaviors.same();
  }

  //#add-item

  private LWWMap<String, LineItem> updateCart(LWWMap<String, LineItem> data, LineItem item) {
    if (data.contains(item.productId())) {
      LineItem existingItem = data.get(item.productId()).get();
      int newQuantity = existingItem.quantity() + item.quantity();
      LineItem newItem = new LineItem(item.productId(), item.title(), newQuantity);
      return data.put(node, item.productId(), newItem);
    } else {
      return data.put(node, item.productId(), item);
    }
  }

  //#remove-item
  private Behavior<Command> onRemoveItem(RemoveItem command) {
    // Try to fetch latest from a majority of nodes first, since ORMap
    // remove must have seen the item to be able to remove it.
    replicator.askGet(
        askReplyTo -> new Get<>(dataKey, readMajority, askReplyTo),
        rsp -> new InternalRemoveItem(command.productId(), rsp));

    return Behaviors.same();
  }

  private Behavior<Command> onInternalRemoveItem(InternalRemoveItem msg) {
    if (msg.rsp() instanceof GetSuccess) {
      removeItem(msg.productId());
    } else if (msg.rsp() instanceof NotFound) {
      /* nothing to remove */
    } else if (msg.rsp() instanceof GetFailure) {
      // ReadMajority failed, fall back to best effort local value
      removeItem(msg.productId());
    }
    return Behaviors.same();
  }

  private void removeItem(String productId) {
    replicator.askUpdate(
        askReplyTo ->
            new Update<>(
                dataKey,
                LWWMap.empty(),
                writeMajority,
                askReplyTo,
                cart -> cart.remove(node, productId)
            ),
        InternalUpdateResponse::new);
  }
  //#remove-item

  private Behavior<Command> onInternalUpdateResponse(InternalUpdateResponse<?> msg) {
    if (msg.rsp() instanceof UpdateSuccess) {
      // ok
    } else if (msg.rsp() instanceof UpdateTimeout) {
      // will eventually be replicated
    } else if (msg.rsp() instanceof UpdateFailure) {
      throw new IllegalStateException("Unexpected failure: " + msg.rsp());
    }
    return Behaviors.same();
  }
}
