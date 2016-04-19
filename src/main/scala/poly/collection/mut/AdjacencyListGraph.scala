package poly.collection.mut

import poly.algebra.specgroup._
import poly.collection._
import poly.collection.builder._
import poly.collection.factory._

/**
 * @author Tongfei Chen
 */
class AdjacencyListGraph[@sp(Int) K, V, E]() extends Graph[K, V, E] {

  private class VertexInfo {
    var data: V = _
    val succ = ListMap[K, E]() // TODO: ListMap?
  }

  private val r = AutoMap[K, VertexInfo]()

  def apply(i: K): V = r(i).data

  def apply(i: K, j: K): E = r(i).succ(j)

  def containsArc(i: K, j: K): Boolean = (for (v ← r ? i; e ← v.succ ? j) yield e).isDefined

  def keySet = r.keySet

  def outgoingKeysOf(i: K): Iterable[K] = r(i).succ.keys

}

object AdjacencyListGraph extends GraphFactory[AdjacencyListGraph] {
  implicit def newBuilder[K, V, E] = new GraphBuilder[K, V, E, AdjacencyListGraph[K, V, E]] {
    private val g = new AdjacencyListGraph[K, V, E]()
    def numNodesHint(n: Int) = ???
    def addEdgeInplace(i: K, j: K, e: E) = ???
    def addNodeInplace(i: K, v: V) = ???
    def result = ???
  }
}