package sample.persistence.res;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.apache.pekko.actor.typed.ActorSystem;
import org.apache.pekko.actor.typed.internal.adapter.ActorSystemAdapter;
import org.apache.pekko.actor.typed.javadsl.Behaviors;
import org.apache.pekko.cluster.sharding.typed.ReplicatedSharding;
import org.apache.pekko.cluster.sharding.typed.ReplicatedShardingExtension;
import org.apache.pekko.cluster.typed.Cluster;
import org.apache.pekko.management.javadsl.PekkoManagement;
import org.apache.pekko.persistence.cassandra.testkit.CassandraLauncher;
import org.apache.pekko.persistence.typed.ReplicaId;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import sample.persistence.res.counter.ThumbsUpCounter;
import sample.persistence.res.counter.ThumbsUpHttp;

public class MainApp {

  public static Set<ReplicaId> ALL_REPLICAS =
          Collections.unmodifiableSet(new HashSet<>(Arrays.asList(new ReplicaId("eu-west"), new ReplicaId("eu-central"))));

  public static void main(String[] args) {
    if (args.length == 0) {
      startClusterInSameJvm();
    } else if (args[0].equals("cassandra")) {
      startCassandraDatabase();
      System.out.println("Started Apache Cassandra, press Ctrl + C to kill");
      try {
        new CountDownLatch(1).await();
      } catch (InterruptedException e) {}
    } else {
      int port = Integer.parseInt(args[0]);
      String dc;
      if (args.length > 1)
        dc = args[1];
      else
        dc = "eu-west";

      startNode(port, dc);
    }
  }

  private static void startClusterInSameJvm() {
    startCassandraDatabase();

    startNode(7345, "eu-west");
    startNode(7355, "eu-central");
  }

  private static void startNode(int port, String dc) {

    ActorSystem<?> system = ActorSystem.create(Behaviors.empty(), "ClusterSystem", config(port, dc));
    Cluster cluster = Cluster.get(system);

    ReplicatedSharding<ThumbsUpCounter.Command> replicatedSharding = ReplicatedShardingExtension.get(system).init(ThumbsUpCounter.provider());

    if (port != 0) {
      ThumbsUpHttp.startServer(system, "0.0.0.0", 20000 + port, new ReplicaId(cluster.selfMember().dataCenter()), replicatedSharding);
      PekkoManagement.get(ActorSystemAdapter.toClassic(system)).start();
    }
  }

  private static Config config(int port, String dc) {
    return ConfigFactory.parseString(
      "pekko.remote.artery.canonical.port = " + port + "\n" +
          "pekko.management.http.port = 1" + port + "\n" +
          "pekko.cluster.multi-data-center.self-data-center = " + dc + "\n")
      .withFallback(ConfigFactory.load("application.conf"));
  }

  /**
   * To make the sample easier to run we kickstart an Apache Cassandra instance to
   * act as the journal. Apache Cassandra is a great choice of backend for Apache Pekko Persistence but
   * in a real application a pre-existing Cassandra cluster should be used.
   */
  private static void startCassandraDatabase() {
    File databaseDirectory = new File("target/cassandra-db");
    CassandraLauncher.start(
      databaseDirectory,
      CassandraLauncher.DefaultTestConfigResource(),
      false,
      9042);
  }

}
