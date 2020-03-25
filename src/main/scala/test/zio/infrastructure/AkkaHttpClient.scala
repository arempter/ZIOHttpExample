package test.zio.infrastructure

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import test.zio.common.Dependencies.{programDependencies, runtime}
import test.zio.domain.Logging.Logging
import test.zio.domain.{HttpClient, Logging}
import zio.duration._
import zio._

object AkkaHttpClient extends HttpClient.Service {

  private def system: ZIO[Has[ActorSystem], Throwable, ActorSystem] =
    for {
      as <- ZIO.access[Has[ActorSystem]](_.get)
    } yield as

  def httpTask(request: HttpRequest): ZIO[Logging with clock.Clock with Has[ActorSystem], Throwable, HttpResponse] = {
    for {
      as <- system
      resp <- Task.fromFuture(_ => Http()(as).singleRequest(request))
        .flatMap {
          case r if r.status == StatusCodes.OK =>
            IO.succeed(r)
          case _ => IO.fail("Request failing, thread: " + Thread.currentThread().getName)
        }
        .tapError(err => Logging.error(err.toString))
        .retry(Schedule.recurs(3) && Schedule.exponential(10.milliseconds))
        .timeoutFail("Timeout occurred, interrupted")(5.seconds)
        .catchAll(e => IO.succeed(HttpResponse(entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`, s"to many tries, $e \n"))))
    } yield resp
  }

  def executeRequest(request: HttpRequest): HttpResponse =
    runtime.unsafeRun(httpTask(request).provideLayer(programDependencies))


}
