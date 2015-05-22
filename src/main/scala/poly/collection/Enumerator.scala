package poly.collection

/**
 * Basic trait for enumerators that supports an iteration over a collection that can be
 * paused and resumed.
 *
 * This is the Poly-collection version of the Java/Scala `Iterator`. The main difference
 * is their abstraction of supported methods:
 *
 *  - Java/Scala `Iterable`: { `hasNext`, `next` }
 *  - Poly-collection `Enumerator`: { `current`, `advance` }
 *  - C++ iterators : { `*`, `++` }
 *  - C# `Enumerator`: { `Current`, `MoveNext` }
 *
 *  Poly-collection adopts the C++/C# style of iterators instead of the Java/Scala style.
 *
 * @author Tongfei Chen (ctongfei@gmail.com).
 */
trait Enumerator[+T] { self =>

  /** Returns the current element of this enumeration. */
  def current: T

  /**
   * Advances the enumerator to the next element.
   * @return Whether the enumerator successfully advanced to the next element.
   */
  def advance(): Boolean

  def foreach[U](f: T => U): Unit = {
    while (self.advance())
      f(self.current)
  }

  def map[U](f: T => U): Enumerator[U] = new Enumerator[U] {
    def current = f(self.current)
    def advance() = self.advance()
  }

  def flatMap[U](f: T => Enumerator[U]) = new Enumerator[U] {
    private var it: Enumerator[U] = Enumerator.empty[U]
    def current = it.current
    def advance() = {
      if (it.advance()) true
      else {
        if (self.advance()) {
          it = f(self.current)
          true
        }
        else false
      }
    }
  }

  def filter(f: T => Boolean): Enumerator[T] = new Enumerator[T] {
    def current: T = self.current
    def advance(): Boolean = {
      do {
        val hasNext = self.advance()
        if (!hasNext) return false
      } while (!f(self.current))
      true
    }
  }

}

object Enumerator {
  def empty[T] = new Enumerator[T] {
    def advance() = false
    def current = throw new NoSuchElementException
  }

}

