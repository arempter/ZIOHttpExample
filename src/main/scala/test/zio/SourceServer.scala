package test.zio

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer

import scala.util.Random

object SourceServer extends App {

  implicit val system = ActorSystem("SourceServer")
  implicit val mat = ActorMaterializer()

  val rand = Random

  val status: Route =
    get {
      path("status") {
        extractRequest { req =>
          println(s"Got request -> ${req.uri}")
          rand.nextInt(10) % 2 match {
            case i if i == 0 => complete(s"This is sample response: ${System.currentTimeMillis()} \n")
            case _ => complete(StatusCodes.InternalServerError -> "Unlucky ...\n")
          }
        }
      }
    } ~ get {
      path("slow") {
        extractRequest { req =>
          println(s"Got request -> ${req.uri}, heavy processing")
          Thread.sleep(5500)
          complete(StatusCodes.OK -> "Finally done...")
        }
      }
    }

  Http().bindAndHandle(status, "localhost", 8081)

}
