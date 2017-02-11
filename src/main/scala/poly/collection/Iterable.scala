package poly.collection

import poly.algebra._
import poly.algebra.hkt._
import poly.algebra.syntax._
import poly.collection.exception._
import poly.collection.immut._
import poly.collection.mut._

import scala.annotation.unchecked.{uncheckedVariance => uv}
import scala.language.implicitConversions

/**
 * The basic trait for all collections that exposes an [[Iterator]].
 *
 * `Iterable`s differ from [[Traversable]]s in that the iteration process can be controlled:
 * It can be paused or resumed by the user.
 *
 * @author Tongfei Chen
 * @since 0.1.0
 */
trait Iterable[+T] extends Traversable[T] { self =>

  import Iterable._

  /** Returns a new iterator that can be used to iterate through this collection. */
  def newIterator: Iterator[T]

  def foreach[V](f: T => V) = newIterator run f

  //region MONADIC OPS

  override def map[U](f: T => U): Iterable[U] = new AbstractIterable[U] {
    def newIterator = new IterableT.MappedIterator(self.newIterator, f)
    override def sizeKnown = self.sizeKnown // map preserves size
    override def size = self.size
  }

  def flatMap[U](f: T => Iterable[U]) = ofIterator(new IterableT.FlatMappedIterator(self, f))

  def product[U](that: Iterable[U]): Iterable[(T, U)] = self.flatMap(t => that.map(u => (t, u)))

  def productWith[U, X](that: Iterable[U])(f: (T, U) => X): Iterable[X] = self.flatMap(t => that.map(u => f(t, u)))
  //endregion

  //region IDIOMATIC OPS
  /**
   * Returns a collection formed from this collection and another iterable collection by combining
   * corresponding elements in pairs.
   *
   * @param that Another iterable collection
   * @example {{{(1, 2, 3) zip (-1, -2, -3, -4) == ((1, -1), (2, -2), (3, -3))}}}
   * @return Zipped sequence
   */
  override def zip[U](that: Iterable[U]): Iterable[(T, U)] = zipWith(that) { (t, u) => (t, u) }

  /**
   * Equivalent to "`this zip that map f`" but may be more efficient.
   *
   * @example {{{
   *   (1, 2, 3) zipWith (2, 3, 4) (_+_) == (3, 5, 7)
   * }}}
   */
  override def zipWith[U, V](that: Iterable[U])(f: (T, U) => V): Iterable[V] = ofIterator {
    new AbstractIterator[V] {
      val ti = self.newIterator
      val ui = that.newIterator
      def current = f(ti.current, ui.current)
      def advance() = ti.advance() && ui.advance()
    }
  }

  def zip[U](that: Traversable[U]) = (that zipWith self) { (t, u) => (u, t) }
  def zipWith[U, X](that: Traversable[U])(f: (T, U) => X) = (that zipWith self) { (t, u) => f(u, t) }
  //endregion

  //region FILTERING OPS

  override def filter(f: T => Boolean) = ofIterator(new IterableT.FilteredIterator(self, f))

  override def filterNot(f: T => Boolean) = filter(x => !f(x))

  override def collect[U](pf: PartialFunction[T, U]) = ofIterator(new IterableT.CollectedIterator(self, pf))

  override def collectOption[U](f: T => Option[U]): Iterable[U] = collect(Function.unlift(f))
  //endregion

  //region SET OPS


  override def distinct(implicit T: Eq[T]): Iterable[T] = ofIterator {
    new AbstractIterator[T] {
      private[this] val set = AutoSet[T]()
      private[this] val i = self.newIterator
      def current = i.current
      def advance(): Boolean = {
        while (i.advance()) {
          if (set notContains i.current) {
            set add_! i.current
            return true
          }
        }
        false
      }
    }
  }

  override def distinctBy[U: Eq](f: T => U) = ofIterator {
    new AbstractIterator[T] {
      private[this] val set = AutoSet[U]()
      private[this] val i = self.newIterator
      def current = i.current
      def advance(): Boolean = {
        while (i.advance()) {
          val u = f(i.current)
          if (set notContains u) {
            set add_! u
            return true
          }
        }
        false
      }
    }
  }

  def union[U >: T : Eq](that: Iterable[U]): Iterable[U] = (this concat that).distinct

  def intersect[U >: T : Eq](that: Iterable[U]): Iterable[U] = {
    if (this.sizeKnown && that.sizeKnown && this.size < that.size) // short circuit!
      (that filter (this: Iterable[U]).to(AutoSet)).distinct
    else (this filter that.to(AutoSet)).distinct
  }


