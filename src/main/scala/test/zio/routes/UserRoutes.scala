package test.zio.routes

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{RequestContext, Route, RouteResult}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import test.zio.domain.HttpClient.executeRequest
import zio._
import zio.console.Console

import scala.concurrent.Future

class UserRoutes(runtime: Runtime[Has[ActorSystem] with clock.Clock with Console])(implicit as: ActorSystem, materializer: ActorMaterializer) {

  private val request = HttpRequest(HttpMethods.GET, uri = "http://localhost:8081/status")
  private val slowRequest = HttpRequest(HttpMethods.GET, uri = "http://localhost:8081/slow")

  private def makeResponse(request: HttpRequest): RequestContext => Future[RouteResult] = extractRequest { req =>
    console.putStrLn(s"Got request ->  ${req.uri}  ${as.name}")
    complete(Unmarshal(executeRequest(request, runtime).entity).to[String])
  }

  val getPaths: Route =
    get {
      path("zio") {
        makeResponse(request)
      }
    } ~ get {
      path("slow") {
        makeResponse(slowRequest)
      }
    }

}
