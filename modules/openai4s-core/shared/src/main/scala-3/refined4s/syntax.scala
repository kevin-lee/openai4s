package refined4s

import refined4s.strings.NonEmptyString

import scala.quoted.*

/** @author Kevin Lee
  * @since 2023-09-16
  */
trait syntax {

  extension (inline a: String) {
    inline def nes: NonEmptyString = ${ syntax.applyImpl('a) }
  }

}
object syntax extends syntax {

  @SuppressWarnings(Array("org.wartremover.warts.TripleQuestionMark"))
  private[syntax] def applyImpl(str: Expr[String])(using Quotes): Expr[NonEmptyString] =
    str match {
      case Expr(value: String) =>
        if (value.nonEmpty) '{ NonEmptyString.unsafeFrom(${ Expr(value) }) }
        else {
          quotes.reflect.report.error("String is empty. It has to be a non-empty String value.")
          '{ ??? }
        }
      case _ =>
        quotes.reflect.report.error("Not a constant string")
        '{ ??? }
    }
}