  //endregion

  //region SEQUENCE OPS

  def concat[U >: T](that: Iterable[U]): Iterable[U] = ofIterator(new IterableT.ConcatenatedIterator(self, that))

  override def prepend[U >: T](u: U): Iterable[U] = ofIterator {
    new AbstractIterator[U] {
      private[this] val i = self.newIterator
      private[this] var first = true
      private[this] var curr: U = _
      def advance() = if (first) {
        curr = u
        first = false
        true
      } else {
        val r = i.advance()
        curr = i.current
        r
      }
      def current = curr
    }
  }

  override def append[U >: T](u: U): Iterable[U] = ofIterator {
    new AbstractIterator[U] {
      private[this] val i = self.newIterator
      private[this] var last = false
      def advance() = {
        if (last) false
        else {
          val r = i.advance()
          if (!r) last = true
          true
        }
      }
      def current = if (!last) i.current else u
    }
  }
  override def tail = {
    val tailIterator = self.newIterator
    tailIterator.advance()
    ofIterator(tailIterator)
  }

  override def init = ofIterator {
    new AbstractIterator[T] {
      private[this] val i = self.newIterator
      i.advance()
      private[this] var prev = default[T]
      def advance() = {
        prev = i.current
        val res = i.advance()
        res
      }
      def current = prev
    }
  }


  override def take(n: Int): Iterable[T] = ofIterator {
    new AbstractIterator[T] {
      private[this] val i = self.newIterator
      private[this] var remaining = n
      def advance() = remaining > 0 && { remaining -= 1; i.advance() }
      def current = i.current
    }
  }

  override def takeWhile(f: T => Boolean) = ofIterator {
    new AbstractIterator[T] {
      private[this] val i = self.newIterator
      def advance() = i.advance() && f(i.current)
      def current = i.current
    }
  }

  override def takeTo(f: T => Boolean) = ofIterator {
    new AbstractIterator[T] {
      private[this] val i = self.newIterator
      private[this] var satisfied = false
      def advance() = {
        val r = (!satisfied) && i.advance()
        if (r) satisfied = f(current)
        r
      }
      def current = i.current
    }
  }

  override def takeUntil(f: T => Boolean) = takeWhile(!f)

  /**
   * Drops the first ''n'' elements from this collection.
   * @param n The number of elements to be skipped
   */
  override def drop(n: Int): Iterable[T] = ofIterator {
    val skippedIterator = self.newIterator
    var i = 0
    while (i < n && skippedIterator.advance()) i += 1
    skippedIterator
  }

  override def dropWhile(f: T => Boolean) = ofIterator {
    val skippedIterator = self.newIterator
    while (skippedIterator.advance() && f(skippedIterator.current)) {}
    skippedIterator
  }

  override def dropTo(f: T => Boolean) = ofIterator {
    val skippedIterator = self.newIterator
    while (skippedIterator.advance() && !f(skippedIterator.current)) {}
    skippedIterator.advance()
    skippedIterator
  }

  override def dropUntil(f: T => Boolean) = dropWhile(!f)

  override def slice(i: Int, j: Int) = self.drop(i).take(j - i)

  override def withIndex: SortedIterable[(Int, T @uv)] = {
    val paired = ofIterator {
      new AbstractIterator[(Int, T)] {
        private[this] var idx = -1
        private[this] val itr = self.newIterator
        def current = (idx, itr.current)
        def advance() = {
          idx += 1
          itr.advance()
        }
      }
    }
    paired.asIfSorted(Order by first)
  }

  override def repeat(n: Int): Iterable[T] = Range(n).flatMap((i: Int) => self)

  override def cycle: Iterable[T] = ofIterator {
    new AbstractIterator[T] {
      private[this] var outer = self.newIterator
      def current = outer.current
      def advance() = {
        val notComplete = outer.advance()
        if (!notComplete) {
          outer = self.newIterator
          outer.advance()
        }
        true
      }
    }
  }

  /**
   * Returns the interleave sequence of two sequences.
   *
   * @param that Another enumerable sequence
   * @example {{{(1, 2, 3, 4) interleave (-1, -2, -3) == (1, -1, 2, -2, 3, -3)}}}
   * @return Interleave sequence
   */
  def interleave[U >: T](that: Iterable[U]): Iterable[U] = ofIterator {
    new AbstractIterator[U] {
      private[this] val ti = self.newIterator
      private[this] val ui = that.newIterator
      private[this] var first = false
      def advance() = {
        first = !first
        if (!first) ti.advance() else ui.advance()
      }
      def current = if (!first) ti.current else ui.current
    }
  }
  //endregion

