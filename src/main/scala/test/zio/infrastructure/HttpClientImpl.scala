package test.zio.infrastructure

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import test.zio.domain.{HttpClient, Logging, ActorEnv}
import zio._
import zio.duration._

case class HttpClientImpl() extends HttpClient.Service {

  def executeRequest(request: HttpRequest): ZIO[ActorEnv, Throwable, HttpResponse] =
      ZIO.accessM { env =>
        (for {
          system <- env.dependencies.getActorSystem
          response <- Task.fromFuture(_ =>
                        Http()(system).singleRequest(request))
                        .flatMap {
                          case r if r.status == StatusCodes.OK => IO.succeed(r)
                          case _ => IO.fail(s"Request failing, system: ${system.name}, thread: ${Thread.currentThread().getName}")
                        }
                          .tapError(err => Logging.error(err.toString))
                          .retry(Schedule.recurs(3) && Schedule.exponential(10.milliseconds))
                          .timeoutFail("Timeout occurred, interrupted")(3.seconds)
                          .catchAll(e => IO.succeed(HttpResponse(entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`, s"To many tries, $e \n")))
                        )
        } yield response
        ).provideLayer(clock.Clock.live ++ (console.Console.live >>> Logging.live)) // add dependencies locally
  }

  def executeManyRequests(requests: List[HttpRequest]): ZIO[ActorEnv, Throwable, List[HttpResponse]] =
    ZIO.foreachParN(10)(requests) { r =>
      ZIO.sleep(500.milliseconds).provideLayer(clock.Clock.live) *>
        executeRequest(r)
    }

}