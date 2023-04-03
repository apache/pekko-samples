package sample.killrweather;

import org.apache.pekko.actor.typed.Behavior;
import org.apache.pekko.actor.typed.javadsl.Behaviors;

/**
 * Root actor bootstrapping the application
 */
final class Guardian {

  public static Behavior<Void> create(int httpPort) {
    return Behaviors.setup(context -> {
      WeatherStation.initSharding(context.getSystem());

      WeatherRoutes routes = new WeatherRoutes(context.getSystem());
      WeatherHttpServer.start(routes.weather(), httpPort, context.getSystem());

      return Behaviors.empty();
    });
  }
}
