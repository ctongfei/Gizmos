package poly.collection.mut

import poly.algebra.implicits._
import poly.collection._
import poly.collection.impl._
import poly.collection.mut._
import poly.collection.node._
import poly.util.specgroup._

/**
 * @author Tongfei Chen (ctongfei@gmail.com).
 */
class AdjacencyListGraph[@sp(i) K, V, E] extends Graph[K, V, E] {

  private class VertexInfo {
    var data: V = _
    val succ = ListMap[K, E]()
  }

  private val r = HashMap[K, VertexInfo]()

  def apply(i: K): V = r(i).data

  def apply(i: K, j: K): E = r(i).succ(j)

  def containsEdge(i: K, j: K): Boolean = (for (v ← r ? i; e ← v.succ ? j) yield e).isDefined

  def containsVertex(i: K): Boolean = (r ? i).isDefined

  def keySet = r.keySet

  def outgoingKeysOf(i: K): Enumerable[K] = r(i).succ.keys

}
