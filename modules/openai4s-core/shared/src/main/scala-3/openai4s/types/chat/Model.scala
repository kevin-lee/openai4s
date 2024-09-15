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
  * - https://platform.openai.com/docs/models/o1
  * - https://platform.openai.com/docs/models/gpt-4o
  * - https://platform.openai.com/docs/models/gpt-4o-mini
  * - https://platform.openai.com/docs/models/gpt-4-turbo-and-gpt-4
  * - https://platform.openai.com/docs/models/gpt-3-5-turbo
  * @author Kevin Lee
  * @since 2023-03-24
  */
enum Model(val value: NonEmptyString, val description: String, val maxTokens: Int, val trainingData: Option[YearMonth])
    derives CanEqual {
  // o1
  case O1_Preview
      extends Model(
        NonEmptyString("o1-preview"),
        "Points to the most recent snapshot of the o1 model: o1-preview-2024-09-12",
        128_000,
        trainingData = YearMonth.of(2023, 10).some,
      )
  case O1_Preview_2024_09_12
      extends Model(
        NonEmptyString("o1-preview-2024-09-12"),
        "Latest o1 model snapshot",
        128_000,
        trainingData = YearMonth.of(2023, 10).some,
      )
  // GPT-4o
  case Gpt_4o
      extends Model(
        NonEmptyString("gpt-4o"),
        "GPT-4o\nOur most advanced, multimodal flagship model that’s cheaper and faster than GPT-4 Turbo. Currently points to gpt-4o-2024-05-13.",
        128_000,
        YearMonth.of(2023, 10).some,
      )
  case Gpt_4o_2024_05_13
      extends Model(
        NonEmptyString("gpt-4o-2024-05-13"),
        "gpt-4o currently points to this version.",
        128_000,
        YearMonth.of(2023, 10).some,
      )
  case Gpt_4o_2024_08_06
      extends Model(
        NonEmptyString("gpt-4o-2024-08-06"),
        "Latest snapshot that supports Structured Outputs",
        128_000,
        YearMonth.of(2023, 10).some,
      )
  // GPT-4o-mini
  case Gpt_4o_mini
      extends Model(
        NonEmptyString("gpt-4o-mini"),
        "GPT-4o mini\nOur affordable and intelligent small model for fast, lightweight tasks. GPT-4o mini is cheaper and more capable than GPT-3.5 Turbo. Currently points to gpt-4o-mini-2024-07-18",
        128_000,
        YearMonth.of(2023, 10).some,
      )
  case Gpt_4o_mini_2024_07_18
      extends Model(
        NonEmptyString("gpt-4o-mini-2024-07-18"),
        "gpt-4o-mini currently points to this version.",
        128_000,
        YearMonth.of(2023, 10).some,
      )
  // GPT-4 Turbo and 4
  case Gpt_4_Turbo
      extends Model(
        NonEmptyString("gpt-4-turbo"),
        "GPT-4 Turbo with Vision\nThe latest GPT-4 Turbo model with vision capabilities. Vision requests can now use JSON mode and function calling. Currently points to gpt-4-turbo-2024-04-09.",
        128_000,
        YearMonth.of(2023, 12).some,
      )
  case Gpt_4_Turbo_2024_04_09
      extends Model(
        NonEmptyString("gpt-4-turbo-2024-04-09"),
        "GPT-4 Turbo with Vision model. Vision requests can now use JSON mode and function calling. gpt-4-turbo currently points to this version.",
        128_000,
        YearMonth.of(2023, 12).some,
      )
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

  // GPT-3.5 Turbo and 3.5
  case Gpt_3_5_Turbo_0125
      extends Model(
        NonEmptyString("gpt-3.5-turbo-0125"),
        "Updated GPT 3.5 Turbo\nThe latest GPT-3.5 Turbo model with higher accuracy at responding in requested formats and a fix for a bug which caused a text encoding issue for non-English language function calls. Returns a maximum of 4,096 output tokens. Learn more: https://openai.com/blog/new-embedding-models-and-api-updates#:~:text=Other%20new%20models%20and%20lower%20pricing",
        16_385,
        YearMonth.of(2021, 9).some,
      )

  case Gpt_3_5_Turbo
      extends Model(
        NonEmptyString("gpt-3.5-turbo"),
        "Currently points to gpt-3.5-turbo-0125.",
        16_385,
        YearMonth.of(2021, 9).some,
      )
  case Gpt_3_5_Turbo_1106
      extends Model(
        NonEmptyString("gpt-3.5-turbo-1106"),
        "GPT-3.5 Turbo model with improved instruction following, JSON mode, reproducible outputs, parallel function calling, and more. Returns a maximum of 4,096 output tokens. Learn more: https://openai.com/blog/new-models-and-developer-products-announced-at-devday",
        16_385,
        YearMonth.of(2021, 9).some,
      )

  case Gpt_3_5_Turbo_Instruct
      extends Model(
        NonEmptyString("gpt-3.5-turbo-instruct"),
        "Similar capabilities as GPT-3 era models. Compatible with legacy Completions endpoint and not Chat Completions.",
        4_096,
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

  def o1_Preview: Model            = O1_Preview
  def o1_Preview_2024_09_12: Model = O1_Preview_2024_09_12

  def gpt_4o: Model            = Gpt_4o
  def gpt_4o_2024_05_13: Model = Gpt_4o_2024_05_13
  def gpt_4o_2024_08_06: Model = Gpt_4o_2024_08_06

  def gpt_4o_mini: Model            = Gpt_4o_mini
  def gpt_4o_mini_2024_07_18: Model = Gpt_4o_mini_2024_07_18

  def gpt_4_Turbo: Model            = Gpt_4_Turbo
  def gpt_4_Turbo_2024_04_09: Model = Gpt_4_Turbo_2024_04_09

  def gpt_4_Turbo_Preview: Model = Gpt_4_Turbo_Preview
  def gpt_4_0125_Preview: Model  = Gpt_4_0125_Preview
  def gpt_4_1106_Preview: Model  = Gpt_4_1106_Preview

  def gpt_4_Vision_Preview: Model      = Gpt_4_Vision_Preview
  def gpt_4_1106_Vision_Preview: Model = Gpt_4_1106_Vision_Preview

  def gpt_4: Model      = Gpt_4
  def gpt_4_0613: Model = Gpt_4_0613

  def gpt_4_32k: Model      = Gpt_4_32k
  def gpt_4_32k_0613: Model = Gpt_4_32k_0613

  def gpt_3_5_Turbo_0125: Model = Gpt_3_5_Turbo_0125

  def gpt_3_5_Turbo: Model = Gpt_3_5_Turbo

  def gpt_3_5_Turbo_1106: Model = Gpt_3_5_Turbo_1106

  def gpt_3_5_Turbo_Instruct: Model = Gpt_3_5_Turbo_Instruct

  def gpt_3_5_turbo_16k: Model = Gpt_3_5_turbo_16k

  def gpt_3_5_turbo_0613: Model     = Gpt_3_5_turbo_0613
  def gpt_3_5_turbo_16k_0613: Model = Gpt_3_5_turbo_16k_0613

  def unsupported(value: NonEmptyString): Model = Unsupported(value)

  def supportedValues: List[Model] =
    List(
      Model.o1_Preview,
      Model.o1_Preview_2024_09_12,
      //
      Model.gpt_4o,
      Model.gpt_4o_2024_05_13,
      //
      Model.gpt_4_Turbo,
      Model.gpt_4_Turbo_2024_04_09,
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
      Model.gpt_3_5_Turbo_0125,
      Model.gpt_3_5_Turbo,
      Model.gpt_3_5_Turbo_1106,
      Model.gpt_3_5_Turbo_Instruct,
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
