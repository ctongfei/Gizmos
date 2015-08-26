package poly.collection.algorithm

import poly.algebra._
import poly.algebra.function._
import poly.algebra.ops._
import poly.collection._
import poly.collection.mut._

/**
 * Runs the Floyd-Warshall algorithm on a specified graph.
 * @author Tongfei Chen (ctongfei@gmail.com).
 */
class AllPairsShortestPath[K, E : AdditiveMonoid : WeakOrder : HasTop]
  (val graph: Graph[K, Any, E]) extends MetricSpace[K, E]
{

  private[this] val max = top[E]

  private[this] val d = HashMap[(K, K), E]()
  private[this] val mid = HashMap[(K, K), K]()

  for (i ← graph.keys; j ← graph.keys) {
    if (i == j) d(i → j) = zero[E]
    if (graph containsEdge (i, j)) d(i → j) = graph(i, j)
  }

  // Floyd-Warshall algorithm
  for (k ← graph.keys; i ← graph.keys; j ← graph.keys) {
    val dik = d.getOrElse(i → k, max)
    val dkj = d.getOrElse(k → j, max)
    val dij = d.getOrElse(i → j, max)
    val dikj = dik + dkj
    if (dikj < dij) {
      d(i → j) = dikj
      mid(i → j) = k
    }
  }

  def dist(i: K, j: K): E = d.getOrElse(i → j, max)
  def pathBetween(i: K, j: K): Seq[K] = ???
}
