package test.zio

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.ActorMaterializer
import test.zio.common.Dependencies.{ programDependencies, runtime }
import test.zio.domain.HttpClient
import test.zio.domain.model.AkkaDependencies
import test.zio.routes.ApiRoutes
import zio.console.Console
import zio.{Has, Task, ZIO, _}

import scala.concurrent.Future

object HttpServer extends App with ApiRoutes {

  val bindTask: AkkaDependencies => Task[Future[Http.ServerBinding]] = { akkaDep =>
    implicit val sys: ActorSystem = akkaDep.system
    implicit val mat: ActorMaterializer = akkaDep.mat
    Task(Http().bindAndHandle(getPaths, "localhost", 8080))
  }

  // check if can be reduced
  val programDeps = programDependencies >>> HttpClient.httpClientImpl

  override protected def executeRequestF(request: HttpRequest): HttpResponse =
    runtime.unsafeRun(HttpClient.executeRequest(request).provideLayer(programDeps))

  val program: ZIO[Console, Throwable, Future[Http.ServerBinding]] =
    (for {
        system <- ZIO.access[Has[ActorSystem]](_.get)
        b <- bindTask(AkkaDependencies(system))
      } yield b
      ).provideLayer(programDependencies)

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = program.fold(_ => 1, _ => 0)

}
