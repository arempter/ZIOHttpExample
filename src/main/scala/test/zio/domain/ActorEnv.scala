package test.zio.domain

import akka.actor.ActorSystem
import test.zio.domain.ActorEnv.Service
import zio.Task

trait ActorEnv {
  def dependencies: ActorEnv.Service
}

object ActorEnv {
  trait Service {
    def getActorSystem: Task[ActorSystem]
  }
}

trait ActorEnvLive extends ActorEnv {
  // once instance, but not description. If wrapped in task will be evaluated each time (call by name)
  private val system = ActorSystem("ZIO"+System.currentTimeMillis())

  val dependencies = new Service {
    override def getActorSystem: Task[ActorSystem] = Task(system)
  }
}

object ActorEnvLive extends ActorEnvLive



