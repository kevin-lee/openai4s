package openai4s.api.chat

import cats.effect.IO
import cats.syntax.all.*
import eu.timepit.refined.types.numeric.PosInt
import extras.hedgehog.ce3.syntax.runner.*
import hedgehog.*
import hedgehog.extra.refined.StringGens
import hedgehog.runner.*
import openai4s.config.ApiKey
import openai4s.http.HttpClient.HttpResponse
import openai4s.http.HttpClientStub
import openai4s.types.chat
import openai4s.types.chat.Chat
import org.http4s.{Uri, Response as Http4sResponse}

/** @author Kevin Lee
  * @since 2023-04-09
  */
object ChatApiSpec extends Properties {
  type F[A] = IO[A]
  val F: IO.type = IO

  override def tests: List[Test] = List(
    property("test chatApi.completion", testCompletion)
  )

  def testCompletion: Property =
    for {
      chatCompletionsUri <- Gen
                              .string(Gen.alphaNum, Range.linear(2, 10))
                              .list(Range.linear(1, 5))
                              .map(_.mkString("."))
                              .map(Uri.unsafeFromString)
                              .log("chatCompletionsUri")

      apiKey   <- StringGens.genNonWhitespaceString(PosInt(20)).map(ApiKey(_)).log("apiKey")
      chatReq  <- chat.Gens.chat.genChat.log("chatReq")
      response <- chat.Gens.response.genResponse.log("response")

    } yield runIO {
      val httpClient = HttpClientStub.withSendAndHandle[F] { request =>
        if (request.uri === chatCompletionsUri) {
          import org.http4s.circe.CirceEntityCodec.*
          request.as[Chat].flatMap { req =>
            import org.http4s.dsl.io.*
            if (req === chatReq) {
              Ok(response).map(HttpResponse.fromHttp4s)
            } else {
              BadRequest(show"Unexpected Chat request: $req").map(HttpResponse.fromHttp4s)
            }
          }
        } else {
          HttpResponse.fromHttp4s(Http4sResponse.notFound[F]).pure[F]
        }
      }

      val expected = response

      val chatApi = ChatApi(chatCompletionsUri, apiKey, httpClient)
      chatApi
        .completion(chatReq)
        .map { actual =>
          actual ==== expected
        }
    }
}