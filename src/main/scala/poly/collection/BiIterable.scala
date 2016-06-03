package poly.collection

/**
 * Represents a collection that can be iterated in two directions, namely forward and backward.
 *
 * @author Tongfei Chen
 * @since 0.1.0
 */
trait BiIterable[+T] extends Iterable[T] { self =>

  import BiIterable._

  /** Returns an iterator that iterates through this collection in reverse order. */
  def newReverseIterator: Iterator[T]

  /** Returns the reverse of this iterable collection. $LAZY */
  override def reverse: BiIterable[T] = new AbstractBiIterable[T] {
    def newReverseIterator = self.newIterator
    def newIterator = self.newReverseIterator
    override def reverse = self
  }

  override def map[U](f: T => U): BiIterable[U] = new AbstractBiIterable[U] {
    def newIterator = new AbstractIterator[U] {
      private[this] val i = self.newIterator
      def current = f(i.current)
      def advance() = i.advance()
    }

    def newReverseIterator = new AbstractIterator[U] {
      private[this] val i = self.newReverseIterator
      def current = f(i.current)
      def advance() = i.advance()
    }

    override def size = self.size // map preserves size
    override def sizeKnown = self.sizeKnown
  }

  override def last = reverse.head

  def asBiIterable = new AbstractBiIterable[T] {
    def newReverseIterator = self.newReverseIterator
    def newIterator = self.newIterator
  }

}

object BiIterable {

  def ofIterator[T](forward: => Iterator[T], backward: => Iterator[T]): BiIterable[T] = new BiIterable[T] {
    def newReverseIterator = backward
    def newIterator = forward
  }

}

abstract class AbstractBiIterable[+T] extends AbstractIterable[T] with BiIterable[T]
