package test.zio.domain

import akka.actor.ActorSystem
import test.zio.domain.ProgramEnv.Service
import zio.Task

trait ProgramEnv {
  def dependencies: ProgramEnv.Service
}

object ProgramEnv {
  trait Service {
    def getSystem: Task[ActorSystem]
  }
}

trait ProgramEnvLive extends ProgramEnv {
  private val system = ActorSystem("ZIO"+System.currentTimeMillis())

  val dependencies = new Service {
    override def getSystem: Task[ActorSystem] = Task(system)
  }
}

object ProgramEnvLive extends ProgramEnvLive



