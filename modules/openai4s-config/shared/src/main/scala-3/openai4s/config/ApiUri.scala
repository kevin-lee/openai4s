package openai4s.config

import cats.{Eq, Show}
import cats.syntax.all.*
import extras.render.Render
import extras.render.syntax.*
import pureconfig.*
import pureconfig.generic.derivation.default.*
import pureconfig.error.CannotConvert
import refined4s.strings.*

/** @author Kevin Lee
  * @since 2023-04-07
  */
final case class ApiUri(baseUri: ApiUri.BaseUri) derives ConfigReader
object ApiUri {
  given apiUriEq: Eq[ApiUri] = Eq.fromUniversalEquals

  given apiUriShow: Show[ApiUri] = Show.fromToString

  extension (apiUri: ApiUri) {
    def chatCompletions: NonEmptyString =
      NonEmptyString.unsafeFrom(render"${apiUri.baseUri}/v1/chat/completions")

    def completions: NonEmptyString =
      NonEmptyString.unsafeFrom(render"${apiUri.baseUri}/v1/completions")
  }

  type BaseUri = BaseUri.BaseUri
  object BaseUri {
    opaque type BaseUri = Uri
    def apply(baseUri: Uri): BaseUri = baseUri

    given baseUriCanEqual: CanEqual[BaseUri, BaseUri] = CanEqual.derived

    extension (baseUri: BaseUri) {
      def value: Uri = baseUri
    }

    given baseUriEq: Eq[BaseUri] = Eq.fromUniversalEquals

    given baseUriRender: Render[BaseUri] = Render.stringRender.contramap(_.value.value)
    given baseUriShow: Show[BaseUri]     = Show.catsShowForString.contramap(_.value.value)

    given baseUriConfigReader: ConfigReader[BaseUri] = ConfigReader
      .stringConfigReader
      .emap(s => Uri.from(s).leftMap(err => CannotConvert(s, "refined4s.strings.Uri$", err)))

  }

}
