package poly.collection.impl

import poly.collection._
import poly.collection.mut._

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
class ForwardLinkedList[T] {

  type Node = ForwardLinkedList.Node[T]

  private[poly] val dummy = new Node(default[T])
  private[poly] var len: Int = 0
  dummy.next = dummy


  /**
   * Locates the ''i''th element in a singly linked list.
   * @param i Index
   * @return The previous node and the node that contains the ''i''-th element.
   */
  def locate(i: Int): (Node, Node) = {
    if (i < 0 || i >= len) throw new IndexOutOfBoundsException
    var curr = dummy
    var prev: Node = null
    var j = 0
    while (j < i) {
      curr = curr.next
      prev = curr
      j += 1
    }
    (prev, curr)
  }

  /**
   * Appends an element to the end of the singly linked list.
   * @param x The element to be appended
   */
  def append(x: T) = {
    val (_, last) = locate(len - 1)
    val node = new Node(x, dummy)
    last.next = node
    len += 1
  }

  /**
   * Prepends an element to the start of the doubly linked list.
   * @param x The element to be prepended.
   */
  def prepend(x: T) = {
    val node = new Node(x, dummy.next)
    dummy.next = node
    len += 1
  }

  /**
   * Gets the ''i''-th element.
   * @param i Index
   * @return The ''i''-th element.
   */
  def apply(i: Int) = locate(i)._2.data

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
  def insert(i: Int, x: T) = {
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
  def remove(i: Int) = {
    val (prev, curr) = locate(i)
    prev.next = curr.next
    len -= 1
  }


}

object ForwardLinkedList {
  class Node[T] (
    var data: T,
    private[poly] var next: Node[T] = null
  ) extends ForwardNode[T] {

    def descendants = ListSeq(next)

  }

}