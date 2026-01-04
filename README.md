# openai4s
openai4s - OpenAI Client for Scala

`openai4s` is OpenAI client for functional programming.

## Example

Here is an example Scala script with `scala-cli` in Scala 3.

First of all you need to set the following environment variables
```bash
export OPENAI_API_KEY=YOUR_API_KEY_FROM_OPENAI
```
If you don't have the API key yet, you should generate it first.
Visit [OpenAI's account page](https://platform.openai.com/account/api-keys) to do it.

`openai4s-app.scala`
```scala 3
//> using scala "3.3.4"
//> using dep "org.typelevel::cats-core::latest.release"
//> using dep "io.kevinlee::openai4s-core::0.1.0"
//> using dep "io.kevinlee::openai4s-config::0.1.0"
//> using dep "io.kevinlee::openai4s-api::0.1.0"
//> using dep "io.kevinlee::openai4s-http4s::0.1.0"
//> using dep "io.kevinlee::refined4s-core::latest.release"
//> using dep "io.kevinlee::refined4s-cats::latest.release"
//> using dep "io.kevinlee::refined4s-pureconfig::latest.release"
//> using dep "com.github.pureconfig::pureconfig-cats-effect::latest.release"

import cats.data.NonEmptyList
import cats.effect.*
import cats.syntax.all.*
import fs2.io.net.Network
import org.http4s.Uri as H4sUri
import org.http4s.ember.client.EmberClientBuilder

import scala.concurrent.duration.*

import refined4s.*
import openai4s.api.ApiCore
import openai4s.api.chat.ChatApi
import openai4s.config.OpenAiConfig
import openai4s.http.*
import openai4s.types.common.*
import openai4s.types.chat.*

object MyAiApp extends IOApp.Simple {

  override def run: IO[Unit] =
    runChat[IO]

  def runChat[F[*]: Async: Network]: F[Unit] =
    (for {
      openAiConfig <- pureconfig.module.catseffect.loadConfigF[F, OpenAiConfig]

      _ <- EmberClientBuilder
        .default[F]
        .withTimeout(120.seconds)
        .withIdleConnectionTime((4 * 60).seconds)
        .build
        .use { client =>

          val httpClient = HttpClient(client)

          for {
            openAiApiUri <- Sync[F]
              .catchNonFatal(
                H4sUri.fromString(
                  openAiConfig.apiUri.chatCompletions.value
                )
              )
              .rethrow
            apiCore = ApiCore(openAiConfig.apiKey, httpClient)
            chatApi = ChatApi(openAiApiUri, apiCore)

            chat = Chat(
              model = Model.gpt_5_Mini,
              messages = NonEmptyList.of(
                Message(
                  Message.Role("user"),
                  Message.Content(
                    "Jane is faster than Joe. Joe is faster than Sam. Is Sam faster than Jane? Explain your reasoning step by step."
                  ),
                ),
              ),
              temperature = Temperature(0.1f).some,
              maxTokens = none,
            )
            _ <- Sync[F].delay(println(show"Sending $chat"))
            response <- chatApi.completion(chat)
            _ <- Sync[F].delay(println(show"Response: $response"))
          } yield ()
        }
    } yield ()).handleErrorWith {
      case err: HttpError[?] =>
        Sync[F].delay(
          println(
            show"""
                  |>> -----
                  |>> HttpError:
                  |$err
                  |>> -----
                  |""".stripMargin
          )
        )
      case err =>
        Sync[F].delay(
          println(
            show"""
                  >> -----
                  |>> ERROR:
                  |${err.toString}
                  |>> -----
                  |""".stripMargin
          )
        )
    }

}
```


Run
```bash
scala-cli run openai4s-app.scala
```
```
Compiling project (Scala 2.13.12, JVM (17))
Compiled project (Scala 2.13.12, JVM (17))
Sending Chat(model = Gpt_4o(value=gpt-4o, description=GPT-4o
Our most advanced, multimodal flagship model that’s cheaper and faster than GPT-4 Turbo. Currently points to gpt-4o-2024-05-13., maxTokens=128000, maxOutputTokens=4096, trainingData=Some(2023-10)), messages = NonEmptyList(Message(role = user, content = Jane is faster than Joe. Joe is faster than Sam. Is Sam faster than Jane? Explain your reasoning step by step.)), temperature = Some(0.1), maxTokens = None)
Response: Response(id = chatcmpl-A9Yv3oSHqqpsd0X5HiMNwhOE4VWwP, object = chat.completion, created = 2024-09-20T14:29:05Z, model = Gpt_4o_2024_05_13(value=gpt-4o-2024-05-13, description=gpt-4o currently points to this version., maxTokens=128000, maxOutputTokens=4096, trainingData=Some(2023-10)), usage = Usage(promptTokens = 32, completionTokens = 307, totalTokens = 339), choices = List(Choice(message = Message(role = assistant, content = To determine whether Sam is faster than Jane, let's analyze the information given step by step:

1. We are told that "Jane is faster than Joe." This establishes a relationship where Jane > Joe in terms of speed.

2. Next, we are told that "Joe is faster than Sam." This establishes another relationship where Joe > Sam in terms of speed.

3. To compare Sam and Jane, we can use the transitive property of inequality, which states that if A > B and B > C, then A > C.

4. Applying the transitive property to the relationships we have:

   Since Jane > Joe and Joe > Sam, we can infer that Jane > Sam.

Therefore, based on the given information, Sam is not faster than Jane. In fact, Jane is faster than Sam.), finishReason = stop, index = 0)))
```
