package poly.collection.search

import poly.collection.conversion.Scala._
import poly.collection.search.ops._

/**
 * @author Tongfei Chen (ctongfei@gmail.com).
 */
object SearchTest extends App {


  val g: Map[Int, List[Int]] = Map(0 → List(1, 2, 3), 1 → List(2, 3, 4), 2 → List(3), 3 → List(), 4 → List())
  val g0 = g(0)

  implicit val ss = new StateSpace[Int] {
    def succ(x: Int) = g(0)
  }


  0.depthFirstTreeSearch(_ == 4) foreach println


}
