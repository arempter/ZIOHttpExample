package test.zio.domain.model

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

case class AkkaDependencies(system: ActorSystem) {
  implicit val mat: ActorMaterializer = ActorMaterializer()(system)
}
