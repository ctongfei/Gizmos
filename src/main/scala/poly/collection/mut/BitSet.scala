package poly.collection.mut

import poly.algebra._
import poly.collection._

/**
 * @author Tongfei Chen (ctongfei@gmail.com).
 */
class BitSet extends MutableSet[Int] {

  def equivOnKey = Equiv.default[Int]

  def add(x: Int) = ???

  def remove(x: Int) = ???

  /** Tests if an element belongs to this set. */
  def contains(x: Int) = ???

  def size = ???

  def elements = ???
}
