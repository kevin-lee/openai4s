package openai4s.types.chat

import cats.{Eq, Show}
import cats.derived.*
import extras.render.Render
import io.circe.derivation.Configuration
import io.circe.derivation.*
import io.circe.*
import io.circe.{Decoder, Encoder}
import refined4s.*
import refined4s.modules.cats.derivation.*
import refined4s.modules.circe.derivation.*

/** @author Kevin Lee
  * @since 2023-03-24
  */
final case class Message(role: Message.Role, content: Message.Content) derives Eq, Show, ConfiguredCodec
object Message {
  given messageConfiguration: Configuration = Configuration.default.withSnakeCaseMemberNames

  type Role = Role.Type
  object Role extends Newtype[String] with CatsEqShow[String] with CirceNewtypeCodec[String] {

    given roleRender: Render[Role] = Render.render(_.value)

  }

  type Content = Content.Type
  object Content extends Newtype[String] with CatsEqShow[String] with CirceNewtypeCodec[String] {

    given contentRender: Render[Content] = Render.render(_.value)

  }

}
