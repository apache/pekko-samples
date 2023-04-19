package com.example;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.cluster.ClusterEvent;
import akka.cluster.typed.Cluster;
import akka.cluster.typed.Subscribe;

public final class ClusterListener extends AbstractBehavior<ClusterEvent.ClusterDomainEvent> {

  public static Behavior<ClusterEvent.ClusterDomainEvent> create() {
    return Behaviors.setup(ClusterListener::new);
  }

  private ClusterListener(ActorContext<ClusterEvent.ClusterDomainEvent> context) {
    super(context);
    context.getLog().debug("starting up cluster listener...");
    final Cluster cluster = Cluster.get(context.getSystem());
    cluster.subscriptions().tell(Subscribe.create(context.getSelf(), ClusterEvent.ClusterDomainEvent.class));
  }

  @Override
  public Receive<ClusterEvent.ClusterDomainEvent> createReceive() {
    return newReceiveBuilder()
        .onMessage(ClusterEvent.MemberUp.class, event -> {
          getContext().getLog().info("Member is Up: {}", event.member().address());
          return this;
        }).onMessage(ClusterEvent.UnreachableMember.class, event -> {
          getContext().getLog().info("Member detected as unreachable: {}", event.member().address());
          return this;
        }).onMessage(ClusterEvent.MemberRemoved.class, event -> {
          getContext().getLog().info("Member is Removed: {} after {}", event.member().address(), event.previousStatus());
          return this;
        }).onMessage(ClusterEvent.MemberRemoved.class, event -> {
          getContext().getLog().info("Member Event: " + event.toString());
          return this;
        }).build();
  }
}
