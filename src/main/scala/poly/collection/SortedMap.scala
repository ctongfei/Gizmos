package poly.collection

import poly.algebra._
import poly.algebra.specgroup._
import scala.annotation.unchecked.{uncheckedVariance => uv}

/**
 * Represents a map that is sorted by key when enumerated.
 * @author Tongfei Chen
 * @since 0.1.0
 */
trait SortedMap[@sp(i) K, +V] extends Map[K, V] { self =>

  /** Returns the weak order on keys. */
  def orderOnKey: WeakOrder[K]

  def equivOnKey = orderOnKey

  def pairs: SortedIterable[(K, V @uv)]

}

// Force specialization of the key (@sp(Int)) for Seq[T].
// Does not know why specialization under `Seq[T] extends SortedMap[Int, T]` does not work.
private[poly] trait IntKeyedSortedMap[+V] extends SortedMap[Int, V]
