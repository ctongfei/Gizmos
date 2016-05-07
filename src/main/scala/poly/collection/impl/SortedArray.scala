package poly.collection.impl

import poly.algebra._
import poly.collection._
import poly.collection.exception._

/**
 * A resizable sorted array. This serves as the implementation container of `SortedArrayX` classes.
 * @author Tongfei Chen
 */
class SortedArray[T] private[poly](val data: ResizableSeq[T])(implicit val orderOnElements: Order[T])
  extends SortedIndexedSeq[T]
{

  def fastLength = data.fastLength

  def fastApply(i: Int) = data(i)

  def add(x: T) = data.insertInplace(lowerBound(x), x)

  def deleteAt(i: Int) = data.deleteInplace(i)

  def remove(x: T) = binarySearch(x) match {
    case Some(i) => data.deleteInplace(i)
    case None =>
  }

}
