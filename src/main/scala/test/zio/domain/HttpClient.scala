package test.zio.domain

import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import test.zio.infrastructure.HttpClientImpl
import zio._

object HttpClient {
  type HttpClient = Has[Service]

  trait Service {
    def executeRequest(request: HttpRequest): ZIO[ActorEnv, Throwable, HttpResponse]
    def executeManyRequests(requests: List[HttpRequest]): ZIO[ActorEnv, Throwable, List[HttpResponse]]
  }

  def executeRequest(request: HttpRequest): ZIO[HttpClient, Throwable, HttpResponse] =
    ZIO.accessM(_.get.executeRequest(request).provide(ActorEnvLive)) // add dependencies locally

  def executeManyRequests(requests: List[HttpRequest]): ZIO[HttpClient, Throwable, List[HttpResponse]] =
    ZIO.accessM(_.get.executeManyRequests(requests).provide(ActorEnvLive))

  val live = ZLayer.succeed[Service](HttpClientImpl())
}
