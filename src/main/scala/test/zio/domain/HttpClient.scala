package test.zio.domain

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import test.zio.infrastructure.HttpClientImpl
import zio._

object HttpClient {
  type HttpClient = Has[Service]

  trait Service {
    def executeRequest(request: HttpRequest): IO[Throwable, HttpResponse]
  }

  def executeRequest(request: HttpRequest): ZIO[HttpClient, Throwable, HttpResponse] = ZIO.accessM(_.get.executeRequest(request))

  val httpClientImpl = ZLayer.fromService[ActorSystem, Service] { system =>
    implicit val s = system
    HttpClientImpl()
  }


}
