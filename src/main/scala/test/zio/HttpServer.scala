package test.zio

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import test.zio.common.Dependencies.programDependencies
import test.zio.domain.model.AkkaDependencies
import test.zio.routes.UserRoutes
import zio.console.Console
import zio.{Has, Task, ZIO, _}

import scala.concurrent.Future

object HttpServer extends App {

  val bindTask: AkkaDependencies => Task[Future[Http.ServerBinding]] = { akkaDep =>
    implicit val sys: ActorSystem = akkaDep.system
    implicit val mat: ActorMaterializer = akkaDep.mat
    Task(Http().bindAndHandle(new UserRoutes().getPaths, "localhost", 8080))
  }

  val program: ZIO[Console, Throwable, Future[Http.ServerBinding]] =
    (for {
        system <- ZIO.access[Has[ActorSystem]](_.get)
        b <- bindTask(AkkaDependencies(system))
      } yield b
      ).provideLayer(programDependencies)

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = program.fold(_ => 1, _ => 0)

}
