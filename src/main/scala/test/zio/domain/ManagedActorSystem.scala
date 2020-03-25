package test.zio.domain

import akka.actor.ActorSystem
import zio.{Has, Managed, Task, ZLayer}

object ManagedActorSystem {

//  private def terminateTask(sys: ActorSystem) = Task.fromFuture { _ =>
//    sys.terminate()
//  }.ignore

  val actorSystem: ZLayer[Any, Throwable, Has[ActorSystem]] =
    ZLayer.fromManaged(Managed.fromEffect(Task(ActorSystem("TestZIO-" + System.currentTimeMillis()))))

}
