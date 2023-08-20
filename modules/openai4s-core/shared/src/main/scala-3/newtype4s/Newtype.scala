package newtype4s

import refined4s.*

/** @author Kevin Lee
  * @since 2023-04-23
  */
trait Newtype[A] {
  opaque type Type = A

  inline protected def toType(a: A): Type = a

  inline def apply(a: A): Type = a

  given newtypeCanEqual: CanEqual[Type, Type] = CanEqual.derived

  extension (typ: Type) {
    inline def value: A = typ
  }
}
