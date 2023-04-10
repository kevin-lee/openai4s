package openai4s.types.chat

import cats.syntax.all.*
import cats.{Eq, Show}
import eu.timepit.refined.cats.*
import eu.timepit.refined.types.string.NonEmptyString
import extras.render.Render
import io.circe.{Codec, Decoder, Encoder}

/** Reference:
  * - https://platform.openai.com/docs/models/
  * - https://platform.openai.com/docs/models/model-endpoint-compatibility
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

  final case class Unsupported(override val value: NonEmptyString) extends Model(value)

  def gpt_4: Model          = Gpt_4
  def gpt_4_0314: Model     = Gpt_4_0314
  def gpt_4_32k: Model      = Gpt_4_32k
  def gpt_4_32k_0314: Model = Gpt_4_32k_0314

  def gpt_3_5_Turbo: Model      = Gpt_3_5_Turbo
  def gpt_3_5_Turbo_0301: Model = Gpt_3_5_Turbo_0301

  def unsupported(value: NonEmptyString): Model = Unsupported(value)

  def fromString(model: String): Either[String, Model] = model match {
    case Gpt_4.value.value => gpt_4.asRight
    case Gpt_4_0314.value.value => gpt_4_0314.asRight
    case Gpt_4_32k.value.value => gpt_4_32k.asRight
    case Gpt_4_32k_0314.value.value => gpt_4_32k_0314.asRight

    case Gpt_3_5_Turbo.value.value => gpt_3_5_Turbo.asRight
    case Gpt_3_5_Turbo_0301.value.value => gpt_3_5_Turbo_0301.asRight

    case _ => s"Unknown model: $model".asLeft
  }

  implicit val modelEq: Eq[Model] = Eq[NonEmptyString].contramap(_.value)

  implicit val modelRender: Render[Model] = Render.render(_.value.value)

  implicit val modelShow: Show[Model] = Show.fromToString

  implicit val modelCodec: Codec[Model] = Codec.from(
    Decoder[String].emap(value =>
      fromString(value).leftFlatMap(_ => unsupported(NonEmptyString.unsafeFrom(value)).asRight)
    ),
    Encoder[String].contramap(_.value.value),
  )
}
