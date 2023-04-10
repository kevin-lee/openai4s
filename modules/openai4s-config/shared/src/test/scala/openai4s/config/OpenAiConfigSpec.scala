package openai4s.config

import eu.timepit.refined.types.numeric.PosInt
import extras.render.syntax.*
import hedgehog.*
import hedgehog.extra.refined.StringGens
import hedgehog.runner.*
import pureconfig.ConfigSource

/** @author Kevin Lee
  * @since 2023-04-04
  */
object OpenAiConfigSpec extends Properties {
  override def tests: List[Test] = List(
    property("test loading OpenAiConfig", testOpenAiConfig)
  )

  def testOpenAiConfig: Property =
    for {
      apiBaseUri   <- Gen
                        .string(Gen.alphaNum, Range.linear(3, 10))
                        .list(Range.linear(1, 4))
                        .map(_.mkString("."))
                        .map(apiBaseUri => ApiUri(ApiUri.BaseUri(ApiUri.BaseUri.Value.unsafeFrom(apiBaseUri))))
                        .log("apiBaseUri")
      apiKeyString <- StringGens
                        .genNonEmptyString(
                          Gen.choice1(Gen.alphaNum, Gen.elementUnsafe("~!@#$%^&*()_+-=;:,.<>/?[]{}|".toList)),
                          PosInt(20),
                        )
                        .log("apiKeyString")
    } yield {
      val apiKey   = ApiKey(apiKeyString)
      val expected = OpenAiConfig(apiBaseUri, apiKey)

      val configString =
        raw"""api-uri {
             |  base-uri: "${apiBaseUri.baseUri.render}"
             |}
             |api-key = "${apiKeyString.value}"
             |""".stripMargin

      ConfigSource.string(configString).load[OpenAiConfig] match {
        case Right(actual) =>
          Result
            .all(
              List(
                actual ==== expected,
                actual.apiKey ==== expected.apiKey,
              )
            )
        case Left(err) =>
          Result
            .failure
            .log(
              s"""Loading OpenAiConfig has failed with the following error:
               |> ${err.toString}
               |> """.stripMargin
            )
      }
    }
}
