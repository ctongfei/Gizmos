package poly.collection.mut

import poly.algebra._
import poly.algebra.syntax._
import poly.collection._
import poly.collection.builder._
import poly.collection.factory._
import poly.collection.impl._

/**
 * A Fenwick tree.
 * Fenwick trees provides efficient methods for the computation and manipulation
 * of the prefix sums of a sequence of values.
 * @author Tongfei Chen
 * @since 0.1.0
 */
class FenwickTree[T] private(private val data: ResizableSeq[T])
  (implicit val groupOnElements: AdditiveGroup[T]) extends AbstractIndexedSeq[T] with ValueMutableIndexedSeq[T] {

  import FenwickTree._

  def fastLength = data.length - 1

  def fastApply(idx: Int) = {
    var i = idx + 1
    var sum = data(i)
    val z = i - lowBit(i)
    i -= 1
    while (i != z) {
      sum -= data(i)
      i -= lowBit(i)
    }
    sum
  }

  /**
   * Returns the sum of the first ''n'' elements of this list. $Ologn
   */
  def cumulativeSum(n: Int) = {
    var i = n
    var sum = zero[T]
    while (i != 0) {
      sum += data(i)
      i -= lowBit(i)
    }
    sum
  }

  /**
   * Returns the sum of the elements in the slice [''i'', ''j''). $Ologn
   */
  def rangeSum(i: Int, j: Int) = cumulativeSum(j) - cumulativeSum(i)

  /** Increments the ''i''-th element by ''δ''. */
  def increment(i: Int, δ: T)  {
    var j = i + 1
    while (j < data.length) {
      data(j) += δ
      j += lowBit(j)
    }
  }

  def update(idx: Int, value: T) = increment(idx, value - this(idx))

  def sum = cumulativeSum(length)

  def prefixSums = Range(length + 1) map cumulativeSum

}

object FenwickTree extends BuilderFactoryA_EvA[FenwickTree, AdditiveGroup] {

  @inline private[poly] def lowBit(x: Int) = x & -x

  implicit def newBuilder[T: AdditiveGroup]: Builder[T, FenwickTree[T]] = new Builder[T, FenwickTree[T]] {
    private[this] val data = ResizableSeq[T](zero[T])
    def add(x: T) = {
      var i = data.len
      var sum = x
      val z = i - lowBit(i)
      i -= 1
      while (i != z) {
        sum += data(i)
        i -= lowBit(i)
      }
      data.append_!(sum)
    }
    override def sizeHint(n: Int) = data.ensureCapacity(n)
    def result = new FenwickTree[T](data)
  }
}
