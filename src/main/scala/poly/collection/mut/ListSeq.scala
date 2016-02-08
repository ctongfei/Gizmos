package poly.collection.mut

import poly.collection._
import poly.collection.builder._
import poly.collection.exception._
import poly.collection.factory._
import poly.collection.impl._
import poly.collection.node._

/**
 * A sequence backed by a linked list.
 *
 * @author Tongfei Chen
 */
class ListSeq[T] private() extends AbstractSeq[T] with KeyMutableSeq[T] with HasKnownSize {

  type Node = ListSeq.Node[T]

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

  override def mapInplace(f: T => T) = {
    var n = dummy.next
    while (n.notDummy) {
      n.data = f(n.data)
      n = n.next
    }
  }

  def inplaceReverse() = ???

  override def newIterator = new Iterator[T] {
    var node = dummy

    def advance(): Boolean = {
      if (node.next.isDummy) false
      else {
        node = node.next
        true
      }
    }

    def current: T = {
      if (node.isDummy) throw new DummyNodeException
      node.data
    }
  }
}

object ListSeq extends SeqFactory[ListSeq] {


  private[poly] class Node[T](var data: T, var next: Node[T]) extends SeqNode[T] {
    def isDummy = false
  }

  implicit def newBuilder[T]: Builder[T, ListSeq[T]] = new Builder[T, ListSeq[T]] {
    val a = new ListSeq[T]()
    def sizeHint(n: Int) = {}
    def add(x: T) = a appendInplace x
    def result = a
  }
}
