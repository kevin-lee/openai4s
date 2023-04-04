package openai4s.http

/** @author Kevin Lee
  * @since 2023-04-01
  */
import cats.syntax.all.*
import org.http4s.*

import scala.annotation.tailrec

@SuppressWarnings(Array("org.wartremover.warts.Null"))
sealed abstract class HttpError[F[*]](request: Request[F], cause: Option[Exception])
    extends Exception(
      s"Error when sending request - ${request.method.name} ${request.uri.renderString}",
      cause.orNull,
    )

object HttpError {

  final case class ConnectionError[F[*]](request: Request[F], cause: Exception)
      extends HttpError[F](request, cause.some)

  final case class ResponseError[F[*]](request: Request[F], cause: Exception) extends HttpError[F](request, cause.some)

  final case class DecodingError[F[*]](request: Request[F], cause: DecodeFailure)
      extends HttpError[F](request, cause.some)

  final case class UnexpectedStatus[F[*]](request: Request[F], status: Status, body: Option[String])
      extends HttpError(request, none)

  def connectionError[F[*]](request: Request[F], cause: Exception): HttpError[F] = ConnectionError(request, cause)

  def responseError[F[*]](request: Request[F], cause: Exception): HttpError[F] = ResponseError(request, cause)

  def decodingError[F[*]](request: Request[F], cause: DecodeFailure): HttpError[F] = DecodingError(request, cause)

  def unexpectedStatus[F[*]](request: Request[F], status: Status, body: Option[String]): HttpError[F] =
    UnexpectedStatus(request, status, body)

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
