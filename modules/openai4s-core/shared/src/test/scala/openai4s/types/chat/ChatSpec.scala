package openai4s.types.chat

import extras.hedgehog.circe.RoundTripTester
import extras.render.syntax.*
import hedgehog.*
import hedgehog.runner.*
import io.circe.Json
import openai4s.compat.TypesCompat
import openai4s.types

/** @author Kevin Lee
  * @since 2023-04-02
  */
object ChatSpec extends Properties with TypesCompat {
  override def tests: List[Test] = List(
    property("round-trip test Chat", roundTripTestChat),
    property("test encoding Chat", testEncodingChat),
    property("test Message(types.Message.Role, types.Message.Content)", testMessageApply),
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

      def toJson(message: Chat.Message): Json =
        json"""{
          "role": ${message.value.role.render},
          "content": ${message.value.content.render}
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

  def testMessageApply: Property =
    for {
      message <- types.Gens.genMessage.log("message")
    } yield {
      val expected = Chat.Message(message)
      val actual   = Chat.Message(message.role, message.content)
      actual ==== expected
    }
}
