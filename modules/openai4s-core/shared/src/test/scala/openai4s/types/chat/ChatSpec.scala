package openai4s.types.chat

import extras.hedgehog.circe.RoundTripTester
import extras.render.syntax.*
import hedgehog.*
import hedgehog.runner.*
import io.circe.Json
import openai4s.compat.TypesCompat

/** @author Kevin Lee
  * @since 2023-04-02
  */
object ChatSpec extends Properties with TypesCompat {
  override def tests: List[Test] = List(
    property("round-trip test Chat", roundTripTestChat),
    property("test encoding Chat", testEncodingChat),
  )

  def roundTripTestChat: Property =
    for {
      chat <- Gens.chat.genChat.log("chat")
    } yield RoundTripTester(chat).test()

  def testEncodingChat: Property =
    for {
      chat <- Gens.chat.genChat.log("chat")
    } yield {
      import io.circe.literal.*
      import io.circe.syntax.*

      def toJson(message: Message): Json =
        json"""{
          "role": ${message.role.render},
          "content": ${message.content.render}
        }"""

      val expected = json"""{
           "model": ${chat.model.render},
           "messages": ${chat.messages.map(toJson)},
           "temperature": ${chat.temperature.map(_.value)},
           "max_tokens": ${chat.maxTokens.map(_.toValue)}
         }""".deepDropNullValues

      val actual = chat.asJson
      actual ==== expected
    }

}
