package refined4s

import scala.annotation.targetName
import scala.quoted.{Expr, Quotes}
import scala.util.control.NonFatal
import scala.compiletime.*
import scala.quoted.*

/** @author Kevin Lee
  * @since 2023-04-23
  */
object strings {

  type NonEmptyString = NonEmptyString.Type
  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  object NonEmptyString extends Refined[String] {

    override inline def invalidReason(a: String): String =
      "It has to be a non-empty String value but got [" + a + "]"

    inline override def predicate(a: String): Boolean = a != ""

    extension (thisNes: NonEmptyString) {
      @targetName("plus")
      def ++(thatNes: NonEmptyString): NonEmptyString = NonEmptyString.unsafeFrom(thisNes.value + thatNes.value)
    }
  }

  type Uri = Uri.Type
  object Uri extends InlinedRefined[String] {

    override def invalidReason(a: String): String =
      "It has to be a URI but got [" + a + "]"

    override inline def inlinedInvalidReason(inline a: String): String =
      "It has to be a URI but got [" + a + "]"

    override def predicate(a: String): Boolean =
      try {
        new java.net.URI(a)
        true
      } catch {
        case NonFatal(_) =>
          false
      }

    override inline def inlinedPredicate(inline uri: String): Boolean = ${ validateUri('uri) }

    private def validateUri(uriExpr: Expr[String])(using Quotes): Expr[Boolean] = {
      import quotes.reflect.*
      uriExpr.asTerm match {
        case Inlined(_, _, Literal(StringConstant(uriStr))) =>
          try {
            new java.net.URI(uriStr)
            Expr(true)
          } catch {
            case _: Throwable => Expr(false)
          }
        case _ =>
          report.error(
            """Uri must be a string literal.
              |If it's unknown in compile-time, use `Uri.from` or `Uri.unsafeFrom` instead.
              |(unsafeFrom is not recommended)""".stripMargin,
            uriExpr,
          )
          Expr(false)
      }
    }

  }
}
