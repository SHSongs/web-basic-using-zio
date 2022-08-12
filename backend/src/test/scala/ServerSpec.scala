import Main.hello
import sttp.client3._
import sttp.tapir.client.sttp.SttpClientInterpreter
import sttp.tapir.DecodeResult
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

      val backend = HttpClientSyncBackend()
      val response = request.send(backend)

      assertTrue(response.code.code == 200)

      val body = response.body match {
        case DecodeResult.Value(Right(v)) => Some(v)
        case _                            => None
      }
      assertTrue(body.get == "hello ZIO")
    },
  )
}
