package poly.collection.mut

import poly.collection._

/**
 * @author Tongfei Chen
 */
trait DataMutableMap[K, V] extends Map[K, V] {
  def update(x: K, y: V): Unit
  def inplaceMap(f: V => V): Unit = ???
}
