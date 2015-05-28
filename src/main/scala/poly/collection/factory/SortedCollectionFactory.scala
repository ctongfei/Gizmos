package poly.collection.factory

import poly.algebra._
import poly.collection._
import poly.collection.conversion._
import scala.reflect._

/**
 * @author Tongfei Chen (ctongfei@gmail.com).
 */
trait SortedCollectionFactory[C[_]] {

  /** Returns a new builder of this collection type. */
  implicit def newBuilder[T: ClassTag: WeakOrder]: CollectionBuilder[T, C]

  /** Creates an empty collection. */
  def empty[T: ClassTag: WeakOrder]: C[T] = newBuilder[T].result

  /** Creates a collection by adding the arguments into it. */
  def apply[T: ClassTag: WeakOrder](xs: T*): C[T] = {
    val b = newBuilder[T]
    b ++= xs
    b.result
  }

  /** Creates a collection by adding all the elements in the specific traversable sequence. */
  def from[T: ClassTag: WeakOrder](xs: Traversable[T]): C[T] = {
    val b = newBuilder[T]
    b ++= xs
    b.result
  }

  implicit def factory: SortedCollectionFactory[C] = this

}
