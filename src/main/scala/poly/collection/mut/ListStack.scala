package poly.collection.mut

import poly.collection._
import poly.collection.factory._
import poly.collection.impl._
import poly.util.specgroup._

/**
 * @author Tongfei Chen (ctongfei@gmail.com).
 */
class ListStack[T] (private var data: SinglyLinkedList[T]) extends Queue[T] {

  def size = data.len

  def push(x: T): Unit = data.prependInplace(x)

  def top: T = data.dummy.next.data

  def pop(): T = {
    val t = top
    data.deleteAt(0)
    t
  }

}

object ListStack extends CollectionFactory[ListStack] {

  implicit def newBuilder[T]: Builder[T, ListStack[T]] = new Builder[T, ListStack[T]] {
    var data: SinglyLinkedList[T] = null
    def sizeHint(n: Int) = {}
    def +=(x: T) = data.prependInplace(x)
    def result = new ListStack[T](data)
  }

}
