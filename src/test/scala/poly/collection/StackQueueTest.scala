package poly.collection

import poly.collection.mut._

/**
 * @author Tongfei Chen
 */
object StackQueueTest extends App {

  val q = ArrayQueue[Int]()
  val t = Iterable.iterate(0)(_ + 1)
  q += 0
  q += 1
  q += 2
  q += 3
  q += 4
  q += 5
  q.pop()
  q.pop()
  q.pop()
  q += 6
  q += 7
  q += 8
  q += 9
  q += 10
  val bp = 0


}
