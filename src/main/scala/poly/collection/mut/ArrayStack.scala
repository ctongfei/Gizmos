package poly.collection.mut

import poly.collection._
import poly.collection.exception._
import poly.collection.factory._
import poly.collection.impl._
import poly.util.specgroup._
import scala.reflect._

/**
 * An array-backed stack.
 * @author Tongfei Chen (ctongfei@gmail.com).
 */
class ArrayStack[@specialized(Int, Double) T] (private var data: ResizableArray[T] = null) extends Queue[T] {

  override def size = data.length

  def push(x: T): Unit = data.append(x)

  def top: T = {
    if (isEmpty) throw new StackEmptyException
    data(data.length - 1)
  }

  def pop(): T = {
    val x = top
    data.deleteAt(data.length - 1)
    x
  }

}

object ArrayStack extends CollectionFactoryWithTag[ArrayStack] {

  implicit def newBuilder[@sp(fdi) T: ClassTag]: Builder[T, ArrayStack[T]] = new Builder[T, ArrayStack[T]] {
    var data = new ResizableArray[T]()
    def sizeHint(n: Int) = data.ensureCapacity(n)
    def +=(x: T) = data.append(x)
    def result = new ArrayStack(data)
  }

}
