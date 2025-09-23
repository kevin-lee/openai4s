package openai4s.config

import cats.{Eq, Show}
import extras.render.Render
import extras.render.syntax.*
import pureconfig.*
import refined4s.*
import refined4s.types.all.*
import refined4s.modules.cats.derivation.*

import refined4s.modules.pureconfig.derivation.PureconfigNewtypeConfigReader
import refined4s.modules.pureconfig.derivation.types.all.given
import refined4s.modules.extras.derivation.*
import refined4s.modules.extras.derivation.types.all.given

/** @author Kevin Lee
  * @since 2023-04-07
  */
final case class ApiUri(baseUri: ApiUri.BaseUri) derives ConfigReader
object ApiUri {

  val default: ApiUri = ApiUri.fromUri(CommonConstants.DefaultOpenAiUri)

  def fromUri(uri: Uri): ApiUri = ApiUri(BaseUri(uri))

  given apiUriEq: Eq[ApiUri] = Eq.fromUniversalEquals

  given apiUriShow: Show[ApiUri] = Show.fromToString

  given apiUriRender: Render[ApiUri] = Render[BaseUri].contramap(_.baseUri)

  extension (apiUri: ApiUri) {
    def chatCompletions: NonEmptyString =
      NonEmptyString.unsafeFrom(render"${apiUri.baseUri}/v1/chat/completions")

    def completions: NonEmptyString =
      NonEmptyString.unsafeFrom(render"${apiUri.baseUri}/v1/completions")
  }

  type BaseUri = BaseUri.Type
  object BaseUri extends Newtype[Uri], CatsEqShow[Uri], PureconfigNewtypeConfigReader[Uri], ExtrasRender[Uri]

}
