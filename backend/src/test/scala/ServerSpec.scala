import TodoApi.{getEndpoint, hello}
import sttp.client3._
import sttp.client3.httpclient.zio.HttpClientZioBackend
import sttp.tapir.client.sttp.SttpClientInterpreter
import sttp.tapir.DecodeResult
import zio.ZIO
import zio.test._

object ServerSpec extends ZIOSpecDefault {
  override def spec = suite("server")(
    test("client") {
      val request = basicRequest.get(uri"http://localhost:8090/")
      val backend = HttpClientSyncBackend()
      val response = request.send(backend)

      assertTrue(response.code.code == 200)

      val body = response.body match {
        case Right(value) => value
        case Left(_)      => "error"
      }
      assertTrue(body == "hello ZIO")
    },
    test("client with tapir") {
      val request = SttpClientInterpreter()
        .toRequest(hello, Some(uri"http://localhost:8090"))
        .apply()

      for {
        backend <- HttpClientZioBackend()
        response <- backend.send(request)
        body = response.body match {
          case DecodeResult.Value(Right(v)) => Some(v)
          case _                            => None
        }
      } yield assertTrue(response.code.code == 200) && assertTrue(body.get == "hello ZIO")
    },
    test("Retrieve all TODOs") {
      val request = SttpClientInterpreter()
        .toRequest(getEndpoint, Some(uri"http://localhost:8090"))
        .apply()

      for {
        backend <- HttpClientZioBackend()
        response <- backend.send(request)
        body = response.body match {
          case DecodeResult.Value(Right(v)) => Some(v)
          case _                            => None
        }
      } yield assertTrue(body.get == List(Todo(Some("test"), completed = true, Some("test"), 0)))

    }
  )
}






































