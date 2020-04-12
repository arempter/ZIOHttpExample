package test.zio.domain

import akka.actor.ActorSystem
import test.zio.domain.ProgramEnv.Service
import zio.Task

trait ProgramEnv {
  def dependencies: ProgramEnv.Service
}

object ProgramEnv {
  trait Service {
    def getActorSystem: Task[ActorSystem]
  }
}

trait ProgramEnvLive extends ProgramEnv {
  // once instance, but not description. If wrapped in task will be evaluated each time (call by name)
  private val system = ActorSystem("ZIO"+System.currentTimeMillis())

  val dependencies = new Service {
    override def getActorSystem: Task[ActorSystem] = Task(system)
  }
}

object ProgramEnvLive extends ProgramEnvLive



