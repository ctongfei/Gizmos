package poly.collection

/**
 * @author Tongfei Chen (ctongfei@gmail.com).
 */
trait Map[K, +V] extends Enumerable[(K, V)] with (K =!> V) { self =>

  def get(x: K): Option[V]

  def apply(x: K): V = get(x).getOrElse(default)

  def contains(x: K): Boolean

  def default = throw new NoSuchElementException

  def getOrElse[W >: V](x: K, default: => W) = get(x) match {
    case Some(y) => y
    case None => default
  }

  def isDefinedAt(x: K) = contains(x)

  def keys = new Enumerable[K] {
    def enumerator = self.enumerator.map(_._1)
  }

  def values = new Enumerable[V] {
    def enumerator = self.enumerator.map(_._2)
  }

}
