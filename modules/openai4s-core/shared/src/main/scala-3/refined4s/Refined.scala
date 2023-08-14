package refined4s

import scala.compiletime.*

/** Copied from https://gist.github.com/kevin-lee/158d3d5c3e036560f8962087a34c95c5 and modified.
  * @author Kevin Lee
  * @since 2022-03-23
  */

trait Refined[A] extends RefinedBase[A] {

  inline def apply(a: A): Type =
    inline if predicate(a) then a.asInstanceOf[Type] // scalafix:ok DisableSyntax.asInstanceOf
    else error("Invalid value: [" + codeOf(a) + "]. " + invalidReason(a))

}
