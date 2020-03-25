package test.zio.domain

import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import zio.Has

object HttpClient {
  type HttpClient = Has[HttpClient.Service]

  trait Service {
    def executeRequest(request: HttpRequest): HttpResponse
  }

}
