package com.example;

import akka.actor.typed.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ClusteringApp {

  public static void main(String[] args) {
    Config config = ConfigFactory.load();
    String clusterName = config.getString("clustering.cluster.name");

    ActorSystem<?> system = ActorSystem.create(ClusterListener.create(), clusterName);
  }

}
