package poly.collection.mut

import poly.algebra._
import poly.collection._
import poly.collection.builder._
import poly.collection.conversion.FromScala._
import poly.collection.factory._
import poly.collection.impl.hashtable._
import scala.collection.JavaConverters._

/**
 * Represents a map backed by an open hash table.
 * @since 0.1.0
 * @author Tongfei Chen
 */
class HashMap[K: Hashing, V] private(private val data: OpenHashTable[K, HashMap.Entry[K, V]]) extends KeyMutableMap[K, V] {

  import HashMap._

  val eqOnKeys = implicitly[Hashing[K]]

  def apply(k: K): V = data.locate(k).value

  def containsKey(k: K): Boolean = data.locate(k) != null

  def update(k: K, v: V): Unit = data.locate(k).value = v

  def ?(k: K): Option[V] = {
    val e = data.locate(k)
    if (e != null) Some(e.value) else None
  }

  def addInplace(k: K, v: V): Unit = {
    val e = data.locate(k)
    if (e != null) e.value = v
    else data.addEntry(new Entry(k, v))
  }

  def removeInplace(k: K): Unit = data.removeEntry(k)

  def clear(): Unit = data.clear()

  override def size = data.size

  def pairs = data.entries.map(e => e.key → e.value).withKnownSize(size)

}

object HashMap extends BuilderFactory2Ev[HashMap, Hashing] {

  private[poly] class Entry[K, V](val key: K, var value: V) extends OpenHashEntryLike[K, Entry[K, V]]

  implicit def newBuilder[K: Hashing, V]: Builder[(K, V), HashMap[K, V]] = new Builder[(K, V), HashMap[K, V]] {
    private[this] val ht = new OpenHashTable[K, Entry[K, V]]()
    private[this] val m = new HashMap(ht)
    override def sizeHint(n: Int) = ht.grow(n)
    def addInplace(x: (K, V)) = m.addInplace(x)
    def result = m
  }
}
