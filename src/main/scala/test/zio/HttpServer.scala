package test.zio

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.ActorMaterializer
import test.zio.domain.model.AkkaDependencies
import test.zio.domain.{HttpClient, ManagedActorSystem, ProgramEnvLive}
import test.zio.routes.ApiRoutes
import zio.internal.Platform
import zio.{Task, ZIO, _}

import scala.concurrent.Future

object HttpServer extends App with ApiRoutes {

  private val runtime = Runtime(ProgramEnvLive, Platform.default)

  private val httpClientDependencies = HttpClient.live

  private val bindTask: AkkaDependencies => Task[Future[Http.ServerBinding]] = { akkaDep =>
    implicit val sys: ActorSystem = akkaDep.system
    implicit val mat: ActorMaterializer = akkaDep.mat
    Task(Http().bindAndHandle(getPaths, "localhost", 8080))
  }

  override protected def runRequest(request: HttpRequest): HttpResponse = {
    runtime.unsafeRun(
      HttpClient.executeRequest(request).provideLayer(httpClientDependencies)
    )
  }

  val program: ZIO[ZEnv, Throwable, Future[Http.ServerBinding]] =
    (for {
        system <- ZIO.access[Has[ActorSystem]](_.get)
        b <- bindTask(AkkaDependencies(system))
      } yield b).provideLayer(ManagedActorSystem.system)

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] =
    program.foldM(
      e => { console.putStrLn(s"Server failed to start ${e.getMessage}") *> IO.fail(throw e) },
      _ => { console.putStrLn("Server started...") *> IO.succeed(0) }
    )

}
