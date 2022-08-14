import TodoApi.{getEndpoint, hello}
import sttp.client3._
import sttp.client3.httpclient.zio.HttpClientZioBackend
import sttp.model.Uri
import sttp.tapir.client.sttp.SttpClientInterpreter
import sttp.tapir.DecodeResult
import zhttp.service.server.ServerChannelFactory
import zhttp.service.{EventLoopGroup, Server, ServerChannelFactory}
import zio.{&, ZIO, ZLayer}
import zio.test._

case class TodoClientConfig(host: Uri)

object ServerSpec extends ZIOSpecDefault {
  override def spec =
    suite("server")(
      test("client with tapir") {
        for {
          config <- ZIO.service[TodoClientConfig]
          request = SttpClientInterpreter()
            .toRequest(hello, Some(config.host))
            .apply()
          backend <- HttpClientZioBackend()
          response <- backend.send(request)
          body = response.body match {
            case DecodeResult.Value(Right(v)) => Some(v)
            case _                            => None
          }
        } yield
          assertTrue(response.code.code == 200) && assertTrue(
            body.get == "hello ZIO")
      },
      test("Retrieve all TODOs") {
        for {
          config <- ZIO.service[TodoClientConfig]

          request = SttpClientInterpreter()
            .toRequest(getEndpoint, Some(config.host))
            .apply()
          backend <- HttpClientZioBackend()
          response <- backend.send(request)
          body = response.body match {
            case DecodeResult.Value(Right(v)) => Some(v)
            case _                            => None
          }
        } yield
          assertTrue(
            body.get ==
              List(Todo(Some("test"), completed = true, Some("test"), 0))
          )
      }
    ).provideSome[EventLoopGroup & ServerChannelFactory](
        serverApp.project { port =>
          TodoClientConfig(uri"http://localhost:$port/")
        }
      )
      .provideSomeShared(
        EventLoopGroup.auto(2),
        ServerChannelFactory.auto
      )

  lazy val serverApp =
    ZLayer.scoped(
      Server(Main.app)
        .withPort(0)
        .make
        .map(_.port)
    )
}
