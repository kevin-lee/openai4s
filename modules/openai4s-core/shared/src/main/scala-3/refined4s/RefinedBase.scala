package refined4s

/** Copied from https://gist.github.com/kevin-lee/158d3d5c3e036560f8962087a34c95c5 and modified.
  * @author Kevin Lee
  * @since 2022-03-23
  */
trait RefinedBase[A] {

  opaque type Type = A

  given newRefinedCanEqual: CanEqual[Type, Type] = CanEqual.derived

  def unapply(typ: Type): Option[A] = Some(typ)

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def from(a: A): Either[String, Type] =
    if predicate(a) then Right(a) else Left("Invalid value: [" + a.toString + "]. " + invalidReason(a))

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def unsafeFrom(a: A): Type =
    from(a).fold(err => throw new IllegalArgumentException(err), identity) // scalafix:ok DisableSyntax.throw

  def invalidReason(a: A): String

  def predicate(a: A): Boolean

  extension (typ: Type) {
    inline def value: A = typ
  }
}