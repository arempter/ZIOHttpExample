package test.zio.domain

import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import test.zio.common.ProgramEnv.ProgramEnv
import test.zio.infrastructure.HttpClientImpl
import zio._

object HttpClient {
  type HttpClient = Has[Service]

  trait Service {
    def executeRequest(request: HttpRequest): ZIO[ProgramEnv, Throwable, HttpResponse]
    def executeManyRequests(requests: List[HttpRequest]): ZIO[ProgramEnv, Throwable, List[HttpResponse]]
  }

  def executeRequest(request: HttpRequest): ZIO[ProgramEnv, Throwable, HttpResponse] =
    ZIO.accessM(_.get.executeRequest(request))

  def executeManyRequests(requests: List[HttpRequest]): ZIO[ProgramEnv, Throwable, List[HttpResponse]] =
    ZIO.accessM(_.get.executeManyRequests(requests))

  val live = ZLayer.succeed[Service](HttpClientImpl())
}
