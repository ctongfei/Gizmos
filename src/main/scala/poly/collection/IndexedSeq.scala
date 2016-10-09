package poly.collection

import poly.algebra._
import poly.algebra.specgroup._
import poly.algebra.syntax._
import poly.collection.exception._
import poly.collection.mut._
import poly.collection.node._
import poly.macroutil._

import scala.annotation.unchecked.{uncheckedVariance => uv}
import scala.language.implicitConversions

/**
 * Represents an indexed sequence.
 * Indexed sequences should support efficient random access (typically O(1), sometimes may be O(log ''n'')).
 *
 * @author Tongfei Chen
 * @since 0.1.0
 */
trait IndexedSeq[+T] extends BidiSeq[T] { self =>

  /** Returns the length of this indexed sequence. */
  def fastLength: Int

  /** Returns the ''i''-th element of this sequence.
   * @note Not necessary to implement negative indices here. */
  def fastApply(i: Int): T

  @inline final override def size = fastLength

  @inline final override def length = fastLength

  /** Returns the ''i''-th element of this sequence. $O1 (sometimes O(log ''n''): e.g. [[poly.collection.mut.FenwickTree]]). */
  @inline final override def apply(i: Int) = {
    if (i >= 0) fastApply(i)
    else fastApply(i + fastLength)
  }

  // Overridden newIterator method for performance.
  override def newIterator: Iterator[T] = new IndexedSeqT.DefaultIterator[T](self)

  // Overridden foreach method for performance.
  override def foreach[@sp(Unit) V](f: T => V): Unit = {
    FastLoop.ascending(0, length, 1) { i => f(apply(i)) }
  }

  class Node(val i: Int) extends BidiSeqNode[T] {
    def isDummy = (i < 0) || (i >= length)
    def data = self(i)
    def next = if (i >= length - 1) BidiSeqNode.dummy else new Node(i + 1)
    def prev = if (i <= 0) BidiSeqNode.dummy else new Node(i - 1)

    override def equals(that: Any) = that match {
      case that: Node => this.i == that.i
      case _ => false
    }
    override def hashCode = hashByRef(self) + i
  }

  def headNode = new Node(0)
  def lastNode = new Node(length - 1)

  override def sizeKnown = true

  override def keys = Range(fastLength)

  override def pairs: SortedIndexedSeq[(Int, T @uv)] =
    IndexedSeq.tabulate(self.length)(i => i -> self(i)).asIfSorted(Order by first)

  // HELPER FUNCTIONS

  override def isEmpty = fastLength <= 0

  override def map[U](f: T => U): IndexedSeq[U] = new IndexedSeqT.Mapped(self, f)

  def monadicProduct[U](that: IndexedSeq[U]): IndexedSeq[(T, U)] = new IndexedSeqT.MonadicProduct(self, that)

  /**
   * Returns the Cartesian product of two indexed sequences. The returning value is a table.
   */
  def product[U](that: IndexedSeq[U]): Table[(T, U)] = new IndexedSeqT.TableProduct(self, that)

  def concat[U >: T](that: IndexedSeq[U]): IndexedSeq[U] = new IndexedSeqT.Concatenated(self, that)

  override def prepend[U >: T](x: U): IndexedSeq[U] = new IndexedSeqT.Prepended(self, x)

  override def append[U >: T](x: U): IndexedSeq[U] = new IndexedSeqT.Appended(self, x)

  override def reduce[U >: T](f: (U, U) => U): U = {
    if (length == 0) throw new EmptyCollectionReductionException
    MapReduceOps.bySemigroup[U](length, x => self(x): U, f) // optimize through macros
  }

  override def fold[U >: T](z: U)(f: (U, U) => U): U = {
    MapReduceOps.byMonoid[U](length, x => self(x): U, z, f) // optimize through macros
  }

  override def head = self(0)

  override def last = self(length - 1)

  override def tail = self.drop(1)

  override def init = self.take(length - 1)

  override def suffixes = Range(length) map drop

  override def prefixes = Range(length, 0, -1) map take

  override def take(n: Int) = slice(0, n)

  override def drop(n: Int) = slice(n, self.length)

