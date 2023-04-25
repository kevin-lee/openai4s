package openai4s.config

import cats.{Eq, Show}
import pureconfig.*
import pureconfig.generic.derivation.default.*
import refined4s.strings.NonEmptyString

/** @author Kevin Lee
  * @since 2023-04-04
  */
final case class OpenAiConfig(apiUri: ApiUri, apiKey: ApiKey) derives ConfigReader
object OpenAiConfig {
  implicit val openAiConfigEq: Eq[OpenAiConfig] = Eq.fromUniversalEquals

  implicit val openAiConfigShow: Show[OpenAiConfig] = cats.derived.semiauto.show

//  implicit val openAiConfigConfigReader: ConfigReader[OpenAiConfig] = deriveReader

  given apiKeyConfigReader: ConfigReader[ApiKey] =
    ConfigReader.stringConfigReader.map(NonEmptyString.unsafeFrom).map(ApiKey(_))

}
