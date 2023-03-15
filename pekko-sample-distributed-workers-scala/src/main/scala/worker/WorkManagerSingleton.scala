package worker

import org.apache.pekko.actor.typed.ActorRef
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.cluster.typed.ClusterSingleton

import scala.concurrent.duration._
import org.apache.pekko.cluster.typed._
import worker.WorkManager.Command

object WorkManagerSingleton {

  private val singletonName = "work-manager"
  private val singletonRole = "back-end"

  def init(system: ActorSystem[_]): ActorRef[Command] = {
    val workTimeout = system.settings.config.getDuration("distributed-workers.work-timeout").getSeconds.seconds

    ClusterSingleton(system).init(
      SingletonActor(WorkManager(workTimeout), singletonName)
        .withSettings(ClusterSingletonSettings(system).withRole(singletonRole)))
  }
}
