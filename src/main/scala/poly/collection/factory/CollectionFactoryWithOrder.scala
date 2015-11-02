package poly.collection.factory

import poly.algebra._
import poly.collection._
import poly.collection.conversion.Scala._
import scala.language.higherKinds

/**
 * @author Tongfei Chen (ctongfei@gmail.com).
 */
trait CollectionFactoryWithOrder[+C[_]] {

  /** Returns a new builder of this collection type. */
  implicit def newBuilder[T: WeakOrder]: Builder[T, C[T]]

  /** Creates an empty collection. */
  def empty[T: WeakOrder]: C[T] = newBuilder[T].result

  /** Creates a collection by adding the arguments into it. */
  def apply[T: WeakOrder](xs: T*): C[T] = {
    val b = newBuilder[T]
    b ++= xs
    b.result
  }

  /** Creates a collection by adding all the elements in the specific traversable sequence. */
  def from[T: WeakOrder](xs: Traversable[T]): C[T] = {
    val b = newBuilder[T]
    b ++= xs
    b.result
  }

  //implicit def factory: CollectionFactoryWithOrder[C] = this

}
