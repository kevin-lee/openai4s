package openai4s.types.chat

import cats.syntax.all.*
import cats.{Eq, Show}
import refined4s.types.all.NonEmptyString
import refined4s.modules.cats.derivation.types.all.given
import extras.render.Render
import io.circe.{Codec, Decoder, Encoder}

import java.time.YearMonth

/** Reference:
  * - https://platform.openai.com/docs/models/
  * - https://platform.openai.com/docs/models/model-endpoint-compatibility
  * @author Kevin Lee
  * @since 2023-03-24
  */
enum Model(val value: NonEmptyString, val description: String, val maxTokens: Int, val trainingData: Option[YearMonth])
    derives CanEqual {
  case Gpt_4_Turbo_Preview
      extends Model(
        NonEmptyString("gpt-4-turbo-preview"),
        "Currently points to gpt-4-0125-preview.",
        128_000,
        YearMonth.of(2023, 12).some,
      )
  case Gpt_4_0125_Preview
      extends Model(
        NonEmptyString("gpt-4-0125-preview"),
        "GPT-4 Turbo\nThe latest GPT-4 model intended to reduce cases of “laziness” where the model doesn’t complete a task. Returns a maximum of 4,096 output tokens. Learn more: https://openai.com/blog/new-embedding-models-and-api-updates",
        128_000,
        YearMonth.of(2023, 12).some,
      )
  case Gpt_4_1106_Preview
      extends Model(
        NonEmptyString("gpt-4-1106-preview"),
        "GPT-4 Turbo model featuring improved instruction following, JSON mode, reproducible outputs, parallel function calling, and more. Returns a maximum of 4,096 output tokens. This is a preview model. Learn more: https://openai.com/blog/new-models-and-developer-products-announced-at-devday",
        128_000,
        YearMonth.of(2023, 4).some,
      )

  case Gpt_4_Vision_Preview
      extends Model(
        NonEmptyString("gpt-4-vision-preview"),
        "GPT-4 with the ability to understand images, in addition to all other GPT-4 Turbo capabilities. Currently points to gpt-4-1106-vision-preview.",
        128_000,
        YearMonth.of(2023, 4).some,
      )
  case Gpt_4_1106_Vision_Preview
      extends Model(
        NonEmptyString("gpt-4-1106-vision-preview"),
        "GPT-4 with the ability to understand images, in addition to all other GPT-4 Turbo capabilities. Returns a maximum of 4,096 output tokens. This is a preview model version. Learn more: https://openai.com/blog/new-models-and-developer-products-announced-at-devday",
        128_000,
        YearMonth.of(2023, 4).some,
      )

  case Gpt_4
      extends Model(
        NonEmptyString("gpt-4"),
        "Currently points to gpt-4-0613. See continuous model upgrades: https://platform.openai.com/docs/models/continuous-model-upgrades",
        8_192,
        YearMonth.of(2021, 9).some,
      )

  case Gpt_4_0613
      extends Model(
        NonEmptyString("gpt-4-0613"),
        "Snapshot of gpt-4 from June 13th 2023 with improved function calling support.",
        8_192,
        YearMonth.of(2021, 9).some,
      )

  case Gpt_4_32k
      extends Model(
        NonEmptyString("gpt-4-32k"),
        "Currently points to gpt-4-32k-0613. See continuous model upgrades: https://platform.openai.com/docs/models/continuous-model-upgrades This model was never rolled out widely in favor of GPT-4 Turbo.",
        32_768,
        YearMonth.of(2021, 9).some,
      )
  case Gpt_4_32k_0613
      extends Model(
        NonEmptyString("gpt-4-32k-0613"),
        "Snapshot of gpt-4-32k from June 13th 2023 with improved function calling support. This model was never rolled out widely in favor of GPT-4 Turbo.",
        32_768,
        YearMonth.of(2021, 9).some,
      )

  case Gpt_3_5_Turbo
      extends Model(
        NonEmptyString("gpt-3.5-turbo"),
        "Currently points to gpt-3.5-turbo-0125.",
        16_385,
        YearMonth.of(2021, 9).some,
      )
  case Gpt_3_5_turbo_16k
      extends Model(
        NonEmptyString("gpt-3.5-turbo-16k"),
        "[Legacy] Currently points to gpt-3.5-turbo-16k-0613.",
        16_385,
        YearMonth.of(2021, 9).some,
      )

  case Gpt_3_5_turbo_0613
      extends Model(
        NonEmptyString("gpt-3.5-turbo-0613"),
        "[Legacy] Snapshot of gpt-3.5-turbo from June 13th 2023. Will be deprecated (https://platform.openai.com/docs/deprecations/2023-10-06-chat-model-updates) on June 13, 2024.",
        4_096,
        YearMonth.of(2021, 9).some,
      )
  case Gpt_3_5_turbo_16k_0613
      extends Model(
        NonEmptyString("gpt-3.5-turbo-16k-0613"),
        "[Legacy] Snapshot of gpt-3.5-16k-turbo from June 13th 2023. Will be deprecated (https://platform.openai.com/docs/deprecations/2023-10-06-chat-model-updates) on June 13, 2024.",
        16_385,
        YearMonth.of(2021, 9).some,
      )

  case Unsupported(override val value: NonEmptyString) extends Model(value, "", 0, none)
}
object Model {

