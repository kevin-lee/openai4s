package openai4s.config

import cats.{Eq, Show}
import eu.timepit.refined.cats.*
import eu.timepit.refined.pureconfig.*
import eu.timepit.refined.types.string.NonEmptyString
import extras.render.Render
import extras.render.refined.*
import extras.render.syntax.*
import io.estatico.newtype.macros.newtype
import pureconfig.ConfigReader
import refined4s.strings.Uri

/** @author Kevin Lee
  * @since 2023-04-07
  */
final case class ApiUri(baseUri: ApiUri.BaseUri)
object ApiUri {
  implicit val apiUriEq: Eq[ApiUri] = Eq.fromUniversalEquals

  implicit val apiUriShow: Show[ApiUri] = Show.fromToString

  implicit val apiUriConfigReader: ConfigReader[ApiUri] = pureconfig.generic.semiauto.deriveReader

  implicit class ApiUriOps(private val apiUri: ApiUri) extends AnyVal {
    def chatCompletions: NonEmptyString =
      NonEmptyString.unsafeFrom(render"${apiUri.baseUri}/v1/chat/completions")
  }

  @newtype case class BaseUri(value: Uri)
  object BaseUri {

    implicit val baseUriEq: Eq[BaseUri] = deriving

    implicit val baseUriRender: Render[BaseUri] = deriving
    implicit val baseUriShow: Show[BaseUri]     = deriving

    implicit val baseUriConfigReader: ConfigReader[BaseUri] = deriving

  }

}
