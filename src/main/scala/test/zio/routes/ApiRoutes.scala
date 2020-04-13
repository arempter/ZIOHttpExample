package test.zio.routes

import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

trait ApiRoutes {

  protected def runRequest(request: HttpRequest): HttpResponse
  protected def runManyRequests(requests: List[HttpRequest]): List[HttpResponse]

  private val request = HttpRequest(HttpMethods.GET, uri = "http://localhost:8081/status")
  private val slowRequest = HttpRequest(HttpMethods.GET, uri = "http://localhost:8081/slow")

  val getPaths: Route =
    get {
      path("zio") {
        extractRequest { req =>
          complete(runRequest(request))
        }
      }
    } ~ get {
      path("slow") {
        extractRequest { req =>
          complete(runRequest(slowRequest))
        }
      }
    } ~ get {
      path("zioMany") {
        extractRequest { req =>
          val requests = (1 to 100).map(_ => request).toList
          complete(runManyRequests(requests).map(r=>r.status.toString).toString)
        }
      }
    }
}