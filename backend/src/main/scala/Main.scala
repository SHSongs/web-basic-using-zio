import TodoBackend._
import zio._
import zhttp.service.{EventLoopGroup, Server}
import zhttp.service.server.ServerChannelFactory

import scala.util.Try

object Main extends ZIOAppDefault {

  private val PORT = 8090

  private val server =
    Server.port(PORT) ++
      Server.paranoidLeakDetection ++
      Server.app(getTodoHttp ++ helloHttp)

  val run = for {
    args <- ZIOAppArgs.getArgs
    nThreads = args.headOption.flatMap(x => Try(x.toInt).toOption).getOrElse(0)

    prog = for {
      start <- server.make
      _ <- Console.printLine(s"Server started on port ${start.port}")
      _ <- ZIO.never
    } yield ()
    _ <- prog.provide(ServerChannelFactory.auto, EventLoopGroup.auto(nThreads), Scope.default)
  } yield ()

}
