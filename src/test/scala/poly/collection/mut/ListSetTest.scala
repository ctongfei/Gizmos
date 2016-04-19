package poly.collection.mut

import poly.algebra.syntax._

/**
 * @author Tongfei Chen
 */
object ListSetTest extends App {

  val l = ListSet(1, 2, 3, 4, 5)
  l.add(8)
  println(l.elements.buildString(" "))
  l.remove(1)

  println(l.elements.buildString(" "))
  l.remove(4)

  println(l.elements.buildString(" "))
  l.remove(7)

  println(l.elements.buildString(" "))
  l.add(3)

  println(l.elements.buildString(" "))
  l.add(4)

  println(l.elements.buildString(" "))

}
