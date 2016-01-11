package poly.collection

import poly.algebra._
import poly.collection.ops._

/**
  * Represents a multiset in which the same element can appear more than once.
  * @author Tongfei Chen
  * @since 0.1.0
  */
trait Multiset[T] extends KeyedStructure[T, Multiset[T]] { self =>

  def equivOnKey: Equiv[T]

  def keys: Iterable[T]

  def keySet: Set[T] = new AbstractSet[T] {
    def equivOnKey = self.equivOnKey
    def keys = self.keys
    def contains(x: T) = self.contains(x)
  }

  def contains(x: T): Boolean

  def notContains(x: T): Boolean = !contains(x)

  def containsKey(x: T) = contains(x)

  def multiplicity(x: T): Int

  def elements: Iterable[T] = keys.flatMap(k => k.repeat(multiplicity(k)))

  def filterKeys(p: T => Boolean): Multiset[T] = ???

  def keyFreqMap: Map[T, Int] = new AbstractMap[T, Int] {
    def pairs = self.keys.map(k => k → self.multiplicity(k))
    def containsKey(x: T) = self.contains(x)
    def apply(k: T) = self.multiplicity(k)
    def ?(k: T) = if (self.contains(k)) Some(self.multiplicity(k)) else None
    def equivOnKey = self.equivOnKey
}

  // HELPER FUNCTIONS

  def foreach[U](f: T => U) = elements foreach f

  def fold[U >: T](z: U)(f: (U, U) => U) = elements.fold(z)(f)

  def foldByMonoid[U >: T : Monoid] = elements.foldByMonoid[U]

  def reduce[U >: T](f: (U, U) => U) = elements reduce f

  def reduceBySemigroup[U >: T : Semigroup] = elements.reduceBySemigroup[U]

  def forall(f: T => Boolean) = elements forall f

  def exists(f: T => Boolean) = elements exists f

  def sum[U >: T : AdditiveCMonoid] = elements.sum[U]

  def max[U >: T : WeakOrder] = elements.max

  def min[U >: T : WeakOrder] = elements.min

  def minAndMax[U >: T : WeakOrder] = elements.minAndMax

  def |(that: Multiset[T]): Multiset[T] = new AbstractMultiset[T] {
    def equivOnKey = self.equivOnKey
    def multiplicity(x: T) = math.max(self.multiplicity(x), that.multiplicity(x))
    def keys = ???
    def contains(x: T) = ???
  }

}

abstract class AbstractMultiset[T] extends Multiset[T]
