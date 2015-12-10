package poly.collection.search.node

import poly.algebra._
import poly.algebra.syntax._
import poly.collection.search._
import poly.util.specgroup._

/**
 * Represents a node in the fringe / open set of a searching algorithm.
 *
 * @author Yuhuan Jiang (jyuhuan@gmail.com).
 * @author Tongfei Chen (ctongfei@gmail.com).
 */
trait WithCost[S, @sp(fdi) C] extends WithParent[S] {
  def state: S
  def parent: WithCost[S, C]
  /** The known cost from the initial node to this node. */
  def g: C
}


object WithCost extends WithCostLowPriorityImplicit {

  def apply[S, @sp(fdi) C](s: S, d: Int, gv: C, p: WithCost[S, C]): WithCost[S, C] = new WithCost[S, C] {
    val state = s
    val depth = d
    val g = gv
    val parent = p
    def isDummy = false
  }

  def dummy[S, @sp(fdi) C: OrderedAdditiveGroup]: WithCost[S, C] = new WithCost[S, C] {
    def state: Nothing = throw new NoSuchElementException()
    val depth: Int = -1
    val g = zero[C]
    def parent = this
    def isDummy = true
  }

  implicit def WeightedSearchNodeInfo[S, @sp(fdi) C: OrderedAdditiveGroup]: WeightedSearchNodeInfo[WithCost[S, C], S, C] =
    new WeightedSearchNodeInfo[WithCost[S, C], S, C] {
      def startNode(s: S) = WithCost(s, 0, zero[C], dummy[S, C])
      def state(n: WithCost[S, C]) = n.state
      def nextNode(currNode: WithCost[S, C])(nextState: S, cost: C) = WithCost(nextState, currNode.depth + 1, currNode.g + cost, currNode)
    }
}

// This should have lower priority than SearchNodeWithHeuristic.order
private[collection] trait WithCostLowPriorityImplicit {
  implicit def order[S, @sp(fdi) C: OrderedAdditiveGroup]: WeakOrder[WithCost[S, C]] = new WeakOrder[WithCost[S, C]] {
      def cmp(x: WithCost[S, C], y: WithCost[S, C]) = x.g >?< y.g
  }
}
