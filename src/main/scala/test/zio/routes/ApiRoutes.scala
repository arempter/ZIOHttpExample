package test.zio.routes

import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

trait ApiRoutes {

  protected def executeRequestF(request: HttpRequest): HttpResponse

  private val request = HttpRequest(HttpMethods.GET, uri = "http://localhost:8081/status")
  private val slowRequest = HttpRequest(HttpMethods.GET, uri = "http://localhost:8081/slow")

  val getPaths: Route =
    get {
      path("zio") {
        extractRequest { req =>
          complete(executeRequestF(request))
        }
      }
    } ~ get {
      path("slow") {
        extractRequest { req =>
          complete(executeRequestF(slowRequest))
        }
      }
    }
}