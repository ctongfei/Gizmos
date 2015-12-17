package poly.collection

import poly.algebra._
import poly.algebra.hkt._
import poly.collection.exception._
import poly.util.specgroup._
import scala.language.reflectiveCalls

/**
 * The base trait for maps.
 * A map is a mapping between a key type (domain) and a value type (codomain).
 * It can also be viewed as a collection of (key, value) pairs, in which each key is unique.
 * @author Tongfei Chen
 * @since 0.1.0
  *
  * @define LAZY The resulting collection will be lazily evaluated.
  * @define EAGER The resulting collection will be eagerly evaluated.
  * @define Onlogn Time complexity: O(n log n).
  * @define On Time complexity: O(n).
  * @define Ologn Time complexity: O(log n).
  * @define O1amortized Time complexity: Amortized O(1).
  * @define O1 Time complexity: O(1).
 */
trait Map[@sp(i) K, +V] extends KeyedStructure[K, Map[K, V]] with PartialFunction[K, V] { self =>

  /**
   * Returns all key-value pairs stored in this map.
   * @return An iterable sequence of key-value pairs.
   */
  def pairs: Iterable[(K, V)]

  /**
   * Optionally retrieves the value associated with the specified key.
   * @param k The given key
   * @return The associated value. If the key is not found, return [[None]].
   */
  def ?(k: K): Option[V]

  /**
   * Retrieves the value associated with the specified key.
   * If the key is not found, its behavior is undefined (this is a deliberate design for efficiency).
   * For maximum safety, use `?` to optionally access an element.
   * @param k The given key
   * @return The associated value
   * @throws KeyNotFoundException if key not found (may or may not throw)
   */
  def apply(k: K): V

  /** Returns the number of (key, value) pairs this map contains. */
  def size: Int

  /**
   * Checks if the specified key is present in this map.
   * @param x The given key
   * @return Whether the key exists in this map
   */
  def containsKey(x: K): Boolean

  def notContainsKey(x: K) = !containsKey(x)

  def getOrElse[V1 >: V](x: K, default: => V1) = ?(x) match {
    case Some(y) => y
    case None => default
  }

  def isDefinedAt(x: K) = containsKey(x)

  /** Returns the set of the keys of this map. $LAZY $O1 */
  def keySet: Set[K] = new AbstractSet[K] {
    def equivOnKey = self.equivOnKey
    def contains(x: K): Boolean = self.containsKey(x)
    override def size: Int = self.size
    def elements: Iterable[K] = self.pairs.map(_._1)
  }

  /** Returns an iterable collection of the keys in this map. $LAZY $O1 */
  def keys = self.pairs.map(_._1)

  /** Returns an iterable collection of the values in this map. $LAZY $O1 */
  def values = self.pairs.map(_._2)

  // HELPER FUNCTIONS

  def filterKeys(f: K => Boolean): Map[K, V] = new AbstractMap[K, V] {
    def apply(k: K) = if (!f(k)) throw new KeyNotFoundException(k) else self(k)
    def ?(k: K) = if (!f(k)) None else self ? k
    def pairs = self.pairs.filter { case (k, _) => f(k) }
    def size = pairs.size
    def containsKey(k: K) = if (!f(k)) false else self.containsKey(k)
    def equivOnKey = self.equivOnKey
  }

  /**
   * Transforms the values of this map according to the specified function. $LAZY
   * @note This function is equivalent to the Scala library's `mapValues`.
   *       To transform all pairs in this map, use `this.pairs.map`.
   * @example {{{Map(1 -> 2, 2 -> 3) map {_ * 2} == Map(1 -> 4, 2 -> 6)}}}
   * @param f The specific function
   * @return A map view that maps every key of this map to `f(this(key))`.
   */
  def map[V1](f: V => V1): Map[K, V1] = new AbstractMap[K, V1] {
    def equivOnKey = self.equivOnKey
    def containsKey(x: K) = self.containsKey(x)
    def ?(x: K) = (self ? x).map(f)
    def apply(x: K) = f(self(x))
    def pairs = self.pairs.map { case (k, v) => (k, f(v)) }
    def size = self.size
  }

  def cartesianProduct[K1, V1](that: Map[K1, V1]): Map[(K, K1), (V, V1)] = new AbstractMap[(K, K1), (V, V1)] {
    def equivOnKey = ??? // TODO: poly-algebra: Equiv.product
    def containsKey(k: (K, K1)) = self.containsKey(k._1) && that.containsKey(k._2)
    def ?(k: (K, K1)) = for (v ← self ? k._1; v1 ← that ? k._2) yield (v, v1)
    def apply(k: (K, K1)) = (self(k._1), that(k._2))
    def pairs = for (k ← self.keys; k1 ← that.keys) yield ((k, k1), (self(k), that(k1)))
    def size = self.size
}

  /**
   * Zips two maps with the same key type into one. $LAZY
   * @note This function is not the same as the Scala library's `zip`. Please
   *       use `this.pairs.zip` instead for zipping a sequence of pairs.
   * @example {{{Map(1 -> 2, 2 -> 3) zip Map(2 -> 5, 3 -> 6) == Map(2 -> (3, 5))}}}
   * @param that Another map to be zipped
   */
  def zip[W](that: Map[K, W]): Map[K, (V, W)] = new AbstractMap[K, (V, W)] {
    def equivOnKey = self.equivOnKey
    def apply(x: K): (V, W) = (self(x), that(x))
    def ?(x: K): Option[(V, W)] = for {v ← self ? x; w ← that ? x} yield (v, w)
    def pairs = self.pairs filter { case (k, v) => that containsKey k } map { case (k, v) => (k, (v, that(k))) }
    def size: Int = pairs.size
    def containsKey(x: K): Boolean = self.containsKey(x) && that.containsKey(x)
  }

  def |>[W](f: V => W) = self map f
  def |~|[W](that: Map[K, W]) = self zip that
}

object Map {
  /** Returns the functor on maps. */
  implicit def Functor[K]: Functor[({type λ[+V] = Map[K, V]})#λ] = new Functor[({type λ[+V] = Map[K, V]})#λ] {
    def map[X, Y](mx: Map[K, X])(f: X => Y): Map[K, Y] = mx map f
  }

}

abstract class AbstractMap[@sp(i) K, +V] extends Map[K, V]
