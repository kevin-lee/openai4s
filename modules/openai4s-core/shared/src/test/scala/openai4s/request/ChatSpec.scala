package openai4s.request

import extras.hedgehog.circe.RoundTripTester
import extras.refinement.syntax.all.*
import extras.render.syntax.*
import hedgehog.*
import hedgehog.runner.*
import io.circe.Json

/** @author Kevin Lee
  * @since 2023-04-02
  */
object ChatSpec extends Properties {
  override def tests: List[Prop] = List(
    property("round-trip test Chat", roundTripTestChat),
    property("test encoding Chat", testEncodingChat),
  )

  def roundTripTestChat: Property =
    for {
      chat <- Gens.genChat.log("chat")
    } yield RoundTripTester(chat).test()

  def testEncodingChat: Property =
    for {
      chat <- Gens.genChat.log("chat")
    } yield {
      import io.circe.literal.*
      import io.circe.syntax.*

      def toJson(message: Chat.Message): Json =
        json"""{
          "role": ${message.value.role.render},
          "content": ${message.value.content.render}
        }"""

      val expected = json"""{
           "model": ${chat.model.render},
           "messages": ${chat.messages.map(toJson)},
           "temperature": ${chat.temperature.map(_.toValue)},
           "max_tokens": ${chat.maxTokens.map(_.toValue)}
         }""".deepDropNullValues

      val actual = chat.asJson
      actual ==== expected
    }
}
