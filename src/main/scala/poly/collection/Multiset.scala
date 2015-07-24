package poly.collection

import poly.algebra._

/**
 * Basic trait for multisets.
 * @author Tongfei Chen (ctongfei@gmail.com).
 * @since 0.1.0
 */
trait Multiset[T] extends PredicateSet[T] with Enumerable[T] { self =>

  /** Returns the multiplicity (number of occurrence) of an element in this multiset. */
  def multiplicity(x: T): Int

  /** Tests if an element belongs to this set. */
  def contains(x: T): Boolean

  /** Returns the union of two sets. */
  def |(that: Multiset[T]): Multiset[T] = ???

  /** Returns the intersection of two sets. */
  def &(that: Multiset[T]): Multiset[T] = ???

  /** Returns the difference of two sets. */
  def \(that: Multiset[T]): Multiset[T] = ???

  /** Tests if this set is a subset of another set. */
  def <=(that: Multiset[T]): Boolean = this.forall(x => this.multiplicity(x) <= that.multiplicity(x))

  /** Tests if this set is a strict subset of another set. */
  def <(that: Multiset[T]): Boolean =
    this.forall(x => this.multiplicity(x) <= that.multiplicity(x)) &&
      that.exists(x => this.multiplicity(x) < that.multiplicity(x))

  /** Tests if this set is a strict superset of another set. */
  def >(that: Multiset[T]): Boolean = that < this

  /** Tests if this set is a superset of another set. */
  def >=(that: Multiset[T]): Boolean = that <= this

  override def equals(that: Any) = that match {
    case that: Multiset[T] =>
      this.forall(x => this.multiplicity(x) == that.multiplicity(x)) &&
      that.forall(x => this.multiplicity(x) == that.multiplicity(x))
    case _ => false
  }
}

object Multiset {

  def empty[T]: Multiset[T] = Set.empty[T]

  implicit def Lattice[T]: Lattice[Multiset[T]] with BoundedLowerSemilattice[Multiset[T]] =
    new Lattice[Multiset[T]] with BoundedLowerSemilattice[Multiset[T]] {
      def zero = empty[T]
      def inf(x: Multiset[T], y: Multiset[T]): Multiset[T] = x & y
      def sup(x: Multiset[T], y: Multiset[T]): Multiset[T] = x | y
  }

}
