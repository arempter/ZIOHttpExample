package test.zio.domain

import akka.actor.ActorSystem
import zio.{Has, Managed, Task, ZLayer}

object ManagedActorSystem {

  val actorSystem: ZLayer[Any, Throwable, Has[ActorSystem]] =
    ZLayer.fromManaged(Managed.fromEffect(Task(ActorSystem("TestZIO-" + System.currentTimeMillis()))))

  // not used with akka http
//    ZLayer.fromAcquireRelease(Task(ActorSystem("TestZIO"+ System.currentTimeMillis() )))(sys=>Task.fromFuture { _ => sys.terminate() }.ignore)


}
