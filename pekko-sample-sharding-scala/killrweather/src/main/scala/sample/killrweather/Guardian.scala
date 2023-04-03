package sample.killrweather

import org.apache.pekko.actor.typed.Behavior
import org.apache.pekko.actor.typed.scaladsl.Behaviors

/**
 * Root actor bootstrapping the application
 */
object Guardian {

  def apply(httpPort: Int): Behavior[Nothing] = Behaviors.setup[Nothing] { context =>
    WeatherStation.initSharding(context.system)

    val routes = new WeatherRoutes(context.system)
    WeatherHttpServer.start(routes.weather, httpPort, context.system)

    Behaviors.empty
  }

}
