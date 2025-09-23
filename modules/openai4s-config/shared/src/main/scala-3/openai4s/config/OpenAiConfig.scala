package openai4s.config

import cats.{Eq, Show}
import pureconfig.*
import refined4s.types.all.NonEmptyString

/** @author Kevin Lee
  * @since 2023-04-04
  */
final case class OpenAiConfig(apiUri: ApiUri, apiKey: ApiKey) derives ConfigReader
object OpenAiConfig {
  val defaultOpenAiApiUri: ApiUri = ApiUri.default

  given openAiConfigEq: Eq[OpenAiConfig] = cats.derived.semiauto.eq

  given openAiConfigShow: Show[OpenAiConfig] = cats.derived.semiauto.show

  given apiKeyConfigReader: ConfigReader[ApiKey] =
    ConfigReader.stringConfigReader.map(NonEmptyString.unsafeFrom).map(ApiKey(_))

}
