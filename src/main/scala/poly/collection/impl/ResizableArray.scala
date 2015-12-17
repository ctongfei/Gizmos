package poly.collection.impl

import poly.collection._

/**
 * @author Tongfei Chen
 */
final class ResizableArray[T]
(private[this] var cap: Int = Settings.ArrayInitialSize) { self =>

  private[this] var data: Array[AnyRef] = Array.ofDim[AnyRef](math.max(nextPowerOfTwo(cap), Settings.ArrayInitialSize))

  def ensureCapacity(minCapacity: Int): Unit = {
    if (cap < minCapacity) {
      val newCapacity = nextPowerOfTwo(minCapacity)
      val newData = Array.ofDim[AnyRef](newCapacity)
      System.arraycopy(data, 0, newData, 0, cap)
      data = newData
      cap = newCapacity
    }
  }

  def apply(i: Int) = data(i).asInstanceOf[T]

  def capacity = cap

  def update(i: Int, x: T) = {
    if (i >= capacity) ensureCapacity(i + 1)
    data(i) = x.asInstanceOf[AnyRef]
  }

}
