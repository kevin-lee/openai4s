package openai4s.types.completions

import cats.syntax.all.*
import cats.{Eq, Show}
import extras.render.Render
import io.circe.{Codec, Decoder, Encoder}
import refined4s.compat.RefinedCompatAllTypes.*

/** Reference:
  * - https://platform.openai.com/docs/models/
  * - https://platform.openai.com/docs/models/model-endpoint-compatibility
  * @author Kevin Lee
  * @since 2023-03-24
  */
sealed abstract class Model(val value: NonEmptyString)
object Model {
  case object Text_Davinci_003 extends Model(NonEmptyString("text-davinci-003"))
  case object Text_Davinci_002 extends Model(NonEmptyString("text-davinci-002"))

  case object Text_Curie_001 extends Model(NonEmptyString("text-curie-001"))
  case object Text_Babbage_001 extends Model(NonEmptyString("text-babbage-001"))
  case object Text_Ada_001 extends Model(NonEmptyString("text-ada-001"))

  final case class Unsupported(override val value: NonEmptyString) extends Model(value)

  def text_Davinci_003: Model = Text_Davinci_003
  def text_Davinci_002: Model = Text_Davinci_002

  def text_Curie_001: Model   = Text_Curie_001
  def text_Babbage_001: Model = Text_Babbage_001
  def text_Ada_001: Model     = Text_Ada_001

  def unsupported(value: NonEmptyString): Model = Unsupported(value)

  def fromString(model: String): Either[String, Model] = model match {
    case Text_Davinci_003.value.value => text_Davinci_003.asRight
    case Text_Davinci_002.value.value => text_Davinci_002.asRight

    case Text_Curie_001.value.value => text_Curie_001.asRight
    case Text_Babbage_001.value.value => text_Babbage_001.asRight
    case Text_Ada_001.value.value => text_Ada_001.asRight

    case _ => s"Unknown model: $model".asLeft
  }

  implicit val modelEq: Eq[Model] = Eq.fromUniversalEquals

  implicit val modelRender: Render[Model] = Render.render(_.value.value)

  implicit val modelShow: Show[Model] = Show.fromToString

  implicit val modelCodec: Codec[Model] = Codec.from(
    Decoder[String].emap(value =>
      fromString(value).leftFlatMap(_ => unsupported(NonEmptyString.unsafeFrom(value)).asRight)
    ),
    Encoder[String].contramap(_.value.value),
  )
}
