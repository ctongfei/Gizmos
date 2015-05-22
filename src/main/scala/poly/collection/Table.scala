package poly.collection

/**
 * @author Tongfei Chen (ctongfei@gmail.com).
 */
trait Table[+T] extends ((Int, Int) => T) {

  def numRows: Int

  def numCols: Int
  
  def apply(i: Int): T

  def size = numRows * numCols


  override def equals(that: Any) = that match {
    case other: Table[T] => ???
    case _ => false
  }

  override def toString() = ???

}

