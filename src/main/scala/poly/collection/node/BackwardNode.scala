package poly.collection.node

import poly.collection._

/**
 * Represents a node that has a list of predecessor nodes.
 * @since 0.1.0
 * @author Tongfei Chen
 */
trait BackwardNodeLike[+T, +N <: BackwardNodeLike[T, N]] extends NodeLike[T, N] { self: N ⇒

  /** Returns the list of predecessor nodes of this node. */
  def pred: Iterable[N]

  override def toString = pred.toString + " → " + super[NodeLike].toString
}

trait BackwardNode[+T] extends Node[T] with BackwardNodeLike[T, BackwardNode[T]] { self ⇒

  def reverse: ForwardNode[T] = new ForwardNode[T] {
    def data = self.data
    def succ = self.pred.map(_.reverse)
    override def reverse = self
    def isDummy = self.isDummy
  }

  def map[U](f: T ⇒ U): BackwardNode[U] = new BackwardNode[U] {
    def pred = self.pred.map(_.map(f))
    def data = f(self.data)
    def isDummy = self.isDummy
  }

  def zip[U](that: BackwardNode[U]): BackwardNode[(T, U)] = new BackwardNode[(T, U)] {
    def data = (self.data, that.data)
    def pred = (self.pred zipWith that.pred) { case (a, b) ⇒ a zip b }
    def isDummy = self.isDummy || that.isDummy
  }

}
