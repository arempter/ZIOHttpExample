package test.zio

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import test.zio.domain.ManagedSystem.actorSystem
import test.zio.routes.UserRoutes
import zio.console.Console
import zio.{Has, Runtime, Task, ZIO, clock, _}

import scala.concurrent.Future

object HttpServer extends App {

  val bindTask: (ActorSystem, Runtime[Has[ActorSystem] with clock.Clock with Console]) => Task[Future[Http.ServerBinding]] = { (system, runtime) =>
    implicit val sys: ActorSystem = system
    implicit val mat: ActorMaterializer = ActorMaterializer()
    Task(Http().bindAndHandle(new UserRoutes(runtime).getPaths, "localhost", 8080))
  }

  val programDependencies: ZLayer[Any, Throwable, clock.Clock with Console with Has[ActorSystem]]  =
    Console.live ++ clock.Clock.live ++ actorSystem

  val program: ZIO[Any, Throwable, Future[Http.ServerBinding]] =
    (for {
        system <- ZIO.access[Has[ActorSystem]](_.get)
        runtime <- ZIO.runtime[Has[ActorSystem] with clock.Clock with Console]
        b <- bindTask(system, runtime)
      } yield b
      ).provideLayer(programDependencies)

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = program.fold(_ => 1, _ => 0)


}