  override def slice(_i: Int, _j: Int): IndexedSeq[T] = {
    val i = if (_i < 0) _i + length else _i
    val j = if (_j < 0) _j + length else _j
    new IndexedSeqT.Sliced(self, i, j)
  }

  override def rotate(j: Int): IndexedSeq[T] = new IndexedSeqT.Rotated(self, j)

  override def reverse: IndexedSeq[T] = new IndexedSeqT.Reversed(self)

  override def repeat(n: Int): IndexedSeq[T] = new IndexedSeqT.Repeated(self, n)

  override def sliding(windowSize: Int, step: Int = 1): IndexedSeq[IndexedSeq[T]] = new IndexedSeqT.Sliding(self, windowSize, step)

  def zip[U](that: IndexedSeq[U]): IndexedSeq[(T, U)] = new IndexedSeqT.ZippedWith(self, that, (t: T, u: U) => (t, u))

  def zipWith[U, V](that: IndexedSeq[U])(f: (T, U) => V): IndexedSeq[V] = new IndexedSeqT.ZippedWith(self, that, f)

  def interleave[U >: T](that: IndexedSeq[U]): IndexedSeq[U] = new IndexedSeqT.Interleaved(self, that)

  /**
   * Rearranges the elements in this indexed sequence according to a permutation. $LAZY
   * @param p A permutation which is of the same length as this sequence
   * @return A permuted sequence
   * @example {{{('a', 'b', 'c') permuteBy Permutation(1, 2, 0) == ('b', 'c', 'a')}}}
   */
  def permuteBy(p: Permutation): IndexedSeq[T] = new IndexedSeqT.Permuted(self, p)

  override def asIfSorted(implicit T: Order[T]): SortedIndexedSeq[T @uv] = new IndexedSeqT.AsIfSorted(self, T)

  def asIndexedSeq: IndexedSeq[T] = new IndexedSeqT.Bare(self)

  // SYMBOLIC ALIASES

  override def +:[U >: T](u: U): IndexedSeq[U] = this prepend u
  override def :+[U >: T](u: U): IndexedSeq[U] = this append u
  def ++[U >: T](that: IndexedSeq[U]) = this concat that
  def ∗[U](that: IndexedSeq[U]) = self monadicProduct that
  def ⋈[U](that: IndexedSeq[U]) = self zip that
  def ×[U](that: IndexedSeq[U]) = self product that

}

object IndexedSeq {

  object Empty extends IndexedSeq[Nothing] {
    def fastApply(i: Int): Nothing = throw new NoSuchElementException
    def fastLength: Int = 0
  }

  def fill[T](n: Int)(x: => T): IndexedSeq[T] = new IndexedSeqT.Filled(x, n)

  def tabulate[T](n: Int)(f: Int => T): IndexedSeq[T] = new IndexedSeqT.Tabulated(f, n)

  //TODO: should be implicit, but results in ambiguous implicits because of contravariant typeclass
  def Eq[T: Eq]: Eq[IndexedSeq[T]] = new Eq[IndexedSeq[T]] {
    def eq(x: IndexedSeq[T], y: IndexedSeq[T]): Boolean = {
      if (x.fastLength != y.fastLength) false
      else {
        FastLoop.ascending(0, x.fastLength, 1) { i =>
          if (x(i) !== y(i)) return false
        }
        true
      }
    }
  }

}


abstract class AbstractIndexedSeq[+T] extends IndexedSeq[T]

