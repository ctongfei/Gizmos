package poly.collection.mut

import poly.collection._
import poly.collection.conversion._
import poly.collection.factory._
import poly.collection.impl._
import poly.util.specgroup._
import scala.reflect._

/**
 * A mutable sequence backed by an array.
 * @author Tongfei Chen (ctongfei@gmail.com).
 */
class ArraySeq[T] private(private var data: ResizableSeq[T] = null) extends DataMutableIndexedSeq[T] with KeyMutableSeq[T] {

  def length = data.length

  def apply(i: Int) = data(i)

  def update(i: Int, x: T) = data.update(i, x)

  def insertAt(i: Int, x: T) = data.insertAt(i, x)

  def deleteAt(i: Int) = data.deleteAt(i)

  def prependInplace(x: T) = data.prependInplace(x)

  def appendInplace(x: T) = data.appendInplace(x)

  def clear() = data.clear()

}

object ArraySeq extends SeqFactory[ArraySeq] {

  implicit def newBuilder[T]: Builder[T, ArraySeq[T]] = new Builder[T, ArraySeq[T]] {
    val a = new ResizableSeq[T]()
    def sizeHint(n: Int) = a.ensureCapacity(n)
    def +=(x: T) = a.appendInplace(x)
    def result = new ArraySeq[T](a)
  }

}
