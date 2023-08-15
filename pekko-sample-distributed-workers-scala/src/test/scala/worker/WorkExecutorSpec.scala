package worker

import org.apache.pekko
import pekko.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import org.scalatest.wordspec.AnyWordSpecLike

class WorkExecutorSpec extends ScalaTestWithActorTestKit with AnyWordSpecLike {

  import WorkExecutor._

  "WorkExecutor" must {
    "correctly compute the square of a number" in {
      val replyProbe = createTestProbe[Worker.WorkComplete]()
      val workExecutor = spawn(WorkExecutor())

      workExecutor ! ExecuteWork(3, replyProbe.ref)
      val response = replyProbe.receiveMessage()

      response.result shouldBe "3 * 3 = 9"
    }
  }
}