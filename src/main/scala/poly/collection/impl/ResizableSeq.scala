package poly.collection.impl

import poly.collection._
import poly.collection.factory._
import poly.collection.mut._
import poly.collection.node._

/**
 * A resizable array. This serves as the implementation container of `ArrayX` classes.
 * Performance:
 *
 *  - Accessing by index: O(1)
 *  - Appending: amortized O(1)
 *  - Prepending: O(''n'')
 *  - Insertion at any index: O(''n'')
 *  - Deletion at any index: O(''n'')
 *
 * This class serves as the basic building block for a series of structures in Poly-collection.
 *
 * @author Tongfei Chen
 */
final class ResizableSeq[T]
  (private[this] var cap: Int = Settings.ArrayInitialSize) extends KeyMutableSeq[T] with ValueMutableIndexedSeq[T]
{ self =>

  private[poly] var data: Array[AnyRef] = Array.ofDim[AnyRef](math.max(nextPowerOfTwo(cap), Settings.ArrayInitialSize))
  private[poly] var len: Int = 0

  case class Node(i: Int) extends BidiSeqNode[T] {
    override val isDummy = i < 0 || i >= len
    def data = self.data(i).asInstanceOf[T]
    def prev = new Node(i - 1)
    def next = new Node(i + 1)
  }

  private[poly] def ensureCapacity(minCapacity: Int): Unit = {
    if (cap < minCapacity) {
      val newCapacity = nextPowerOfTwo(minCapacity)
      val newData = Array.ofDim[AnyRef](newCapacity)
      System.arraycopy(data, 0, newData, 0, cap)
      data = newData
      cap = newCapacity
    }
  }

  private[poly] def grow() = ensureCapacity(cap * 2)

  def fastApply(i: Int) = data(i).asInstanceOf[T]

  private[poly] def capacity = cap

  def fastLength = len

  def update(i: Int, x: T) = data(i) = x.asInstanceOf[AnyRef]

  def clear_!() = len = 0

  def insert_!(i: Int, x: T) = {
    if (cap < len + 1) ensureCapacity(len + 1)
    System.arraycopy(data, i, data, i + 1, len - i)
    data(i) = x.asInstanceOf[AnyRef]
    len += 1
  }

  def delete_!(i: Int): Unit = {
    System.arraycopy(data, i + 1, data, i, len - i - 1)
    len -= 1
  }

  def moveInplace(i: Int, j: Int, k: Int): Unit = {
    System.arraycopy(data, i, data, k, j - i)
  }

  def prepend_!(x: T) = insert_!(0, x)

  def append_!(x: T) = {
    if (cap < len + 1) ensureCapacity(len + 1)
    data(len) = x.asInstanceOf[AnyRef]
    len += 1
  }

  private[poly] def appendUnchecked(x: T) = {
    data(len) = x.asInstanceOf[AnyRef]
    len += 1
  }
}

object ResizableSeq extends SeqFactory[ResizableSeq] {
  def newBuilder[T]: Builder[T, ResizableSeq[T]] = new Builder[T, ResizableSeq[T]] {
    val a = new ResizableSeq[T]()
    override def sizeHint(n: Int) = a.ensureCapacity(n)
    def add(x: T) = a.append_!(x)
    def result = a
  }

}