  //region FOLDING/SCANNING OPS

  override def scanLeft[U](z: U)(f: (U, T) => U) = ofIterator {
    new AbstractIterator[U] {
      private[this] val i = self.newIterator
      private[this] var accum = z
      private[this] var first = true
      def advance() = {
        if (first) {
          first = false
          true
        }
        else {
          val r = i.advance()
          if (r) accum = f(accum, i.current)
          r
        }
      }
      def current = accum
    }
  }

  override def scan[U >: T](z: U)(f: (U, U) => U) = scanLeft(z)(f)

  override def scanByMonoid[U >: T : Monoid] = scanLeft(id)(_ <> _)

  override def diffByGroup[U >: T](implicit U: Group[U]) = slidingPairsWith((x, y) => U.op(y, U.inv(x)))

  //endregion

  //region SEQUENTIAL GROUPING OPS

  override def slidingPairs = slidingPairsWith { (x, y) => (x, y) }

  override def slidingPairsWith[U](f: (T, T) => U) = ofIterator {
    new AbstractIterator[U] {
      private[this] val i = self.newIterator
      private[this] var a = default[T]
      private[this] var finished = i.advance()
      private[this] var b = i.current
      def advance() = {
        a = b
        finished = i.advance()
        b = i.current
        finished
      }
      def current = f(b, a)
    }
  }

  /**
   * Groups elements in fixed size blocks by passing a sliding window over them.
   * @param windowSize The size of the sliding window
   * @param step Step size. The default value is 1.
   * @example {{{(1, 2, 3, 4).sliding(2) == ((1, 2), (2, 3), (3, 4))}}}
   */
  override def sliding(windowSize: Int, step: Int = 1) = ofIterator {
    new AbstractIterator[IndexedSeq[T]] {
      private[this] val it = self.newIterator
      private[this] var window = ArraySeq.withSizeHint[T](windowSize)
      private[this] var first = true
      def advance(): Boolean = {
        if (first) {
          var i = 0
          while (i < windowSize && { val t = it.advance(); if (!t) return false; t }) {
            window.append_!(it.current)
            i += 1
          }
          first = false
          true
        } else {
          val newWindow = ArraySeq.withSizeHint[T](windowSize)
          var i = 0
          while (i + step < windowSize) {
            newWindow.append_!(window(i + step))
            i += 1
          }
          while (i < windowSize && { val t = it.advance(); if (!t) return false; t }) {
            newWindow.append_!(it.current)
            i += 1
          }
          window = newWindow
          true
        }
      }
      def current = window
    }
  }

  override def chunk(chunkSize: Int) = ofIterator {
    new AbstractIterator[IndexedSeq[T]] {
      private[this] val it = self.newIterator
      private[this] var buf: ArraySeq[T] = null
      private[this] var lastChunk = false
      def current = buf
      def advance(): Boolean = {
        if (lastChunk) return false
        val newBuf = ArraySeq.withSizeHint[T](chunkSize)
        buf = newBuf
        var i = 0
        var last = false
        while (i < chunkSize && {
          val t = it.advance(); if (!t) last = true; t
        }) {
          buf :+= it.current
          i += 1
        }
        if (last) lastChunk = true
        true
      }
    }
  }

  /**
   * Groups the elements into groups if the consecutive elements are the same under the specified
   * equivalence relation. $LAZY
   * @note This is similar to the Unix `uniq`, C++ `std::unique` and Python `itertools.groupby`.
   * @example {{{
   *   (1, 1, 2, 2, 2, 0).groupConsecutively == ((1, 1), (2, 2, 2), (0))
   * }}}
   */
  def groupConsecutively[U >: T : Eq]: Iterable[Iterable[T]] = groupConsecutivelyBy(identity)

  /**
   * Groups the elements into groups if the consecutive elements
   * are mapped to the same value using the given predicate. $LAZY
   * @note This is similar to Python `itertools.groupby`.
   * @example {{{
   *   (1, 4, 7, 2, 5, 2, 0, 3, 1).groupConsecutivelyBy(_ % 3) ==
   *   ((1, 4, 7), (2, 5, 2), (0, 3), (1))
   * }}}
   */
  def groupConsecutivelyBy[K: Eq](f: T => K): Iterable[Iterable[T]] = ofIterator {
    new AbstractIterator[Iterable[T]] {
      private[this] val it = self.newIterator
      private[this] var key: K = default[K]
      private[this] var g: ArraySeq[T] = null
      private[this] var buf = ArraySeq[T]()
      def current = g
      def advance(): Boolean = {
        while (it.advance()) {
          val curr = it.current
          val currKey = f(curr)
          if (key === currKey || buf.isEmpty)
            buf :+= curr
          else {
            g = buf to ArraySeq
            buf.clear_!()
            buf :+= curr
            key = currKey
            return true
          }
        }
        if (buf.notEmpty) {
          g = buf to ArraySeq
          buf.clear_!()
          true
        }
        else false
      }
    }
  }

