package openai4s.types

import cats.syntax.all._
import cats.{Eq, Show}
import eu.timepit.refined.types.string.NonEmptyString
import extras.render.Render
import io.circe.{Codec, Decoder, Encoder}

/** Reference: https://platform.openai.com/docs/models/
  * @author Kevin Lee
  * @since 2023-03-24
  */
sealed abstract class Model(val value: NonEmptyString)
object Model {
  case object Gpt_4 extends Model(NonEmptyString("gpt-4"))
  case object Gpt_4_0314 extends Model(NonEmptyString("gpt-4-0314"))
  case object Gpt_4_32k extends Model(NonEmptyString("gpt-4-32k"))
  case object Gpt_4_32k_0314 extends Model(NonEmptyString("gpt-4-32k-0314"))

  case object Gpt_3_5_Turbo extends Model(NonEmptyString("gpt-3.5-turbo"))
  case object Gpt_3_5_Turbo_0301 extends Model(NonEmptyString("gpt-3.5-turbo-0301"))
  case object Text_Davinci_003 extends Model(NonEmptyString("text-davinci-003"))
  case object Text_Davinci_002 extends Model(NonEmptyString("text-davinci-002"))
  case object Code_Davinci_002 extends Model(NonEmptyString("code-davinci-002"))

  final case class Unsupported(override val value: NonEmptyString) extends Model(value)

  def gpt_4: Model          = Gpt_4
  def gpt_4_0314: Model     = Gpt_4_0314
  def gpt_4_32k: Model      = Gpt_4_32k
  def gpt_4_32k_0314: Model = Gpt_4_32k_0314

  def gpt_3_5_Turbo: Model      = Gpt_3_5_Turbo
  def gpt_3_5_Turbo_0301: Model = Gpt_3_5_Turbo_0301
  def text_Davinci_003: Model   = Text_Davinci_003
  def text_Davinci_002: Model   = Text_Davinci_002
  def code_Davinci_002: Model   = Code_Davinci_002

  def unsupported(value: NonEmptyString): Model = Unsupported(value)

  def fromString(model: String): Either[String, Model] = model match {
    case Gpt_4.value.value => gpt_4.asRight
    case Gpt_4_0314.value.value => gpt_4_0314.asRight
    case Gpt_4_32k.value.value => gpt_4_32k.asRight
    case Gpt_4_32k_0314.value.value => gpt_4_32k_0314.asRight

    case Gpt_3_5_Turbo.value.value => gpt_3_5_Turbo.asRight
    case Gpt_3_5_Turbo_0301.value.value => gpt_3_5_Turbo_0301.asRight
    case Text_Davinci_003.value.value => text_Davinci_003.asRight
    case Text_Davinci_002.value.value => text_Davinci_002.asRight
    case Code_Davinci_002.value.value => code_Davinci_002.asRight

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
