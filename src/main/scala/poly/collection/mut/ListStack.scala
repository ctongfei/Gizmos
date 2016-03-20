package poly.collection.mut

import poly.collection._
import poly.collection.builder._
import poly.collection.factory._
import poly.collection.impl._

/**
 * A stack backed by a singly linked list.
 *
 * @author Tongfei Chen
 * @since 0.1.0
 */
class ListStack[T] private(private var data: SinglyLinkedList[T]) extends Queue[T] with HasKnownSize {

  override def size = data.len

  def push(x: T): Unit = data.prependInplace(x)

  def top: T = data.dummy.next.data

  def pop(): T = {
    val t = top
    data.deleteAt(0)
    t
  }

  def elements = data

}

object ListStack extends Factory[ListStack] {

  implicit def newBuilder[T]: Builder[T, ListStack[T]] = new Builder[T, ListStack[T]] {
    var data: SinglyLinkedList[T] = null
    def sizeHint(n: Int) = {}
    def add(x: T) = data.prependInplace(x)
    def result = new ListStack[T](data)
  }

}