  /**
   * Lazily splits this collection into multiple subsequences using the given delimiter predicate.
   * @param delimiter Predicate that determines whether an element is a delimiter.
   */
  def splitBy(delimiter: T => Boolean): Iterable[Seq[T]] = ofIterator {
    new AbstractIterator[Seq[T]] {
      private[this] val i = self.newIterator
      private[this] var buf: ArraySeq[T] = null
      private[this] var complete = false
      def current = buf
      def advance(): Boolean = {
        if (complete) return false
        buf = ArraySeq[T]()
        while (i.advance()) {
          if (delimiter(i.current)) return true
          else buf :+= i.current
        }
        complete = true
        true
      }
    }
  }

  /** Lazily splits this collection into multiple subsequences using the given delimiter. */
  def split[U >: T : Eq](delimiter: U) = splitBy(delimiter === _)
  //endregion

  //region REORDERING OPS

  override def rotate(n: Int): Iterable[T] = self.drop(n) ++ self.take(n)

  //endregion

  //region DECORATION OPS
  /**
   * Pretends that this sequence is sorted under the given implicit order.
   *
   * @param T The implicit order
   * @note Actual orderedness is not guaranteed! The user should make sure that it is sorted.
   */
  def asIfSorted(implicit T: Order[T]): SortedIterable[T @uv] = new SortedIterable[T] {
    def elementOrder = T
    def newIterator = self.newIterator
  }

  override def asIfSizeKnown(s: Int): Iterable[T] = new AbstractIterable[T] {
    def newIterator = self.newIterator
    override def foreach[V](f: T => V) = self.foreach(f)
    override def sizeKnown = true
    override def size = s
  }
  //endregion

  //region CASTING OPS

  def asIterable: Iterable[T] = ofIterator(self.newIterator)

  def asLazyList: LazyList[T] = newIterator.asLazyList

  //endregion

  //region SYMBOLIC ALIASES

  override def +:[U >: T](u: U): Iterable[U] = this prepend u
  override def :+[U >: T](u: U): Iterable[U] = this append u
  def ++[U >: T](that: Iterable[U]) = this concat that
  //def *(n: Int) = this repeat n
  def |*|[U](that: Iterable[U]) = this product that
  def ⋈[U](that: Iterable[U]) = this zip that
  //endregion

  //region JAVA/SCALA CONFORMATION

  override def withFilter(f: T => Boolean) = filter(f)
  // toString: inherit from Traversable
  // hashCode: by reference
  // equals: by reference
  //endregion

}

object Iterable {

  object Empty extends Iterable[Nothing] {
    def newIterator: Iterator[Nothing] = new AbstractIterator[Nothing] {
      def advance() = false
      def current = throw new DummyNodeException
    }
    def unapply[T](x: Iterable[T]) = x.isEmpty
  }

  /** Creates an iterable collection based on an existing iterator. */
  def ofIterator[T](i: => Iterator[T]): Iterable[T] = new AbstractIterable[T] {
    def newIterator = i // call-by-name parameter because Iterators are mutable objects that contain states!
  }

  /** Creates an iterable collection that contains only one element. */
  def single[T](x: => T) = ofIterator {
    new AbstractIterator[T] {
      private[this] var curr: T = _
      private[this] var first = true
      def advance() = {
        if (first) {
          curr = x
          first = false
          true
        } else false
      }
      def current = curr
    }
  }

  /**
   * Constructs a lazy infinite collection that is generated by repeatedly applying a given function to
   * a starting value. $LAZY
   * @param s Start value
   * @param f Transition function
   * @return An infinite sequence
   */
  def iterate[T](s: T)(f: T => T) = ofIterator {
    new AbstractIterator[T] {
      private[this] var curr: T = _
      private[this] var first = true
      def advance() = {
        if (first) {
          first = false
          curr = s
        } else curr = f(curr)
        true
      }
      def current = curr
    }
  }

