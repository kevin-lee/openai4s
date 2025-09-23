package openai4s.http

import cats.effect.*
import cats.syntax.all.*
import extras.fs2.text.syntax.*
import io.circe.Decoder
import org.http4s.*
import org.http4s.Status.Successful as H4sSuccessful
import org.http4s.client.Client

/** @author Kevin Lee
  * @since 2023-04-01
  */
trait HttpClient[F[*]] {
  def send[A: Decoder](request: Request[F]): F[A]
  def sendWith[A: Decoder](request: F[Request[F]]): F[A]

  def sendAndHandle[A](request: Request[F])(handler: HttpClient.HttpResponse[F] => F[A]): F[A]
  def sendWithAndHandle[A](request: F[Request[F]])(handler: HttpClient.HttpResponse[F] => F[A]): F[A]
}
object HttpClient {

  def apply[F[*]: Async](client: Client[F]): HttpClient[F] =
    new HttpClientF[F](client)

  private final class HttpClientF[F[*]: Async](client: Client[F]) extends HttpClient[F] {
    import org.http4s.circe.CirceEntityCodec.*
    override def send[A: Decoder](request: Request[F]): F[A] = {
      val entityDecoder = EntityDecoder[F, A]
      val theRequest    = if (entityDecoder.consumes.nonEmpty) {
        import org.http4s.headers.*
        val mediaRanges = entityDecoder.consumes.toList
        mediaRanges match {
          case head :: tail =>
            request.addHeader(Accept(MediaRangeAndQValue(head), tail.map(MediaRangeAndQValue(_))*))
          case Nil =>
            request
        }
      } else request
      sendAndHandle(theRequest) {
        case HttpResponse.Successful(res) =>
          entityDecoder
            .decode(res, strict = false)
            .leftMap(HttpError.decodingError[F](theRequest, _))
            .rethrowT

        case HttpResponse.Failed(res) =>
          val err = res
            .as[io.circe.Json]
            .map { jsonBody =>
              HttpError.unexpectedStatus(request, res.status, HttpError.UnexpectedStatus.Body.jsonBody(jsonBody))
            }
            .handleErrorWith { _ =>
              res.body.utf8String.map { body =>
                HttpError.unexpectedStatus(request, res.status, HttpError.UnexpectedStatus.Body.stringBody(body))
              }
            }
            .handleError { err =>
              HttpError.unexpectedStatus(request, res.status, HttpError.UnexpectedStatus.Body.withCause(err))
            }
          err.flatMap(Sync[F].raiseError[A](_))
      }
    }

    override def sendWith[A: Decoder](request: F[Request[F]]): F[A] =
      request.flatMap(send[A](_))

    override def sendAndHandle[A](request: Request[F])(handler: HttpResponse[F] => F[A]): F[A] =
      client
        .run(request)
        .handleErrorWith { (err: Throwable) =>
          HttpError
            .fromHttp4sException(err, request)
            .fold(Resource.eval[F, Response[F]](Sync[F].raiseError(err)))(e => Resource.eval(Sync[F].raiseError(e)))
        }
        .use(response => handler(HttpResponse.fromHttp4s[F](response)))

    override def sendWithAndHandle[A](request: F[Request[F]])(handler: HttpResponse[F] => F[A]): F[A] =
      request.flatMap(sendAndHandle(_)(handler))
  }

  sealed abstract class HttpResponse[F[*]](val response: Response[F])
  object HttpResponse {

    final case class Successful[F[*]] private (override val response: Response[F]) extends HttpResponse[F](response)
    object Successful {
      private[HttpResponse] def create[F[*]](response: Response[F]): HttpResponse[F] = new Successful[F](response)
    }

    final case class Failed[F[*]] private (override val response: Response[F]) extends HttpResponse[F](response)
    object Failed {
      private[HttpResponse] def create[F[*]](response: Response[F]): HttpResponse[F] = new Failed[F](response)
    }

    def successful[F[*]](response: Response[F]): HttpResponse[F] = Successful.create[F](response)

    def failed[F[*]](response: Response[F]): HttpResponse[F] = Failed.create[F](response)

    def fromHttp4s[F[*]](response: Response[F]): HttpResponse[F] =
      H4sSuccessful.unapply(response).fold(failed(response))(successful)

  }

}
