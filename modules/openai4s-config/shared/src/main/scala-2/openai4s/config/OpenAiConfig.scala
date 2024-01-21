package openai4s.config

import cats.{Eq, Show}
import eu.timepit.refined.pureconfig.*
import pureconfig.ConfigReader
import pureconfig.generic.semiauto.*
import refined4s.compat.RefinedCompatAllTypes.*

/** @author Kevin Lee
  * @since 2023-04-04
  */
final case class OpenAiConfig(apiUri: ApiUri, apiKey: ApiKey)
object OpenAiConfig {
  val defaultOpenAiApiUri: ApiUri = ApiUri.default

  implicit val openAiConfigEq: Eq[OpenAiConfig] = Eq.fromUniversalEquals

  implicit val openAiConfigShow: Show[OpenAiConfig] = cats.derived.semiauto.show

  implicit val openAiConfigConfigReader: ConfigReader[OpenAiConfig] = deriveReader

  implicit val apiKeyConfigReader: ConfigReader[ApiKey] = ConfigReader[NonEmptyString].map(ApiKey(_))

}
