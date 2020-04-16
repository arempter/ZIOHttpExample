package test.zio

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.ActorMaterializer
import test.zio.common.ProgramEnv
import test.zio.domain.{ActorEnv, ActorEnvLive, HttpClient}
import test.zio.routes.ApiRoutes
import zio.{Task, ZIO, _}

import scala.concurrent.Future

object HttpServer extends App with ApiRoutes {

  private val runtime = Runtime.default

  private val bindTask: ActorSystem => Task[Future[Http.ServerBinding]] = { system =>
    implicit val sys: ActorSystem = system
    implicit val mat: ActorMaterializer = ActorMaterializer()(system)
    Task(Http().bindAndHandle(getPaths, "localhost", 8080))
  }

  override protected def runRequest(request: HttpRequest): HttpResponse = {
    runtime.unsafeRun(
      HttpClient.executeRequest(request).provideLayer(ProgramEnv.live)
    )
  }

  override protected def runManyRequests(requests: List[HttpRequest]): List[HttpResponse] =
    runtime.unsafeRun(
      HttpClient.executeManyRequests(requests).provideLayer(ProgramEnv.live)
    )

  val program: ZIO[ZEnv, Throwable, Future[Http.ServerBinding]] =
    (for {
      system <- ZIO.accessM[ActorEnv](_.dependencies.getActorSystem)
      b <- bindTask(system)
     } yield b
    ).provide(ActorEnvLive)

  override def run(args: List[String]): ZIO[ZEnv, Nothing, Int] =
    program.foldM(
      e => { console.putStrLn(s"Server failed to start ${e.getMessage}") *> IO.fail(throw e) },
      _ => { console.putStrLn("Server started...") *> IO.succeed(0) }
    )

}
