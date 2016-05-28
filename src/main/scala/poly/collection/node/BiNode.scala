package poly.collection.node

import poly.collection._

/**
 * Represents a node that has a list of successor nodes as well as a list of predecessor nodes.
 * @since 0.1.0
 */
trait BiNodeLike[+T, +N <: BiNodeLike[T, N]] extends ForwardNodeLike[T, N] with BackwardNodeLike[T, N] { self: N ⇒

  def pred: Iterable[N]
  def succ: Iterable[N]

  override def toString = pred.toString + " → " + super[ForwardNodeLike].toString
}

trait BiNode[+T] extends ForwardNode[T] with BackwardNode[T] with BiNodeLike[T, BiNode[T]] { self ⇒

  override def reverse: BiNode[T] = new BiNode[T] {
    def data = self.data
    def pred = self.succ.map(_.reverse)
    def succ = self.pred.map(_.reverse)
    override def reverse = self
    def isDummy = self.isDummy
  }

  override def map[U](f: T ⇒ U): BiNode[U] = new BiNode[U] {
    def data = f(self.data)
    def pred = self.pred.map(_ map f)
    def succ = self.succ.map(_ map f)
    def isDummy = self.isDummy
  }

  def zip[U](that: BiNode[U]): BiNode[(T, U)] = new BiNode[(T, U)] {
    def data = (self.data, that.data)
    def pred = (self.pred zipWith that.pred) { case (a, b) ⇒ a zip b }
    def succ = (self.succ zipWith that.succ) { case (a, b) ⇒ a zip b }
    def isDummy = self.isDummy || that.isDummy
  }

}
