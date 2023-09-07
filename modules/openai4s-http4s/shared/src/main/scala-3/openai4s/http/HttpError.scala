package openai4s.http

import cats.{Eq, Show}
import cats.syntax.all.*
import io.circe.Json
import org.http4s.*

import scala.annotation.tailrec

/** @author Kevin Lee
  * @since 2023-04-01
  */
@SuppressWarnings(Array("org.wartremover.warts.Null"))
enum HttpError[F[*]](request: Request[F], cause: Option[Exception])
    extends Exception(
      s"Error when sending request - ${request.method.name} ${request.uri.renderString}",
      cause.orNull,
    ) {
  case ConnectionError(request: Request[F], cause: Exception) extends HttpError[F](request, cause.some)

  case ResponseError(request: Request[F], cause: Exception) extends HttpError[F](request, cause.some)

  case DecodingError(request: Request[F], cause: DecodeFailure) extends HttpError[F](request, cause.some)

  case UnexpectedStatus(request: Request[F], status: Status, body: UnexpectedStatus.Body)
      extends HttpError[F](request, none)

}

object HttpError {

  def connectionError[F[*]](request: Request[F], cause: Exception): HttpError[F] = ConnectionError(request, cause)

  def responseError[F[*]](request: Request[F], cause: Exception): HttpError[F] = ResponseError(request, cause)

  def decodingError[F[*]](request: Request[F], cause: DecodeFailure): HttpError[F] = DecodingError(request, cause)

  def unexpectedStatus[F[*]](request: Request[F], status: Status, body: UnexpectedStatus.Body): HttpError[F] =
    UnexpectedStatus(request, status, body)

  object UnexpectedStatus {
    enum Body {
      case JsonBody(json: Json)
      case StringBody(text: String)
      case WithCause(cause: Throwable)
    }
    object Body {
      def jsonBody(json: Json): Body = JsonBody(json)

      def stringBody(text: String): Body = StringBody(text)

      def withCause(cause: Throwable): Body = WithCause(cause)

      given bodyEq: Eq[Body] = Eq.fromUniversalEquals

      given bodyShow: Show[Body] with {
        def show(body: Body): String = body match {
          case JsonBody(json) => json.noSpaces
          case StringBody(string) => string
          case WithCause(cause) => cause.getMessage
        }
      }
    }

  }

  given httpErrorShow[F[*]]: Show[HttpError[F]] with {
    def show(httpError: HttpError[F]): String = httpError match {
      case HttpError.ConnectionError(req, cause) =>
        show"ConnectionError(request=${req.method} ${req.uri}, cause=${cause.getMessage})"

      case HttpError.ResponseError(req, cause) =>
        show"ResponseError(request=${req.method} ${req.uri}, cause=${cause.getMessage})"

      case HttpError.DecodingError(req, cause) =>
        show"DecodingError(request=${req.method} ${req.uri}, cause=${cause.getMessage})"

      case HttpError.UnexpectedStatus(req, status, body) =>
        show"UnexpectedStatus(request=${req.method} ${req.uri}, status=$status, body=$body)"
    }
  }

  @tailrec
  def otherHttpException[F[*]](request: Request[F], ex: Exception): Option[HttpError[F]] = ex match {
    case e: java.net.ConnectException => connectionError(request, e).some
    case e: java.net.UnknownHostException => connectionError(request, e).some
    case e: java.net.MalformedURLException => connectionError(request, e).some
    case e: java.net.NoRouteToHostException => connectionError(request, e).some
    case e: java.net.PortUnreachableException => connectionError(request, e).some
    case e: java.net.ProtocolException => connectionError(request, e).some
    case e: java.net.URISyntaxException => connectionError(request, e).some
    case e: java.net.SocketTimeoutException => responseError(request, e).some
    case e: java.net.UnknownServiceException => responseError(request, e).some
    case e: java.net.SocketException => responseError(request, e).some
    case e: java.util.concurrent.TimeoutException => responseError(request, e).some
    case e: java.io.IOException => responseError(request, e).some
    case e =>
      e.getCause match {
        case ex: Exception => otherHttpException(request, ex)
        case _ => none
      }
  }

  def fromHttp4sException[F[*]](ex: Throwable, request: Request[F]): Option[HttpError[F]] = ex match {
    case e: org.http4s.DecodeFailure => decodingError(request, e).some
    case e: org.http4s.client.ConnectionFailure => connectionError(request, e).some
    case e: org.http4s.InvalidBodyException => responseError(request, e).some
    case e: org.http4s.InvalidResponseException => responseError(request, e).some
    case e: Exception => HttpError.otherHttpException(request, e)
    case _ => none
  }
}
