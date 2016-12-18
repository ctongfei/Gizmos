package poly.collection

import poly.algebra.syntax._
import poly.collection.mut._
/**
  * @author Tongfei Chen (ctongfei@gmail.com).
  */
object SeqTest extends App {

  for (i ← 0 ~~> 4) println(i)

  val aa = new Range.Ascending(1, 22, 4)
  val bb = new Range.Ascending(2, 15, 3)
  val cc = aa intersect bb

  val a = ListSeq[Int](3, 3, 2)
  a append_! 0
  a append_! 2
  a append_! 3
  a append_! 4

  val b = ArraySeq(5, 6, 7)


  val t0 = a map { i => i * 2 }
  val t1 = a flatMap { i: Int => ArraySeq.fill(i)(i) }

  val t3 = a filter { _ % 2 == 0 }
  val t4 = a filterNot { _ > 2 }

  val t5 = a concat b
  val t6 = -1 +: a
  val t7 = b :+ 10

  val t8 = a.scanLeft(0)(_+_)
  val t9 = t8.slidingPairsWith(_+_)
  val t10 = a.suffixes

  val bp = 0


}
