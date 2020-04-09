package test.zio.domain

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import test.zio.domain.Logging.Logging
import test.zio.infrastructure.HttpClientImpl
import zio._

object HttpClient {
  type HttpClient = Has[Service] with Logging with clock.Clock

  trait Service {
    def executeRequest(request: HttpRequest): ZIO[Logging with clock.Clock, String, HttpResponse]
  }

  def executeRequest(request: HttpRequest): ZIO[HttpClient, String, HttpResponse] = ZIO.accessM(_.get.executeRequest(request))

  val live = ZLayer.fromService[ActorSystem, Service] { system =>
    implicit val s = system
    HttpClientImpl()
  }


}
