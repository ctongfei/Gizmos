package poly.collection

/**
 * Represents a queue.
 * @author Tongfei Chen (ctongfei@gmail.com).
 * @since 0.1.0
 */
trait Queue[T] extends Iterable[T] {

  /** Checks if this queue is empty. */
  override def isEmpty: Boolean = size == 0

  /** Checks if this queue is not empty. */
  def notEmpty: Boolean = size != 0

  /**
   * Pushes the specified element into this queue.
   * @param x The element to be pushed
   */
  def push(x: T): Unit

  /**
   * Returns the top element of the queue.
   * @return The top element
   */
  def top: T

  /**
   * Removes the top element from the queue and returns it.
   * @return The removed element
   */
  def pop(): T

  /**
   * Returns the number of elements in this queue.
   * @return The number of elements
   */
  def size: Int

  def +=(x: T): Unit = push(x)

  def pushAll(xs: Traversable[T]) = xs foreach +=

  def ++=(xs: Traversable[T]) = pushAll(xs)

  def enqueue(x: T) = push(x)
  def dequeue() = pop()
  def front = top

}
