package poly.collection.impl

import poly.algebra._
import poly.algebra.ops._
import poly.collection._
import poly.collection.exception._
import poly.collection.mut._

/**
 * An implementation of a binary min-heap.
 * The least element under the specific order will surface at the top of the heap.
 *
 * @author Tongfei Chen (ctongfei@gmail.com).
 */
class BinaryHeap[T](val data: ResizableSeq[T])(implicit val order: WeakOrder[T]) extends PriorityQueue[T] {

  import BinaryTree._

  @inline private[this] def smallerChildIndex(x: Int) = {
    val l = 2 * x + 1
    if (l < data.fastLength - 1 && data(l + 1) < data(l)) l + 1 else l
  }

  def siftUp(i: Int): Unit = {
    var p = i
    val t = data(p)
    while (p > 0 && t < data(parentIndex(p))) {
      data(p) = data(parentIndex(p))
      p = parentIndex(p)
    }
    data(p) = t
  }

  def siftDown(i: Int): Unit = {
    var p = i
    var c = smallerChildIndex(p)
    val t = data(p)
    while (c < data.fastLength && t > data(c)) {
      data(p) = data(c)
      p = c
      c = smallerChildIndex(p)
    }
    data(p) = t
  }

  def push(x: T): Unit = {
    data.appendInplace(x)
    siftUp(data.fastLength - 1)
  }

  def pop(): T = {
    val front = data(0)
    data.swapInplace(0, data.fastLength - 1)
    data.deleteAt(data.fastLength - 1)
    if (data.fastLength > 1) siftDown(0)
    front
  }

  def top = if (size <= 0) throw new QueueEmptyException else data(0)

  def newIterator = data.newIterator

  override def size: Int = data.fastLength

}

