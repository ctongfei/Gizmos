package poly.collection.node

import poly.collection._
import poly.collection.search._

/**
 * Basic trait for nodes. A node may contain a list of successor nodes.
 *
 * This serves as a common trait for sequence nodes, tree nodes and graph nodes.
 * Nodes provide a unified view for lists, trees and graphs.
 * @author Tongfei Chen (ctongfei@gmail.com).
 * @since 0.1.0
 */
trait Node[+T] { self =>
  def data: T
  def succ: Enumerable[Node[T]]
  override def toString = data.toString

  def map[U](f: T => U): Node[U] = new Node[U] {
    def data = f(self.data)
    def succ = self.succ.map(n => n.map(f))
  }
}

object Node {
  implicit def StateSpace[T]: StateSpace[Node[T]] = new StateSpace[Node[T]] {
    def succ(x: Node[T]) = x.succ
  }
}

/**
 * Represents a node that has a list of predecessor nodes.
 * @since 0.1.0
 */
trait BackwardNode[+T] {
  def data: T
  def pred: Enumerable[BackwardNode[T]]
}

/**
 * Represents a node that has a list of successor nodes as well as a list of predecessor nodes.
 * @since 0.1.0
 */
trait BiNode[+T] extends Node[T] with BackwardNode[T] {
  def data: T
  def succ: Enumerable[BiNode[T]]
  def pred: Enumerable[BiNode[T]]
}
