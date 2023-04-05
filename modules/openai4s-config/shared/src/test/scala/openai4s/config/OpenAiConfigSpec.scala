package openai4s.config

import eu.timepit.refined.types.numeric.PosInt
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
      apiKeyString <- StringGens.genNonWhitespaceString(PosInt(20)).log("apiKey")
    } yield {
      val apiKey   = ApiKey(apiKeyString)
      val expected = OpenAiConfig(apiKey)

      val configString =
        s"""api-key = ${apiKeyString.value}
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
