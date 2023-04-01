package openai4s.request

import extras.hedgehog.circe.RoundTripTester
import extras.refinement.syntax.all._
import extras.render.syntax._
import hedgehog._
import hedgehog.runner._
import io.circe.Json

/** @author Kevin Lee
  * @since 2023-04-02
  */
object ChatSpec extends Properties {
  override def tests: List[Prop] = List(
    property("round-trip test Chat", roundTripTestChat),
    property("test decoding Chat", testDecodingChat),
  )

  def roundTripTestChat: Property =
    for {
      chat <- Gens.genChat.log("chat")
    } yield RoundTripTester(chat).test()

  def testDecodingChat: Property =
    for {
      chat <- Gens.genChat.log("chat")
    } yield {
      import io.circe.literal._
      import io.circe.syntax._

      def messages(message: Chat.Message): Json =
        json"""{
          "role": ${message.role.render},
          "content": ${message.content.render}
        }"""

      val expected = json"""{
           "model": ${chat.model.render},
           "messages": ${chat.messages.map(messages)},
           "temperature": ${chat.temperature.map(_.toValue)},
           "max_tokens": ${chat.maxTokens.map(_.toValue)}
         }""".deepDropNullValues

      val actual = chat.asJson
      actual ==== expected
    }
}
