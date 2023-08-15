package worker

import org.apache.pekko
import org.apache.pekko.actor.typed.delivery.ConsumerController
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import pekko.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import org.scalatest.wordspec.AnyWordSpecLike

class WorkerSpec extends ScalaTestWithActorTestKit with AnyWordSpecLike{

  "A Worker" must {

    "start work when in idle state" in {
      val workExecutorProbe = createTestProbe[WorkExecutor.ExecuteWork]()
      val worker = spawn(Worker(workExecutorFactory = () => Behaviors.monitor(workExecutorProbe.ref, WorkExecutor())))
      val work = WorkManager.DoWork(Work("TestWork", 1000))
      val deliveryProbe = createTestProbe[ConsumerController.Confirmed]()
      val deliveredMessage = Worker.DeliveredMessage(deliveryProbe.ref, work, 1)

      worker ! deliveredMessage

      workExecutorProbe.expectMessageType[WorkExecutor.ExecuteWork]
    }

    "confirm the work when work is complete" in {
      val workExecutorProbe = createTestProbe[WorkExecutor.ExecuteWork]()
      val worker = spawn(Worker(workExecutorFactory = () => Behaviors.monitor(workExecutorProbe.ref, WorkExecutor())))
      val work = WorkManager.DoWork(Work("TestWork", 1000))

      val deliveryProbe = createTestProbe[ConsumerController.Confirmed]()
      val deliveredMessage = Worker.DeliveredMessage(deliveryProbe.ref, work, 1)

      worker ! deliveredMessage
      worker ! Worker.WorkComplete("Successful result")

      deliveryProbe.expectMessageType[ConsumerController.Confirmed]
    }
  }
}