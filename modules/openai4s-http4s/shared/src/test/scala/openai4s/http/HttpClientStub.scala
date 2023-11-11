package openai4s.http

import cats.MonadThrow
import cats.effect.*
import cats.syntax.all.*
import extras.testing.StubToolsCats
import io.circe.Decoder
import openai4s.http.HttpClient.HttpResponse
import org.http4s.client.UnexpectedStatus
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, Request}

object HttpClientStub {

  def empty[F[*]: Concurrent]: HttpClient[F] = HttpClientStub(none)

  def withAll[F[*]: Concurrent](
    requestToResponse: => Request[F] => F[HttpResponse[F]]
  ): HttpClient[F] = HttpClientStub(requestToResponse.some)

  def withSendAndHandle[F[*]: Concurrent](requestToResponse: => Request[F] => F[HttpResponse[F]]): HttpClient[F] =
    apply(requestToResponse.some)

  private def apply[F[*]: Concurrent](
    requestToResponse: => Option[Request[F] => F[HttpResponse[F]]]
  ): HttpClient[F] = new HttpClient[F] with Http4sDsl[F] {

    override def send[A: Decoder](request: Request[F]): F[A] = {
      import org.http4s.circe.CirceEntityDecoder.*
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
          MonadThrow[F].raiseError(UnexpectedStatus(res.status, request.method, request.uri))
      }
    }

    override def sendWith[A: Decoder](request: F[Request[F]]): F[A] =
      request.flatMap(send[A](_))

    override def sendAndHandle[A](request: Request[F])(handler: HttpClient.HttpResponse[F] => F[A]): F[A] =
      for {
        toResponse <- StubToolsCats.stub[F](requestToResponse)
        response   <- toResponse(request)
        result     <- handler(response)
      } yield result

    override def sendWithAndHandle[A](request: F[Request[F]])(handler: HttpClient.HttpResponse[F] => F[A]): F[A] =
      for {
        req    <- request
        result <- sendAndHandle(req)(handler)
      } yield result
  }
}