  /** Constructs an infinite iterable stream of the given value. */
  def infinite[T](x: => T) = ofIterator {
    new AbstractIterator[T] {
      def current = x
      def advance() = true
    }
  }

  def repeat[T](n: Int)(x: => T) = ofIterator {
    new AbstractIterator[T] {
      private[this] var r = n
      def current = x
      def advance() = {
        r -= 1
        r > 0
      }
    }
  }

  def zipMany[T](xss: Iterable[T]*) = ofIterator {
    new AbstractIterator[IndexedSeq[T]] {
      private[this] val is = ArraySeq.tabulate(xss.length)(i => xss(i).newIterator)
      def current = is map { _.current }
      def advance() = is forall { _.advance() }
    }
  }

  /** Returns the natural monad on Iterables. */
  implicit object Monad extends ConcatenativeMonad[Iterable] {
    def flatMap[X, Y](mx: Iterable[X])(f: X => Iterable[Y]): Iterable[Y] = mx.flatMap(f)
    override def map[X, Y](mx: Iterable[X])(f: X => Y): Iterable[Y] = mx.map(f)
    def id[X](u: X): Iterable[X] = Iterable.single(u)
    def empty[X]: Iterable[X] = Iterable.Empty
    def concat[X](a: Iterable[X], b: Iterable[X]) = a.concat(b)
    override def filter[X](mx: Iterable[X])(f: X => Boolean) = mx.filter(f)
  }

  object ZipIdiom extends Idiom[Iterable] {
    def id[X](u: X) = Iterable.infinite(u)
    def liftedMap[X, Y](mx: Iterable[X])(mf: Iterable[X => Y]) = (mx zipWith mf) { case (x, f) => f(x) }
    override def product[X, Y](mx: Iterable[X])(my: Iterable[Y]) = mx zip my
  }

  /** Implicitly converts an `Option` to an `Iterable` that contains one or zero element. */
  implicit def fromOption[T](o: Option[T]): Iterable[T] = o match {
    case Some(x) => Iterable.single(x)
    case None    => Iterable.Empty
  }


  implicit class IterableOfIterablesOps[T](val underlying: Iterable[Iterable[T]]) extends AnyVal {
    /**
     * "Flattens" this collection of collection into one collection.
     * @example {{{((1, 2, 3), (4, 5), (), (7)).flatten == (1, 2, 3, 4, 5, 7)}}}
     */
    def flatten: Iterable[T] = underlying.flatMap(identity)
  }

  implicit class IterableOfPairsOps[A, B](val underlying: Iterable[(A, B)]) extends AnyVal {

    def unzip = (underlying map first, underlying map second)

  }
}

abstract class AbstractIterable[+T] extends AbstractTraversable[T] with Iterable[T]

private[poly] object IterableT {

  class MappedIterator[T, U](self: Iterator[T], f: T => U) extends AbstractIterator[U] {
    def current = f(self.current)
    def advance() = self.advance()
  }


  class FlatMappedIterator[T, U](self: Iterable[T], f: T => Iterable[U]) extends AbstractIterator[U] {
    private[this] val outer: Iterator[T] = self.newIterator
    private[this] var inner: Iterator[U] = Iterator.Empty
    def current = inner.current
    def advance(): Boolean = {
      if (inner.advance()) true
      else {
        while (outer.advance()) {
          inner = f(outer.current).newIterator
          if (inner.advance()) return true
        }
        false
      }
    }
  }

  class FilteredIterator[T](self: Iterable[T], f: T => Boolean) extends AbstractIterator[T] {
    private[this] val i = self.newIterator
    def current: T = i.current
    def advance(): Boolean = {
      do {
        val hasNext = i.advance()
        if (!hasNext) return false
      } while (!f(i.current))
      true
    }
  }

  class CollectedIterator[T, U](self: Iterable[T], pf: PartialFunction[T, U]) extends AbstractIterator[U] {
    private[this] val i = self.newIterator
    private[this] var c = default[U]
    def advance(): Boolean = {
      do {
        if (!i.advance()) return false
      } while (!(pf runWith { c = _ })(i.current))
      true
    }
    def current = c
  }

  class ConcatenatedIterator[U](self: Iterable[U], that: Iterable[U]) extends AbstractIterator[U] {
    private[this] var e: Iterator[U] = self.newIterator
    private[this] var first = true
    def advance() = {
      if (e.advance()) true
      else if (first) {
        e = that.newIterator
        first = false
        e.advance()
      }
      else false
    }
    def current = e.current
  }

}
