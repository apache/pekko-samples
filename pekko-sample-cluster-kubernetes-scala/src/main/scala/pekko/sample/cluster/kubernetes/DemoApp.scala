package org.apache.pekko.sample.cluster.kubernetes

import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.cluster.ClusterEvent
import org.apache.pekko.cluster.typed.{ Cluster, Subscribe }
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.management.cluster.bootstrap.ClusterBootstrap
import org.apache.pekko.management.javadsl.PekkoManagement
import org.apache.pekko.{ actor => classic }

import scala.concurrent.ExecutionContext

object DemoApp extends App {

  ActorSystem[Nothing](Behaviors.setup[Nothing] { context =>
      import org.apache.pekko.actor.typed.scaladsl.adapter._
      implicit val classicSystem: classic.ActorSystem = context.system.toClassic
      implicit val ec: ExecutionContext = context.system.executionContext

      val cluster = Cluster(context.system)
      context.log.info("Started [" + context.system + "], cluster.selfAddress = " + cluster.selfMember.address + ")")

      Http().newServerAt("0.0.0.0", 8080).bind(complete("Hello world"))

      // Create an actor that handles cluster domain events
      val listener = context.spawn(Behaviors.receive[ClusterEvent.MemberEvent]((ctx, event) => {
          ctx.log.info("MemberEvent: {}", event)
          Behaviors.same
        }), "listener")

      Cluster(context.system).subscriptions ! Subscribe(listener, classOf[ClusterEvent.MemberEvent])

      PekkoManagement.get(classicSystem).start()
      ClusterBootstrap.get(classicSystem).start()
      Behaviors.empty
    }, "appka")
}
