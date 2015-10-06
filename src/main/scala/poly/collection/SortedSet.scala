package poly.collection

/**
 * @author Tongfei Chen (ctongfei@gmail.com).
 * @since 0.1.0
 */
trait SortedSet[T] extends Set[T] {

  def order = elements.orderOnKey

  def equivOnKey = order

  def elements: SortedIterable[T]

}
