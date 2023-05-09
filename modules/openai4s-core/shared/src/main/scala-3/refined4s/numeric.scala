package refined4s

/** @author Kevin Lee
  * @since 2023-04-24
  */
object numeric {
  type PosInt = PosInt.Type
  object PosInt extends Refined[Int] {

    inline override def invalidReason(a: Int): String =
      "It should be a positive Int but got [" + a + "]"

    inline override def predicate(a: Int): Boolean = a > 0
  }

  type NonNegFloat = NonNegFloat.Type
  object NonNegFloat extends Refined[Float] {
    override def invalidReason(a: Float): String =
      "It should be a non-negative Float but got [" + a + "]"

    override def predicate(a: Float): Boolean = a >= 0f
  }
}
