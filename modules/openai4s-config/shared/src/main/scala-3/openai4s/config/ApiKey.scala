package openai4s.config

import cats.syntax.all.*
import cats.{Eq, Show}
import refined4s.types.all.NonEmptyString
import extras.render.Render

/** @author Kevin Lee
  * @since 2023-04-04
  */
final class ApiKey private (private val value: NonEmptyString) {

  override def hashCode(): Int = value.hashCode()

  override def equals(obj: Any): Boolean = obj match {
    case that: ApiKey => this.value.value === that.value.value
    case _ => false
  }

  override val toString: String = "***PROTECTED***"
}

object ApiKey {
  def apply(value: NonEmptyString): ApiKey = new ApiKey(value)

  given apiKeyEq: Eq[ApiKey] = Eq[String].contramap(_.value.value)

  given apiKeyRender: Render[ApiKey] = Render[String].contramap(_.value.value)

  given apiKeyShow: Show[ApiKey] = _ => "***PROTECTED***"

}
