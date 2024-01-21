package openai4s.config

import cats.syntax.all.*
import cats.{Eq, Show}
import eu.timepit.refined.cats.*
import extras.render.Render
import extras.render.refined.*
import refined4s.compat.RefinedCompatAllTypes.*

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

  implicit val apiKeyRender: Render[ApiKey] = Render[NonEmptyString].contramap(_.value)

  implicit val apiKeyShow: Show[ApiKey] = _ => "***PROTECTED***"

}
