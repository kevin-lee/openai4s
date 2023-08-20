package refined4s

import scala.compiletime.*

/** @author Kevin Lee
  * @since 2023-04-24
  */
object numeric {
  type PosInt = PosInt.Type
  object PosInt extends InlinedRefined[Int] {

    override inline def inlinedInvalidReason(inline a: Int): String =
      "It should be a positive Int but got [" + codeOf(a) + "]"

    override inline def inlinedPredicate(inline a: Int): Boolean = a > 0

    inline override def invalidReason(a: Int): String =
      "It should be a positive Int but got [" + a + "]"

    inline override def predicate(a: Int): Boolean = a > 0
  }

  type NonNegInt = NonNegInt.Type
  object NonNegInt extends InlinedRefined[Int] {
    override inline def inlinedInvalidReason(inline a: Int): String =
      "It should be a positive Int but got [" + codeOf(a) + "]"

    override inline def inlinedPredicate(inline a: Int): Boolean = a >= 0

    override def invalidReason(a: Int): String =
      "It should be a positive Int but got [" + a + "]"

    override def predicate(a: Int): Boolean = a >= 0
  }

  type NonNegFloat = NonNegFloat.Type
  object NonNegFloat extends InlinedRefined[Float] {

    override inline def inlinedInvalidReason(inline a: Float): String =
      "It should be a non-negative Float but got [" + codeOf(a) + "]"

    override inline def inlinedPredicate(inline a: Float): Boolean = a >= 0f

    override def invalidReason(a: Float): String =
      "It should be a non-negative Float but got [" + a + "]"

    override def predicate(a: Float): Boolean = a >= 0f
  }
}
