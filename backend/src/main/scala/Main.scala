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

  val run = ZIOAppArgs.getArgs.flatMap { args =>
    val nThreads: Int = args.headOption.flatMap(x => Try(x.toInt).toOption).getOrElse(0)

    server.make
      .flatMap(start =>
        Console.printLine(s"Server started on port ${start.port}")

          // Ensures the server doesn't die after printing
          *> ZIO.never,
      )
      .provide(ServerChannelFactory.auto, EventLoopGroup.auto(nThreads), Scope.default)
  }

}
