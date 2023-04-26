/*
 * Copyright (C) 2017 Lightbend Inc. <http://www.lightbend.com>
 */
package org.apache.pekko.cluster.bootstrap.demo;

import org.apache.pekko.actor.typed.ActorRef;
import org.apache.pekko.actor.typed.ActorSystem;
import org.apache.pekko.actor.typed.Behavior;
import org.apache.pekko.actor.typed.javadsl.Adapter;
import org.apache.pekko.actor.typed.javadsl.Behaviors;
import org.apache.pekko.cluster.ClusterEvent;
import org.apache.pekko.cluster.typed.Cluster;
import org.apache.pekko.cluster.typed.Subscribe;
import org.apache.pekko.http.javadsl.ConnectHttp;
import org.apache.pekko.http.javadsl.Http;
import static org.apache.pekko.http.javadsl.server.Directives.*;
import org.apache.pekko.management.cluster.bootstrap.ClusterBootstrap;
import org.apache.pekko.management.scaladsl.PekkoManagement;
import org.apache.pekko.stream.Materializer;

public class DemoApp {

  static class MemberEventLogger {
    public static Behavior<ClusterEvent.MemberEvent> create() {
      return Behaviors.setup(context -> {
        Cluster cluster = Cluster.get(context.getSystem());

        context.getLog().info("Started [{}], cluster.selfAddress = {})",
                context.getSystem(),
                cluster.selfMember().address());

        cluster.subscriptions().tell(new Subscribe<>(context.getSelf(), ClusterEvent.MemberEvent.class));

        return Behaviors.receiveMessage(event -> {
          context.getLog().info("MemberEvent: {}", event);
          return Behaviors.same();
        });
      });
    }
  }

  static class Guardian {
    public static Behavior<Void> create() {
      return Behaviors.setup(context -> {
        final org.apache.pekko.actor.ActorSystem classicSystem = Adapter.toClassic(context.getSystem());
        Materializer mat = Materializer.matFromSystem(classicSystem);

        Http.get(classicSystem).bindAndHandle(complete("Hello world")
                .flow(classicSystem, mat), ConnectHttp.toHost("0.0.0.0", 8080), mat)
                .whenComplete((binding, failure) -> {
                  if (failure == null) {
                    classicSystem.log().info("HTTP server now listening at port 8080");
                  } else {
                    classicSystem.log().error(failure, "Failed to bind HTTP server, terminating.");
                    classicSystem.terminate();
                  }
                });

        context.spawn(MemberEventLogger.create(), "listener");

        PekkoManagement.get(classicSystem).start();
        ClusterBootstrap.get(classicSystem).start();

        return Behaviors.empty();
      });
    }
  }

  public static void main(String[] args) {
    ActorSystem.create(Guardian.create(), "Appka");
  }

}