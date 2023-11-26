package openai4s.types.chat

import cats.{Eq, Show}
import extras.render.Render
import io.circe.generic.extras.Configuration
import io.circe.{Codec, Decoder, Encoder}
import io.estatico.newtype.macros.newtype

/** @author Kevin Lee
  * @since 2023-03-24
  */
final case class Message(role: Message.Role, content: Message.Content)
object Message {
  implicit val messageConfiguration: Configuration = Configuration.default.withSnakeCaseMemberNames

  implicit val messageEq: Eq[Message] = Eq.fromUniversalEquals

  implicit val messageShow: Show[Message] = cats.derived.semiauto.show

  implicit val messageCodec: Codec[Message] = io.circe.generic.extras.semiauto.deriveConfiguredCodec

  @newtype case class Role(value: String)
  object Role {
    implicit val roleEq: Eq[Role] = deriving

    implicit val roleRender: Render[Role] = deriving
    implicit val roleShow: Show[Role]     = deriving

    implicit val roleEncoder: Encoder[Role] = deriving
    implicit val roleDecoder: Decoder[Role] = deriving
  }

  @newtype case class Content(value: String)
  object Content {
    implicit val contentEq: Eq[Content] = deriving

    implicit val contentRender: Render[Content] = deriving
    implicit val contentShow: Show[Content]     = deriving

    implicit val contentEncoder: Encoder[Content] = deriving
    implicit val contentDecoder: Decoder[Content] = deriving
  }

}
