package poly.collection.node

import poly.collection._

/**
 * Represents a node that has a list of predecessor nodes.
 * @since 0.1.0
 */
trait BackwardNode[+T] { self =>
  def data: T
  def pred: Iterable[BackwardNode[T]]

  def isDummy: Boolean
  def notDummy = !isDummy

  def reverse: ForwardNode[T] = new ForwardNode[T] {
    def data = self.data
    def succ = self.pred.map(_.reverse)
    override def reverse = self
    def isDummy = self.isDummy
  }

  override def toString = if (notDummy) s"Node($data)" else "<dummy>"
}
