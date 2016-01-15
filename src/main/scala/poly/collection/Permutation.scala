package poly.collection

import poly.algebra._
import poly.algebra.syntax._
import poly.collection.exception._
import poly.collection.ops._

/**
  * Represents a permutation on a set {0, ..., ''n'' - 1}.
  * @author Tongfei Chen
  * @since 0.1.0
  */
class Permutation private(private val a1: Array[Int], private val a2: Array[Int])
  extends BijectiveMap[Int, Int] with IndexedSeq[Int] with HasKnownSize with SequentialOrder[Int] with Bounded[Int]
{
  def fastLength = a1.length
  def fastApply(x: Int) = a1(x)
  def invert(y: Int) = a2(y)

  def equivOnValue = Equiv[Int]

  def containsValue(y: Int) = containsKey(y)

  def invertOption(y: Int) = if (y < 0 || y >= size) None else Some(a2(y))

  def compose(that: Permutation) = {
    require(this.size == that.size)
    Permutation(Array.tabulate(size)(i => that(this(i))))
  }

  def andThen(that: Permutation) = that compose this

  def cmp(x: Int, y: Int) = a2(x) >?< a1(y)
  def pred(x: Int) = a1((a2(x) - 1) % size)
  def succ(x: Int) = a1((a2(x) + 1) % size)
  def top = a1(size - 1)
  def bot = a1(0)

  override def inverse: Permutation = new Permutation(a2, a1)

  override def reverse: Permutation = Permutation(arrayAsIndexedSeq(a1).reverse.toArray)

  override def toString = arrayAsIndexedSeq(a1).toString
}

object Permutation {

  def apply(xs: Int*): Permutation = apply(xs.toArray)

  def apply(xs: Array[Int]): Permutation = {
    val ys = Array.ofDim[Int](xs.length)
    for (i ← Range(xs.length)) {
      ys(xs(i)) = i
    }
    new Permutation(xs, ys)
  }

  def random(n: Int) = {
    val a = Array.tabulate(n)(i => i)
    val r = new scala.util.Random()
    for (i ← Range(n - 1, 0, -1)) {
      val j = r.nextInt(i + 1)
      val t = a(i)
      a(i) = a(j)
      a(j) = t
    }
    Permutation(a)
  }

  def identity(n: Int) = {
    val a = Array.tabulate(n)(i => i)
    new Permutation(a, a)
  }

  implicit def GroupAction[T]: Action[IndexedSeq[T], Permutation] = new Action[IndexedSeq[T], Permutation] {
    def act(k: Permutation, x: IndexedSeq[T]) = x permuteBy k
  }

  /**
    * Returns the permutation group of size ''n''.
    * This structure is a [[poly.algebra.Group]] as well as a [[poly.collection.Set]].
    * @return
    */
  def Group(n: Int): Group[Permutation] with Set[Permutation] = new Group[Permutation] with Set[Permutation] {
    def inv(x: Permutation) = x.inverse
    def id = identity(n)
    def op(x: Permutation, y: Permutation) = x compose y
    def equivOnKey = LexicographicOrder
    def contains(x: Permutation) = x.size == n

    def keys: Iterable[Permutation] = Iterable.ofIterator {
      new Iterator[Permutation] {
        var p: Array[Int] = null

        def current = Permutation(p)

        // Generate permutations under lexicographic order.
        def advance(): Boolean = {
          if (p == null) {
            p = Array.tabulate(n)(i => i)
            true
          }
          else {
            var k = -1
            for (i ← Range(n - 1)) if (p(i) < p(i + 1)) k = i
            if (k == -1) return false
            var l = k + 1
            for (i ← Range(k + 1, n)) if (p(k) < p(i)) l = i
            val t = p(k)
            p(k) = p(l)
            p(l) = t
            var left = k + 1
            var right = n - 1
            while (left < right) {
              val t = p(right)
              p(right) = p(left)
              p(left) = t
              left += 1
              right -= 1
            }
            true
          }
        }
      }
    }
  }

  implicit object LexicographicOrder extends SequentialOrder[Permutation] {
    def cmp(x: Permutation, y: Permutation): Int = {
      for (i ← Range(x.size)) {
        if (x(i) < y(i)) return -1
        if (x(i) > y(i)) return 1
      }
      0
    }

    def pred(x: Permutation) = ???

    def succ(x: Permutation) = ???
  }

}