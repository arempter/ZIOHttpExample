package test.zio.common

import akka.actor.ActorSystem
import test.zio.domain.{Logging, ManagedActorSystem}
import test.zio.domain.Logging.Logging
import zio.{Has, Runtime, ZLayer, clock}
import zio.console.Console

object Dependencies {
  val programDependencies: ZLayer[Console, Throwable, clock.Clock with Logging with Has[ActorSystem]]  =
    Logging.consoleLogger ++ clock.Clock.live ++ ManagedActorSystem.actorSystem

  val runtime = Runtime.default
}
