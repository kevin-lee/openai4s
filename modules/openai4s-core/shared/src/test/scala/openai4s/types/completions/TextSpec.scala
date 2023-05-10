package openai4s.types.completions

import cats.syntax.all.*
import extras.hedgehog.circe.RoundTripTester
import extras.render.syntax.*
import hedgehog.*
import hedgehog.runner.*
import openai4s.compat.TypesCompat

/** @author Kevin Lee
  * @since 2023-05-08
  */
object TextSpec extends Properties with TypesCompat {
  override def tests: List[Test] = List(
    property("round-trip test Text", roundTripTestText),
    property("test encoding Text", testEncodingText),
  )

  def roundTripTestText: Property =
    for {
      text <- Gens.text.genText.log("text")
    } yield RoundTripTester(text).test()

  def testEncodingText: Property =
    for {
      text <- Gens.text.genText.log("text")
    } yield {
      import io.circe.literal.*
      import io.circe.syntax.*

      val expected =
        json"""{
           "model": ${text.model.render},
           "prompt": ${text.prompt},
           "max_tokens": ${text.maxTokens.map(_.toValue)},
           "temperature": ${text.temperature.map(_.value)},
           "top_p": ${text.topP.map(_.toValue)},
           "n": ${text.n.map(_.value)},
           "stream": ${text.stream.map(_ === Text.Stream.isStream)},
           "logprobs": ${text.logprobs.map(_.value)},
           "stop": ${text.stop.map(_.toValue)}
         }""".deepDropNullValues

      val actual = text.asJson
      actual ==== expected
    }

}
