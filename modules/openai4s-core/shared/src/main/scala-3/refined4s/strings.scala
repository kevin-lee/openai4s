package refined4s

import scala.annotation.targetName
import scala.util.control.NonFatal

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
  object Uri extends Refined[String] {
    override inline def invalidReason(a: String): String =
      "It has to be a URI but got [" + a + "]"

    override def predicate(a: String): Boolean =
      try {
        new java.net.URI(a)
        true
      } catch {
        case NonFatal(_) =>
          false
      }

  }
}
