package poly.collection.node

import poly.collection._
import poly.collection.mut._

/**
 * Represents a node that has exactly one predecessor and one successor.
 * It is the type of nodes in a bidirectional sequence ([[poly.collection.BiSeq]]).
 * @since 0.1.0
 */
trait BiSeqNode[+T] extends BiNode[T] with SeqNode[T] with SinglePredNode[T] { self =>
  def data: T
  def prev: BiSeqNode[T]
  def next: BiSeqNode[T]

  override def reverse: BiSeqNode[T] = new BiSeqNode[T] {
    def next = self.prev.reverse
    def prev = self.next.reverse
    def data = self.data
    override def reverse = self
    def isDummy = self.isDummy
  }

  override def parent: BiSeqNode[T] = prev
  override def pred: Iterable[BiSeqNode[T]] = ListSeq(prev).filter(_.notDummy)
  override def succ: Iterable[BiSeqNode[T]] = ListSeq(next).filter(_.notDummy)
  override def map[U](f: T => U): BiSeqNode[U] = new BiSeqNode[U] {
    def isDummy = self.isDummy
    def prev = self.prev map f
    def next = self.next map f
    def data = f(self.data)
  }
}
