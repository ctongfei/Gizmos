package poly.collection.algorithm

import poly.algebra._
import poly.algebra.function._
import poly.algebra.ops._
import poly.algebra.std._
import poly.collection._
import poly.collection.mut._

/**
 * Runs Floyd-Warshall algorithm on a specified graph.
 * @author Tongfei Chen
 * @since 0.1.0
 */
class AllPairsShortestPath[K, E : OrderedAdditiveGroup : HasTop](val graph: Graph[K, E]) extends MetricSpace[K, E] {

  private[this] implicit val eq = graph.eqOnKeys

  private[this] val d = AutoMap[(K, K), E]().withDefaultUpdate(zero[E])
  private[this] val mid = AutoMap[(K, K), K]().withDefaultUpdate(default[K])

  for (i <- graph.keys; j <- graph.keys) {
    if (i == j) d(i -> j) = zero[E]
    else if (graph containsArc (i, j)) d(i -> j) = graph(i, j)
  }

  // Floyd-Warshall algorithm
  for (k <- graph.keys; i <- graph.keys; j <- graph.keys) {
    val dik = d ? (i -> k)
    val dkj = d ? (k -> j)
    val dij = d ? (i -> j)
    val dikj = for { ik <- dik; kj <- dkj } yield ik + kj
    if (dikj.isDefined && (dij.isEmpty || (dikj.get < dij.get))) {
      d(i -> j) = dikj.get
      mid(i -> j) = k
    }
  }

  def dist(i: K, j: K): E = distanceBetween(i, j).getOrElse(top[E])

  def distanceBetween(i: K, j: K) = d ? (i, j)

  def pathBetween(i: K, j: K): Seq[K] = {
    if (i === j) Seq(i)
    else if (graph.containsArc(i, j)) Seq(i, j)
    else mid ? (i -> j) match {
      case None => Seq.Empty
      case Some(m) => pathBetween(i, m) ++ pathBetween(m, j).tail
    }
  }
}
