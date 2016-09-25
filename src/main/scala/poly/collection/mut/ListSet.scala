package poly.collection.mut

import poly.algebra._
import poly.algebra.ops._
import poly.collection._
import poly.collection.builder._
import poly.collection.factory._
import poly.collection.impl._

/**
 * A set backed by a singly-linked list.
 * @since 0.1.0
 * @author Tongfei Chen
 */
class ListSet[T] private(private val data: ListSeq[T])(implicit val keyEq: Eq[T]) extends KeyMutableSet[T] {

  override def size = data.len

  def contains(x: T): Boolean = {
    var found = false
    var c = data.dummy.next
    while (c ne data.dummy) {
      if (c.data === x)
        return true
      c = c.next
    }
    false
  }


  def clear() = data.clear()

  def addInplace(x: T) = {
    if (!contains(x)) data.prependInplace(x)
  }

  def removeInplace(x: T) = {
    var p = data.dummy
    var c = data.dummy.next
    while (c ne data.dummy) {
      if (c.data === x) p.next = c.next
      p = c
      c = c.next
    }
  }

  def keys: Seq[T] = data
}

object ListSet extends BuilderFactoryA_EvA[ListSet, Eq] {

  implicit def newBuilder[K: Eq]: Builder[K, ListSet[K]] = new Builder[K, ListSet[K]] {
    private[this] val s = new ListSet(ListSeq[K]())
    def addInplace(x: K) = s addInplace x
    def result = s
  }

}