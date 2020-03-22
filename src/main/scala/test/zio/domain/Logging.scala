package test.zio.domain

import zio.{Has, UIO, ZIO, ZLayer}
import zio.console.Console

object Logging {
  type Logging = Has[Logging.Service]

  trait Service {
    def info(msg: String): UIO[Unit]
    def error(msg: String): UIO[Unit]
  }

  val consoleLogger: ZLayer[Console, Nothing, Logging] = ZLayer.fromFunction { console =>
    new Service {
      override def info(msg: String): UIO[Unit] = console.get.putStrLn(s"INFO: $msg")
      override def error(msg: String): UIO[Unit] = console.get.putStrLn(s"DEBUG: $msg")
    }
  }

  def info(msg: String): ZIO[Logging, Nothing, Unit] = ZIO.accessM(_.get.info(msg))
  def error(msg: String): ZIO[Logging, Nothing, Unit] = ZIO.accessM(_.get.error(msg))

}