  def gpt_4_Turbo_Preview: Model = Gpt_4_Turbo_Preview
  def gpt_4_0125_Preview: Model  = Gpt_4_0125_Preview
  def gpt_4_1106_Preview: Model  = Gpt_4_1106_Preview

  def gpt_4_Vision_Preview: Model      = Gpt_4_Vision_Preview
  def gpt_4_1106_Vision_Preview: Model = Gpt_4_1106_Vision_Preview

  def gpt_4: Model      = Gpt_4
  def gpt_4_0613: Model = Gpt_4_0613

  def gpt_4_32k: Model      = Gpt_4_32k
  def gpt_4_32k_0613: Model = Gpt_4_32k_0613

  def gpt_3_5_Turbo: Model     = Gpt_3_5_Turbo
  def gpt_3_5_turbo_16k: Model = Gpt_3_5_turbo_16k

  def gpt_3_5_turbo_0613: Model     = Gpt_3_5_turbo_0613
  def gpt_3_5_turbo_16k_0613: Model = Gpt_3_5_turbo_16k_0613

  def unsupported(value: NonEmptyString): Model = Unsupported(value)

  def supportedValues: List[Model] =
    List(
      Model.gpt_4_Turbo_Preview,
      Model.gpt_4_0125_Preview,
      Model.gpt_4_1106_Preview,
      //
      Model.gpt_4_Vision_Preview,
      Model.gpt_4_1106_Vision_Preview,
      //
      Model.gpt_4,
      Model.gpt_4_32k,
      //
      Model.gpt_4_0613,
      Model.gpt_4_32k_0613,
      //
      Model.gpt_3_5_Turbo,
      Model.gpt_3_5_turbo_16k,
      //
      Model.gpt_3_5_turbo_0613,
      Model.gpt_3_5_turbo_16k_0613,
    )

  def fromString(model: String): Either[String, Model] =
    Model.supportedValues.find(_.toValue === model).toRight(s"Unknown model: $model")

  given modelEq: Eq[Model] = Eq[String].contramap(_.toValue)

  given modelRender: Render[Model] = Render.render(_.toValue)

  given showModel: Show[Model] = {
    case Unsupported(value) =>
      show"Unsupported(value=$value)"

    case m =>
      show"${m.toString}(value=${m.value}, description=${m.description}, maxTokens=${m.maxTokens}, trainingData=${m.trainingData.toString})"
  }

  given modelCodec: Codec[Model] = Codec.from(
    Decoder[String].emap(value =>
      fromString(value).leftFlatMap(_ => unsupported(NonEmptyString.unsafeFrom(value)).asRight)
    ),
    Encoder[String].contramap(_.toValue),
  )

  def unapply(model: Model): String = model.toValue

  extension (model: Model) {
    inline def toValue: String = model.value.value
  }

}
