package poly.collection.factory

import poly.collection._
import poly.collection.conversion.FromScala._
import scala.language.higherKinds

/**
 * @author Tongfei Chen
 */
trait BuilderFactory2Ev1[+C[_, _], Ev[_]] extends Factory2Ev1[C, Ev] {

  implicit def ground[A: Ev, B] = GroundedFactory ofBuilder newBuilder[A, B]

  def newBuilder[A: Ev, B]: Builder[(A, B), C[A, B]]

  override def empty[A: Ev, B] = newBuilder[A, B].result

  def from[A: Ev, B](xs: Traversable[(A, B)]) = {
    val b = newBuilder[A, B]
    if (xs.sizeKnown) b.sizeHint(xs.size)
    b addAll xs
    b.result
  }
}

trait Factory2Ev1[+C[_, _], Ev[_]] {

  def empty[A: Ev, B]: C[A, B] = from(Traversable.empty)

  def apply[A: Ev, B](xs: (A, B)*): C[A, B] = from(xs)

  def from[A: Ev, B](xs: Traversable[(A, B)]): C[A, B]
}
