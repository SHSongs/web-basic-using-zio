import TodoEndpoint.{getEndpoint, hello}
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.ztapir.RichZEndpoint
import zio._
import zhttp.service.Server

object Main extends ZIOAppDefault {
  val helloHttp =
    ZioHttpInterpreter()
      .toHttp(hello.zServerLogic(_ => ZIO.succeed("hello ZIO")))

  val getTodoHttp =
    ZioHttpInterpreter()
      .toHttp(
        getEndpoint.zServerLogic(
          _ =>
            ZIO.succeed(
              List(Todo(Some("test"), completed = true, Some("test"), 0))
          ))
      )

  override def run =
    Server.start(8090, getTodoHttp ++ helloHttp)
}
