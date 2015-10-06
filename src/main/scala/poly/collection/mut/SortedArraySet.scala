package poly.collection.mut

import poly.collection._
import poly.collection.impl._

/**
 * @author Tongfei Chen (ctongfei@gmail.com).
 */
class SortedArraySet[T] private(private val data: SortedArray[T]) extends SortedSet[T] with MutableSet[T] {

  override val order = data.orderOnKey

  def contains(x: T) = data.tryBinarySearch(x) >= 0

  def elements = data

  def add(x: T) = data.add(x)

  def remove(x: T) = data.remove(x)

  def size: Int = data.size
}
