import zio.json._

final case class Todo(title: Option[String],
                      completed: Boolean,
                      text: Option[String],
                      order: Int)

object Todo {
  implicit val ZioCodecForCosmosRequest: JsonCodec[Todo] =
    DeriveJsonCodec.gen[Todo]

  val Example = Todo(
    title = Some("한글제목"),
    completed = true,
    text = Some("내용임"),
    order = 1
  )
}
