package poly.collection

import java.util._

import org.scalatest._
import poly.collection.ops._
import poly.algebra.syntax._
import poly.collection.testutil._
import TestUtil._
/**
 * @author Tongfei Chen (ctongfei@gmail.com).
 */
class TraversableTest extends FunSuite {

  val n = 1000

  def sampleArray = {
    val r = new Random()
    val n = r.nextInt(100)
    val a = Array.fill(n)(r.nextInt(100))
    a
  }

  def genVal = {
    val x = sampleArray
    val s = scala.Traversable(x: _*)
    val p = arrayAsPoly(x).asTraversable
    (s, p)
  }

  test("Equality") {
    for (i ← 0 until n) {
      val (s, p) = genVal
      (p intersect p) foreach println
      s traversable_=== p
    }
  }

  test("Map") {
    for (i ← 0 until n) {
      val (s, p) = genVal
      s.map(_ * 2) traversable_=== p.map(_ * 2)
    }
  }

  test("Filter") {
    for (i ← 0 until n) {
      val (s, p) = genVal
      s.filter(_ % 2 == 1) traversable_=== p.filter(_ % 2 == 1)
    }
  }

  test("FlatMap") {
    for (i ← 0 until n) {
      val (s, p) = genVal
      s.flatMap(i => scala.Traversable.fill(i)(i)) traversable_=== p.flatMap(i => i repeat i)
    }
  }

}
