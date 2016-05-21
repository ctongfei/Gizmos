package poly.collection.mut

import poly.collection._
import poly.collection.builder._
import poly.collection.exception._
import poly.collection.factory._
import poly.collection.impl._

/**
 * An array-backed circular queue that supports amortized O(1) time for both pushing and popping.
 * @author Tongfei Chen
 * @since 0.1.0
 */
class ArrayQueue[T] private(private val data: CircularArray[T]) extends Queue[T] {

  def fastSize = data.length

  def elements = IndexedSeq.tabulate(fastSize)(i => data(i))

  def top = {
    if (data.isEmpty) throw new QueueEmptyException
    data(0)
  }

  def push(x: T) = data.appendInplace(x)

  def pop(): T = {
    val x = top
    data.frontPtr = (data.frontPtr + 1) % data.capacity
    x
  }
}

object ArrayQueue extends BuilderFactory[ArrayQueue] {

  implicit def newBuilder[T]: Builder[T, ArrayQueue[T]] = new Builder[T, ArrayQueue[T]] {
    var a: ResizableSeq[T] = new ResizableSeq[T]()
    override def sizeHint(n: Int) = a.ensureCapacity(n)
    def addInplace(x: T) = a.appendInplace(x)
    def result = new ArrayQueue[T](new CircularArray[T](a))
  }

}
