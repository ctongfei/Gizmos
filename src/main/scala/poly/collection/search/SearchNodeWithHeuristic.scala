package poly.collection.search

import poly.algebra._
import poly.algebra.ops._
import poly.algebra.function._
import poly.util.specgroup._

/**
 * Represents a node in the fringe / open set of a searching algorithm.
 * @author Yuhuan Jiang (jyuhuan@gmail.com).
 * @author Tongfei Chen (ctongfei@gmail.com).
 */
trait SearchNodeWithHeuristic[+S, @sp(fdi) C] extends SearchNodeWithCost[S, C] {

  def parent: SearchNodeWithHeuristic[S, C]

  /** The heuristic estimate of the cost from this node to the goal. */
  def h: C

  def f(implicit C: OrderedAdditiveGroup[C]) = g + h
}


object SearchNodeWithHeuristic {

  implicit def order[S, @sp(fdi) C: OrderedAdditiveGroup]: WeakOrder[SearchNodeWithHeuristic[S, C]] =
    new WeakOrder[SearchNodeWithHeuristic[S, C]] {
      def cmp(x: SearchNodeWithHeuristic[S, C], y: SearchNodeWithHeuristic[S, C]) = x.f >?< y.f
    }

  def apply[S, @sp(fdi) C](s: S, d: Int, gv: C, hv: C, p: SearchNodeWithHeuristic[S, C]): SearchNodeWithHeuristic[S, C] = new SearchNodeWithHeuristic[S, C] {
    val state = s
    val depth = d
    val g = gv
    val h = hv
    val parent = p
    def isDummy = false
  }

  def dummy[@sp(fdi) C: OrderedAdditiveGroup]: SearchNodeWithHeuristic[Nothing, C] = new SearchNodeWithHeuristic[Nothing, C] {
    def state: Nothing = throw new NoSuchElementException()
    val depth: Int = -1
    val g = zero[C]
    val h = zero[C]
    def parent: SearchNodeWithHeuristic[Nothing, C] = this
    def isDummy = true
  }
}

