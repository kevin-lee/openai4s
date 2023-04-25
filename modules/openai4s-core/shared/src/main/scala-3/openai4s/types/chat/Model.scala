package openai4s.types.chat

import cats.syntax.all.*
import cats.{Eq, Show}
//import eu.timepit.refined.cats.*
import refined4s.strings.NonEmptyString
//import eu.timepit.refined.types.string.NonEmptyString
//import eu.timepit.refined.*
//import eu.timepit.refined.api.*
//import eu.timepit.refined.api.Refined.*
import extras.render.Render
import io.circe.{Codec, Decoder, Encoder}

/** Reference:
  * - https://platform.openai.com/docs/models/
  * - https://platform.openai.com/docs/models/model-endpoint-compatibility
  * @author Kevin Lee
  * @since 2023-03-24
  */
enum Model(val value: NonEmptyString) derives CanEqual {
  case Gpt_4 extends Model(NonEmptyString("gpt-4"))
  case Gpt_4_0314 extends Model(NonEmptyString("gpt-4-0314"))
  case Gpt_4_32k extends Model(NonEmptyString("gpt-4-32k"))
  case Gpt_4_32k_0314 extends Model(NonEmptyString("gpt-4-32k-0314"))

  case Gpt_3_5_Turbo extends Model(NonEmptyString("gpt-3.5-turbo"))
  case Gpt_3_5_Turbo_0301 extends Model(NonEmptyString("gpt-3.5-turbo-0301"))

  case Unsupported(override val value: NonEmptyString) extends Model(value)
}
object Model {

  def gpt_4: Model          = Gpt_4
  def gpt_4_0314: Model     = Gpt_4_0314
  def gpt_4_32k: Model      = Gpt_4_32k
  def gpt_4_32k_0314: Model = Gpt_4_32k_0314

  def gpt_3_5_Turbo: Model      = Gpt_3_5_Turbo
  def gpt_3_5_Turbo_0301: Model = Gpt_3_5_Turbo_0301

  def unsupported(value: NonEmptyString): Model = Unsupported(value)

  def supportedValues: List[Model] =
    List(
      Model.Gpt_4,
      Model.Gpt_4_0314,
      Model.Gpt_4_32k,
      Model.Gpt_4_32k_0314,
      Model.Gpt_3_5_Turbo,
      Model.Gpt_3_5_Turbo_0301,
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
