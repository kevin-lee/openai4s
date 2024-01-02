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
import refined4s.modules.extras.derivation.*

/** @author Kevin Lee
  * @since 2023-03-24
  */
final case class Message(role: Message.Role, content: Message.Content) derives Eq, Show, ConfiguredCodec
object Message {
  given messageConfiguration: Configuration = Configuration.default.withSnakeCaseMemberNames

  type Role = Role.Type
  object Role extends Newtype[String], CatsEqShow[String], CirceNewtypeCodec[String], ExtrasRender[String]

  type Content = Content.Type
  object Content extends Newtype[String], CatsEqShow[String], CirceNewtypeCodec[String], ExtrasRender[String]

}
