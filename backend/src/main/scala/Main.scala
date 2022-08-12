import sttp.tapir.{endpoint, stringBody}
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.ztapir.RichZEndpoint
import zio._
import zhttp.service.Server

object Main extends ZIOAppDefault {
  val hello =
    endpoint.get
      .out(stringBody)

  val helloHttp =
    ZioHttpInterpreter()
      .toHttp(hello.zServerLogic(_ => ZIO.succeed("hello ZIO")))

  override def run =
    Server.start(8090, helloHttp)
}
