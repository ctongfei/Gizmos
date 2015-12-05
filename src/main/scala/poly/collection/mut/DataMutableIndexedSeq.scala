package poly.collection.mut

import poly.algebra._
import poly.algebra.ops._
import poly.collection._

import scala.util._

/**
 * Basic trait for mutable indexed sequences.
 * Fast random access and update should be guaranteed.
 *
 * @author Tongfei Chen (ctongfei@gmail.com).
 */
trait DataMutableIndexedSeq[T] extends DataMutableSeq[T] with IndexedSeq[T] {

  /**
   * Sorts this sequence in-place using the order provided.
 *
   * @param order The order for sorting
   */
  def sortInplace()(implicit order: WeakOrder[T]): Unit = {
    //TODO: This is a naive implementation of quicksort.
    //TODO: May possibly be changed to introsort or timsort in the future.
    def quicksort(i: Int, j: Int): Unit = {
      var l = i
      var r = j
      val pivot = this(l + (r - l) / 2)
      while (l <= r) {
        while (this(l) < pivot) l += 1
        while (this(r) > pivot) r -= 1
        if (l <= r) {
          swapInplace(l, r)
          l += 1
          r -= 1
        }
      }
      if (i < r) quicksort(i, r)
      if (l < j) quicksort(l, j)
    }
    quicksort(0, length - 1)
  }

  /**
   * Reverses this sequence in-place.
   */
  def reverseInplace(): Unit = {
    var l = 0
    var r = length - 1
    while (l <= r) {
      swapInplace(l, r)
      l += 1
      r -= 1
    }
  }

  /**
   *  Transforms this sequence in-place given a function.
 *
   *  @param f The function
   */
  override def mapInplace(f: T => T): Unit = {
    var i = 0
    while (i < length) {
      update(i, f(apply(i)))
      i += 1
    }
  }

  /**
    * Randomly shuffles this sequence in-place using the Fisher-Yates shuffling algorithm.
    */
  def shuffleInplace(): Unit = {
    val r = new Random()
    for (i ← Range(length - 1, 0, -1)) {
      val j = r.nextInt(i + 1)
      swapInplace(i, j)
    }
  }

}
