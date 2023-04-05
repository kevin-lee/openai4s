package openai4s.config

import cats.syntax.all.*
import cats.{Eq, Show}
import eu.timepit.refined.cats.*
import eu.timepit.refined.types.string.NonEmptyString
import org.http4s.headers.Authorization
import org.http4s.{AuthScheme, Credentials, Request}

/** @author Kevin Lee
  * @since 2023-04-04
  */
final class ApiKey private (private val value: NonEmptyString) {

  override def hashCode(): Int = value.hashCode()

  override def equals(obj: Any): Boolean = obj match {
    case that: ApiKey => this.value === that.value
    case _ => false
  }

  override val toString: String = "***PROTECTED***"
}

object ApiKey {
  def apply(value: NonEmptyString): ApiKey = new ApiKey(value)

  implicit val apiKeyEq: Eq[ApiKey] = Eq.by(_.value)

  implicit val apiKeyShow: Show[ApiKey] = _ => "***PROTECTED***"

  implicit class ApiKeyOps(private val apiKey: ApiKey) extends AnyVal {

    def setApiKeyHeader[F[*]](request: Request[F]): Request[F] =
      request.putHeaders(
        Authorization(Credentials.Token(AuthScheme.Bearer, apiKey.value.value))
      )
  }
}
