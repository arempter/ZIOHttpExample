package test.zio.routes

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpMethods, HttpRequest}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{RequestContext, Route, RouteResult}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import test.zio.infrastructure.AkkaHttpClient.executeRequest

import scala.concurrent.Future

class UserRoutes()(implicit as: ActorSystem, materializer: ActorMaterializer) {

  private val request = HttpRequest(HttpMethods.GET, uri = "http://localhost:8081/status")
  private val slowRequest = HttpRequest(HttpMethods.GET, uri = "http://localhost:8081/slow")

  private def makeResponse(request: HttpRequest): RequestContext => Future[RouteResult] = extractRequest { req =>
    println(s"Got request ->  ${req.uri}  ${as.name}")
    complete(Unmarshal(executeRequest(request).entity).to[String])
  }

  val getPaths: Route =
    get {
      path("zio") {
        extractRequest { req =>
          makeResponse(request)
        }
      }
    } ~ get {
      path("slow") {
        extractRequest { req =>
          makeResponse(slowRequest)
        }
      }
    }
}