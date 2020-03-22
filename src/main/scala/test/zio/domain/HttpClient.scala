package test.zio.domain

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import test.zio.domain.Logging.Logging
import zio._
import zio.duration._

object HttpClient {

  private def system: ZIO[Has[ActorSystem], Throwable, ActorSystem] =
    for {
      as <- ZIO.access[Has[ActorSystem]](_.get)
    } yield as

  private def executeRequestTask(request: HttpRequest): ZIO[clock.Clock with Logging with Has[ActorSystem], Throwable, HttpResponse] =
    for {
      as <- system
      resp <- httpTask(request, as)
    } yield resp

  def httpTask(request: HttpRequest, as: ActorSystem): ZIO[clock.Clock with Logging, Nothing, HttpResponse] =
    Task.fromFuture(_ => Http()(as).singleRequest(request))
      .flatMap {
        case r if r.status == StatusCodes.OK =>
          IO.succeed(r)
        case _ => IO.fail("Request failing, thread: " + Thread.currentThread().getName)
      }
      .tapError(err => Logging.error(err.toString))
      .retry(Schedule.recurs(3) && Schedule.exponential(10.milliseconds))
      .timeoutFail("Timeout occurred, interrupted")(5.seconds)
      .catchAll(e => IO.succeed(HttpResponse(entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`, s"to many tries, $e \n"))))

  def executeRequest(request: HttpRequest, runtime: Runtime[clock.Clock with Logging with Has[ActorSystem]]): HttpResponse =
    runtime.unsafeRun(executeRequestTask(request))

}
