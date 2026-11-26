package sample.killrweather.fog;

import org.apache.pekko.actor.typed.ActorSystem;

import java.util.Arrays;
import java.util.List;

/**
 * In another terminal start the `Fog` (see Fog computing https://en.wikipedia.org/wiki/Fog_computing).
 * Starts the fog network, simulating devices and stations.
 * In the wild, each station would run its own system and be location-aware.
 */
public class Fog {

  public static void main(String[] args) {
    final List<Integer> weatherApiPorts;
    if (args.length == 0) {
      weatherApiPorts = List.of(12553, 12554);
    } else {
      weatherApiPorts = Arrays.stream(args).map(Integer::parseInt).toList();
    }

    ActorSystem.create(Guardian.create(weatherApiPorts), "Fog");
  }
}
