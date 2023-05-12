package openai4s.api.completions

import cats.effect.IO
import cats.syntax.all.*
import extras.hedgehog.ce3.syntax.runner.*
import hedgehog.*
import hedgehog.extra.Gens
import hedgehog.runner.*
import openai4s.compat.TypesCompat
import openai4s.config.ApiKey
import openai4s.http.HttpClient.HttpResponse
import openai4s.http.HttpClientStub
import openai4s.types.completions
import openai4s.types.completions.Text
import org.http4s.{Response as Http4sResponse, Uri as H4sUri}

/** @author Kevin Lee
  * @since 2023-05-12
  */
object CompletionsApiSpec extends Properties with TypesCompat {
  type F[A] = IO[A]
  val F: IO.type = IO

  override def tests: List[Test] = List(
    property("test completionsApi.completions", testCompletions)
  )

  def testCompletions: Property =
    for {
      completionsUri <- Gen
                          .string(Gen.alphaNum, Range.linear(2, 10))
                          .list(Range.linear(1, 5))
                          .map(_.mkString("."))
                          .map(H4sUri.unsafeFromString)
                          .log("completionsUri")

      apiKey   <- Gen
                    .string(Gens.genNonWhitespaceChar, Range.linear(1, 20))
                    .map(NonEmptyString.unsafeFrom)
                    .map(ApiKey(_))
                    .log("apiKey")
      textReq  <- completions.Gens.text.genText.log("textReq")
      response <- completions.Gens.response.genResponse.log("response")

    } yield runIO {
      val httpClient = HttpClientStub.withSendAndHandle[F] { request =>
        if (request.uri === completionsUri) {
          import org.http4s.circe.CirceEntityCodec.*
          request.as[Text].flatMap { req =>
            import org.http4s.dsl.io.*
            if (req === textReq) {
              Ok(response).map(HttpResponse.fromHttp4s)
            } else {
              BadRequest(show"Unexpected Text request: $req").map(HttpResponse.fromHttp4s)
            }
          }
        } else {
          HttpResponse.fromHttp4s(Http4sResponse.notFound[F]).pure[F]
        }
      }

      val expected = response

      val chatApi = CompletionsApi(completionsUri, apiKey, httpClient)
      chatApi
        .completions(textReq)
        .map { actual =>
          actual ==== expected
        }
    }

}
