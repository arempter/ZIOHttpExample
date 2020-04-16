package test.zio.common

import test.zio.domain.HttpClient.HttpClient
import test.zio.domain.Logging.Logging
import test.zio.domain.{ActorEnv, ActorEnvLive, HttpClient, Logging}
import zio.clock.Clock
import zio.{Has, ZLayer, clock, console}

object ProgramEnv {

  type ProgramEnv = HttpClient with Logging with Clock with Has[ActorEnv.Service]

  val actorEnvLive = ZLayer.succeed[ActorEnv.Service](ActorEnvLive.dependencies)

  val live =
    clock.Clock.live ++ (console.Console.live >>> Logging.live) ++ actorEnvLive ++ HttpClient.live
}
