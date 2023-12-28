package openai4s.types.completions

import cats.syntax.all.*
import cats.{Eq, Show}
import refined4s.types.all.NonEmptyString
import extras.render.Render
import io.circe.{Codec, Decoder, Encoder}

/** Reference:
  * - https://platform.openai.com/docs/models/
  * - https://platform.openai.com/docs/models/model-endpoint-compatibility
  * @author Kevin Lee
  * @since 2023-03-24
  */
enum Model(val value: NonEmptyString) derives CanEqual {
  case Text_Davinci_003 extends Model(NonEmptyString("text-davinci-003"))
  case Text_Davinci_002 extends Model(NonEmptyString("text-davinci-002"))

  case Text_Curie_001 extends Model(NonEmptyString("text-curie-001"))
  case Text_Babbage_001 extends Model(NonEmptyString("text-babbage-001"))
  case Text_Ada_001 extends Model(NonEmptyString("text-ada-001"))

  case Unsupported(override val value: NonEmptyString) extends Model(value)
}
object Model {
  def text_Davinci_003: Model = Text_Davinci_003
  def text_Davinci_002: Model = Text_Davinci_002

  def text_Curie_001: Model   = Text_Curie_001
  def text_Babbage_001: Model = Text_Babbage_001
  def text_Ada_001: Model     = Text_Ada_001

  def unsupported(value: NonEmptyString): Model = Unsupported(value)

  def supportedValues: List[Model] =
    List(
      Model.Text_Davinci_003,
      Model.Text_Davinci_002,
      Model.Text_Curie_001,
      Model.Text_Babbage_001,
      Model.Text_Ada_001,
    )

  def fromString(model: String): Either[String, Model] =
    Model.supportedValues.find(_.value.value === model).toRight(s"Unknown model: $model")

  given modelEq: Eq[Model] = Eq[String].contramap(_.value.value)

  given modelRender: Render[Model] = Render.render(_.value.value)

  given modelShow: Show[Model] = Show.fromToString

  given modelCodec: Codec[Model] = Codec.from(
    Decoder[String].emap(value =>
      fromString(value).leftFlatMap(_ => unsupported(NonEmptyString.unsafeFrom(value)).asRight)
    ),
    Encoder[String].contramap(_.value.value),
  )

  def unapply(model: Model): String = model.value.value

  extension (model: Model) {
    inline def toValue: String = model.value.value
  }

}
