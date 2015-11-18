package poly.collection.impl

import poly.collection._
import poly.collection.mut._
import poly.collection.node._

/**
 * A singly linked list.
 * Performance:
 *
 *  - Accessing by index: O(''n'')
 *  - Appending: amortized O(''n'')
 *  - Prepending: O(1)
 *  - Insertion at any index: O(''n'') (searching) + O(1) (insertion)
 *  - Removing at any index: O(''n'') (searching) + O(1) (removing)
 *
 * @author Tongfei Chen (ctongfei@gmail.com).
 */
class SinglyLinkedList[T] extends Seq[T] with KeyMutableSeq[T] {

  type Node = SinglyLinkedList.Node[T]

  val dummy: Node = new Node(default[T], dummy) { override def isDummy = true }
  dummy.next = dummy

  private[poly] var len: Int = 0
  private[poly] var lastNode: Node = dummy

  override def length = len

  /**
   * Locates the ''i''th element in a singly linked list.
   * @param i Index
   * @return The previous node and the node that contains the ''i''-th element.
   */
  def locate(i: Int): (Node, Node) = {
    if (i == -1) return (dummy, dummy)
    if (i < 0 || i >= len) throw new IndexOutOfBoundsException
    var curr = dummy.next
    var prev: Node = dummy
    var j = 0
    while (j < i) {
      prev = curr
      curr = curr.next
      j += 1
    }
    (prev, curr)
  }

  /**
   * Appends an element to the end of the singly linked list. $O1
   * @param x The element to be appended
   */
  def appendInplace(x: T) = {
    val node = new Node(x, dummy)
    lastNode.next = node
    lastNode = node
    len += 1
  }

  /**
   * Prepends an element to the start of the doubly linked list. $O1
   * @param x The element to be prepended.
   */
  def prependInplace(x: T) = {
    val node = new Node(x, dummy.next)
    dummy.next = node
    len += 1
  }

  /**
   * Gets the ''i''-th element.
   * @param i Index
   * @return The ''i''-th element.
   */
  override def apply(i: Int) = locate(i)._2.data

  /**
   * Sets the ''i''-th element of this doubly linked list to the specified value.
   * @param i Index
   * @param x The new value
   */
  def update(i: Int, x: T) = {
    val (_, node) = locate(i)
    node.data = x
  }

  /**
   * Inserts an element at the ''i''-th position.
   * @param i Index
   * @param x New element
   */
  def insertAt(i: Int, x: T) = {
    val (prev, curr) = locate(i)
    val node = new Node(x, curr)
    prev.next = node
    len += 1
  }

  /**
   * Clears this singly linked list.
   */
  def clear() = {
    dummy.next = dummy
  }

  /**
   * Removes the ''i''-th element.
   * @param i Index
   */
  def deleteAt(i: Int) = {
    val (prev, curr) = locate(i)
    prev.next = curr.next
    len -= 1
  }

  override def mapInplace(f: (T) => T): Unit = ???
}

object SinglyLinkedList {

  private[poly] class Node[T](var data: T, var next: Node[T]) extends SeqNode[T] {
    def isDummy = false
  }

}
