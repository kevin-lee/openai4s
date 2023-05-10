package hedgehog.cats

import cats.Applicative
import hedgehog.Gen
import hedgehog.core.GenT

/** @author Kevin Lee
  * @since 2023-05-09
  */
object instances {
  implicit val genApplicative: Applicative[Gen] = new Applicative[Gen] {
    override def pure[A](x: A): Gen[A] = GenT.GenApplicative.point(x)

    override def ap[A, B](ff: Gen[A => B])(fa: Gen[A]): Gen[B] = GenT.GenApplicative.ap(fa)(ff)
  }
}
