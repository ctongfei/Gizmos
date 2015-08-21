package poly.collection.impl

import poly.collection._
import poly.collection.mut._
import poly.collection.node._

/**
 * A doubly linked list.
 * @author Tongfei Chen (ctongfei@gmail.com).
 */
class DoublyLinkedList[T] extends BiSeq[T] with KeyMutableSeq[T] {

  /**
   * Type of the internal node of a linked list.
   * @param data Data held in this node
   * @param prev The previous node
   * @param next The next node
   */
  class Node (var data: T, var prev: Node = null, var next: Node = null) extends BiSeqNode[T]

  private[poly] val dummy = new Node(default[T])
  private[poly] var len: Int = 0
  dummy.prev = dummy
  dummy.next = dummy

  def length = len

  /**
   * Locates the ''i''th element in a doubly linked list.
   * @param i Index
   * @return The node that contains the ''i''-th element.
   */
  def locate(i: Int): Node = { //TODO: for i >= length / 2, find backwards for faster speed
    if (i < 0 || i >= len) throw new IndexOutOfBoundsException
    var curr = dummy.next
    var j = 0
    while (j < i) {
      curr = curr.next
      j += 1
    }
    curr
  }

  /**
   * Appends an element to the end of the doubly linked list.
   * @param x The element to be appended
   */
  def inplaceAppend(x: T) = {
    val node = new Node(x, dummy.prev, dummy)
    node.prev.next = node
    node.next.prev = node
    len += 1
  }

  def inplacePrepend(x: T) = {
    val node = new Node(x, dummy, dummy.next)
    node.prev.next = node
    node.next.prev = node
    len += 1
  }

  def apply(i: Int) = locate(i).data

  def update(i: Int, x: T) = {
    val node = locate(i)
    node.data = x
  }

  def insertAt(i: Int, x: T) = {
    val p = locate(i)
    val node = new Node(x, p.prev, p)
    node.prev.next = node
    node.next.prev = node
    len += 1
  }

  def clear() = {
    dummy.prev = dummy
    dummy.next = dummy
  }

  def deleteAt(i: Int) = {
    val p = locate(i)
    p.prev.next = p.next
    p.next.prev = p.prev
    len -= 1
  }

  def headNode: BiSeqNode[T] = dummy.next

  def lastNode: BiSeqNode[T] = dummy.prev

  def inplaceReverse(): Unit = {
    ???
  }

  override def inplaceMap(f: T => T): Unit = {
    ???
  }
}

object DoublyLinkedList {


}