package sample.killrweather;

import org.apache.pekko.actor.CoordinatedShutdown;
import org.apache.pekko.actor.typed.ActorSystem;
import org.apache.pekko.actor.typed.javadsl.Adapter;
import org.apache.pekko.http.javadsl.Http;
import org.apache.pekko.http.javadsl.server.Route;

import java.net.InetSocketAddress;
import java.time.Duration;

import static org.apache.pekko.Done.done;

final class WeatherHttpServer {

  public static void start(Route routes, int port, ActorSystem<?> system) {
    org.apache.pekko.actor.ActorSystem classicActorSystem = Adapter.toClassic(system);

    Http.get(classicActorSystem).newServerAt("localhost", port).bind(routes)
    .whenComplete((binding, failure) -> {
      if (failure == null) {
        final InetSocketAddress address = binding.localAddress();
        system.log().info(
            "WeatherServer online at http://{}:{}/",
            address.getHostString(),
            address.getPort());

        CoordinatedShutdown.get(classicActorSystem).addTask(
            CoordinatedShutdown.PhaseServiceRequestsDone(),
            "http-graceful-terminate",
            () ->
              binding.terminate(Duration.ofSeconds(10)).thenApply(terminated -> {
                system.log().info( "WeatherServer http://{}:{}/ graceful shutdown completed",
                    address.getHostString(),
                    address.getPort()
                );
                return done();
              })
        );
      } else {
        system.log().error("Failed to bind HTTP endpoint, terminating system", failure);
        system.terminate();
      }
    });
  }
}
