import sttp.tapir.generic.auto._
import sttp.tapir.json.zio._
import sttp.tapir.ztapir._

object TodoApi {
  val hello =
    endpoint.get
      .out(stringBody)

  val getEndpoint =
    endpoint.get
      .in("v1")
      .out(jsonBody[List[Todo]])
      .errorOut(stringBody)
      .description("Retrieve all TODOs")
}
