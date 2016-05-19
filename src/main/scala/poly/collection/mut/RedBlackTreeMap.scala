package poly.collection.mut

import poly.algebra._
import poly.algebra.conversion.FromJava._
import poly.collection._
import poly.collection.builder._
import poly.collection.conversion.FromJava._
import poly.collection.factory._

/**
  * Represents a map backed by a red-black tree. This class is a wrapper of [[java.util.TreeMap]].
  * @author Tongfei Chen
 *  @since 0.1.0
  */
class RedBlackTreeMap[K, V] private(private val data: java.util.TreeMap[K, V])
  extends AbstractMap[K, V] with KeyMutableMap[K, V] with SortedMap[K, V] {

  def apply(k: K) = data get k

  def ?(k: K) = if (data containsKey k) Some(data get k) else None

  def addInplace(x: K, y: V) = data.put(x, y)

  def clear() = data.clear()

  def removeInplace(x: K) = data.remove(x)

  def pairs: SortedIterable[(K, V)] = new AbstractSortedIterable[(K, V)] {
    def orderOnElements = orderOnKeys contramap firstOfPair
    def newIterator = data.entrySet().elements.map(e => (e.getKey, e.getValue)).newIterator
  }

  def orderOnKeys: Order[K] = data.comparator()

  def update(x: K, y: V) = data.put(x, y)

  override def size = data.size()

  def containsKey(x: K) = data.containsKey(x)
}

object RedBlackTreeMap extends BuilderFactory2Ev[RedBlackTreeMap, Order] {

  implicit def newBuilder[K, V](implicit K: Order[K]): Builder[(K, V), RedBlackTreeMap[K, V]] =
    new Builder[(K, V), RedBlackTreeMap[K, V]] {
      private[this] val data = new java.util.TreeMap[K, V](new java.util.Comparator[K] {
        def compare(a: K, b: K) = K.cmp(a, b)
      })
      def result = new RedBlackTreeMap(data)
      def addInplace(x: (K, V)) = data.put(x._1, x._2)
    }
}
