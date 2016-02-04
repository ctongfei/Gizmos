package poly.collection.factory

import poly.algebra._
import poly.collection._
import poly.collection.builder._
import poly.collection.conversion.FromScala._
import scala.language.higherKinds

/**
 * @author Tongfei Chen
 */
trait MapFactory[M[_, _]] {

  implicit def newBuilder[K, V]: Builder[(K, V), M[K, V]]

  def apply[K, V](kvs: (K, V)*): M[K, V] = {
    val b = newBuilder[K, V]
    b.sizeHint(kvs.length)
    b addAll kvs
    b.result
  }

}

trait MapFactoryWithIntHashing[M[_, _]] {

  implicit def newBuilder[K: IntHashing, V]: Builder[(K, V), M[K, V]]

  def apply[K: IntHashing, V](kvs: (K, V)*): M[K, V] = {
    val b = newBuilder[K, V]
    b.sizeHint(kvs.length)
    b addAll kvs
    b.result
  }

}
