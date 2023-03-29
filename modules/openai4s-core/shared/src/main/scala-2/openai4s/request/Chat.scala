package openai4s.request

import cats.data.NonEmptyList
import cats.{Eq, Show}
import eu.timepit.refined.cats._
import eu.timepit.refined.types.numeric.NonNegDouble
import eu.timepit.refined.types.string.NonEmptyString
import extras.render.Render
import extras.render.refined._
import io.circe.refined._
import io.circe.{Codec, Decoder, Encoder}
import io.estatico.newtype.macros.newtype
import openai4s.types.Model

/** @author Kevin Lee
  * @since 2023-03-24
  */
final case class Chat(model: Model, messages: NonEmptyList[Chat.Message], temperature: Option[Chat.Temperature])
object Chat {
  implicit val chatEq: Eq[Chat] = Eq.fromUniversalEquals

  @SuppressWarnings(Array("org.wartremover.warts.Equals", "org.wartremover.warts.FinalVal"))
  implicit val chatShow: Show[Chat] = cats.derived.semiauto.show

  implicit val chatEncoder: Encoder[Chat] = io.circe.generic.semiauto.deriveEncoder[Chat].mapJson(_.deepDropNullValues)
  implicit val chatDecoder: Decoder[Chat] = io.circe.generic.semiauto.deriveDecoder

  final case class Message(role: Message.Role, content: Message.Content)
  object Message {
    implicit val messageCodec: Codec[Message] = io.circe.generic.semiauto.deriveCodec
    @newtype case class Role(value: NonEmptyString)
    object Role {
      implicit val roleEq: Eq[Role] = deriving

      implicit val roleRender: Render[Role] = deriving
      implicit val roleShow: Show[Role]     = deriving

      implicit val roleEncoder: Encoder[Role] = deriving
      implicit val roleDecoder: Decoder[Role] = deriving
    }

    @newtype case class Content(value: NonEmptyString)
    object Content {
      implicit val contentEq: Eq[Content] = deriving

      implicit val contentRender: Render[Content] = deriving
      implicit val contentShow: Show[Content]     = deriving

      implicit val contentEncoder: Encoder[Content] = deriving
      implicit val contentDecoder: Decoder[Content] = deriving
    }

  }

  @newtype case class Temperature(value: NonNegDouble)
  object Temperature {
    implicit val temperatureEq: Eq[Temperature] = deriving

    implicit val temperatureRender: Render[Temperature] = deriving
    implicit val temperatureShow: Show[Temperature]     = deriving

    implicit val temperatureEncoder: Encoder[Temperature] = deriving
    implicit val temperatureDecoder: Decoder[Temperature] = deriving
  }

}