private[poly] object IndexedSeqT {

  class DefaultIterator[T](self: IndexedSeq[T]) extends AbstractIterator[T] {
    private[this] var i: Int = -1
    def current = self(i)
    def advance(): Boolean = {
      i += 1
      i < self.length
    }
  }

  class Mapped[T, U](self: IndexedSeq[T], f: T => U) extends AbstractIndexedSeq[U] {
    def fastLength = self.length
    def fastApply(i: Int) = f(self(i))
  }

  class MonadicProduct[T, U](self: IndexedSeq[T], that: IndexedSeq[U]) extends AbstractIndexedSeq[(T, U)] {
    private[this] val stride = that.length
    def fastLength = self.length * that.length
    def fastApply(i: Int) = (self(i / stride), that(i % stride))
  }

  class TableProduct[T, U](self: IndexedSeq[T], that: IndexedSeq[U]) extends AbstractTable[(T, U)] {
    def apply(i: Int, j: Int) = (self(i), that(j))
    def numRows = self.length
    def numCols = that.length
  }

  class Concatenated[T](self: IndexedSeq[T], that: IndexedSeq[T]) extends AbstractIndexedSeq[T] {
    def fastLength = self.length + that.length
    def fastApply(i: Int) = if (i < self.length) self(i) else that(i - self.length)
  }

  class Prepended[T](self: IndexedSeq[T], x: T) extends AbstractIndexedSeq[T] {
    def fastLength = self.length + 1
    def fastApply(i: Int) = if (i == 0) x else self(i - 1)
  }

  class Appended[T](self: IndexedSeq[T], x: T) extends AbstractIndexedSeq[T] {
    def fastLength = self.length + 1
    def fastApply(i: Int) = if (i == self.length) x else self(i)
  }

  class Sliced[T](self: IndexedSeq[T], i: Int, j: Int) extends AbstractIndexedSeq[T] {
    def fastApply(n: Int) = self(i + n)
    def fastLength = if (j > self.length) self.length - i else j - i
    override def slice(ii: Int, jj: Int) = { // optimize for nested slices
      self.slice(i + ii, math.min(i + jj, j))
    }
  }

  class Rotated[T](self: IndexedSeq[T], j: Int) extends AbstractIndexedSeq[T] {
    def fastApply(i: Int): T = self((j + i) %+ self.length)
    def fastLength: Int = self.length
    override def rotate(jj: Int) = self.rotate(j + jj) // optimize for nested rotates
  }

  class Reversed[T](self: IndexedSeq[T]) extends AbstractIndexedSeq[T] {
    def fastApply(i: Int) = self(self.length - 1 - i)
    def fastLength = self.length
    override def reverse = self
  }

  class Repeated[T](self: IndexedSeq[T], n: Int) extends AbstractIndexedSeq[T] {
    def fastApply(i: Int) = self(i % self.length)
    def fastLength = self.length * n
    override def repeat(nn: Int) = self.repeat(n * nn)
  }

  class Sliding[T](self: IndexedSeq[T], windowSize: Int, step: Int) extends  AbstractIndexedSeq[IndexedSeq[T]] {
    def fastLength = (self.length - windowSize) / step + 1
    def fastApply(i: Int) = self.slice(step * i, step * i + windowSize)
  }

  class ZippedWith[T, U, X](self: IndexedSeq[T], that: IndexedSeq[U], f: (T, U) => X) extends AbstractIndexedSeq[X] {
    def fastApply(i: Int) = f(self(i), that(i))
    def fastLength = math.min(self.length, that.length)
  }

  class Interleaved[T](self: IndexedSeq[T], that: IndexedSeq[T]) extends AbstractIndexedSeq[T] {
    def fastLength = math.min(self.length, that.length) * 2
    def fastApply(i: Int) = if (i % 2 == 0) self(i / 2) else that(i / 2)
  }

  class Permuted[T](self: IndexedSeq[T], p: Permutation) extends AbstractIndexedSeq[T] {
    def fastApply(i: Int) = self(p.invert(i))
    def fastLength = self.fastLength
    override def permuteBy(q: Permutation) = self.permuteBy(q compose p)
  }


  class AsIfSorted[T](self: IndexedSeq[T], order: Order[T]) extends SortedIndexedSeq[T] {
    def elementOrder = order
    def fastLength: Int = self.length
    def fastApply(i: Int): T = self.apply(i)
  }

  class Bare[T](self: IndexedSeq[T]) extends AbstractIndexedSeq[T] {
    def fastApply(i: Int) = self.fastApply(i)
    def fastLength = self.fastLength
  }

  class Filled[T](x: => T, n: Int) extends AbstractIndexedSeq[T] {
    def fastLength = n
    def fastApply(i: Int) = x
  }

  class Tabulated[T](f: Int => T, n: Int) extends AbstractIndexedSeq[T] {
    def fastLength: Int = n
    def fastApply(i: Int): T = f(i)
  }

}