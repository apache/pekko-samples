package sample;

import org.apache.pekko.NotUsed;
import org.apache.pekko.actor.typed.ActorRef;
import org.apache.pekko.actor.typed.ActorSystem;
import org.apache.pekko.actor.typed.Behavior;
import org.apache.pekko.actor.typed.javadsl.Behaviors;
import org.apache.pekko.actor.typed.javadsl.ActorContext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DiningHakkers {

   public static void main(String[] args) {
      ActorSystem.create(DiningHakkers.create(), "DiningHakkers");
   }

   private static Behavior<NotUsed> create() {
      return Behaviors.setup(context -> new DiningHakkers(context).behavior());
   }

   private final ActorContext<NotUsed> context;

   private DiningHakkers(ActorContext<NotUsed> context) {
      this.context = context;
   }

   private Behavior<NotUsed> behavior() {
      //Create 5 chopsticks
      List<ActorRef<Chopstick.Command>> chopsticks = IntStream.rangeClosed(1, 5)
         .mapToObj(i -> context.spawn(Chopstick.create(), "Chopstick" + i))
         .collect(Collectors.toList());

      //Create 5 hakkers and assign them their left and right chopstick
      List<String> names = Arrays.asList("Ghosh", "Boner", "Klang", "Krasser", "Manie");
      IntStream.range(0, names.size())
         .mapToObj(i ->
            context.spawn(Hakker.create(names.get(i), chopsticks.get(i), chopsticks.get((i + 1) % 5)), names.get(i))
         )
         //Signal all hakkers that they should start thinking, and watch the show
         .forEach(hakker -> hakker.tell(Hakker.Think.INSTANCE));
      return Behaviors.empty();
   }
}
