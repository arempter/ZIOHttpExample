package test.zio.infrastructure

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import test.zio.domain.Logging.Logging
import test.zio.domain.{HttpClient, Logging}
import zio.{clock, _}
import zio.duration._

case class HttpClientImpl()(implicit system: ActorSystem) extends HttpClient.Service {

  private def httpTask(request: HttpRequest): ZIO[Logging with clock.Clock, String, HttpResponse] = {
    Task.fromFuture(_ =>
      Http().singleRequest(request))
      .flatMap {
        case r if r.status == StatusCodes.OK =>
          IO.succeed(r)
        case _ => IO.fail(s"Request failing, system: ${system.name}, thread: ${Thread.currentThread().getName}")
      }
      .tapError(err => Logging.error(err.toString))
      .retry(Schedule.recurs(3) && Schedule.exponential(10.milliseconds))
      .timeoutFail("Timeout occurred, interrupted")(3.seconds)
      .catchAll(e => IO.succeed(HttpResponse(entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`, s"To many tries, $e \n"))))
  }

  def executeRequest(request: HttpRequest): ZIO[Logging with clock.Clock, String, HttpResponse] = httpTask(request)

}