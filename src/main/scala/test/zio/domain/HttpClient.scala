package test.zio.domain

import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import test.zio.infrastructure.HttpClientImpl
import zio._

object HttpClient {
  type HttpClient = Has[Service]

  trait Service {
    def executeRequest(request: HttpRequest): ZIO[ProgramEnv, Throwable, HttpResponse]
  }

  def executeRequest(request: HttpRequest): ZIO[HttpClient, Throwable, HttpResponse] =
    ZIO.accessM(_.get.executeRequest(request).provide(ProgramEnvLive))

  val live = ZLayer.succeed[Service](HttpClientImpl())


}
