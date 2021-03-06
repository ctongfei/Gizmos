package poly.collection.conversion

import poly.collection._
import poly.collection.mut._
import poly.collection.node._
import poly.collection.typeclass._

import scala.{collection => sc}
import scala.collection.{mutable => scm}

/**
 * @author Tongfei Chen
 */
class ScalaTraversableAsPoly[T](xs: sc.Traversable[T]) extends AbstractTraversable[T] {
  def foreach[V](f: T => V) = xs foreach f
}

class ScalaIteratorAsPoly[T](xs: sc.Iterator[T]) extends AbstractIterator[T] {
  private[this] var curr = default[T]
  def current = curr
  def advance() = if (xs.hasNext) {
    curr = xs.next()
    true
  } else false
}

class ScalaIterableAsPoly[T](xs: sc.Iterable[T]) extends AbstractIterable[T] {
  def newIterator = new ScalaIteratorAsPoly(xs.iterator)
}

class ScalaLinearSeqAsPoly[T](xs: sc.LinearSeq[T]) extends AbstractSeq[T] with SeqNode[T] with SeqNodeLike[T, ScalaLinearSeqAsPoly[T]] {
  def next: ScalaLinearSeqAsPoly[T] = new ScalaLinearSeqAsPoly(xs.tail)
  def headNode = this
  def data: T = xs.head
  def isDummy = xs.isEmpty
  override def newIterator = new ScalaIteratorAsPoly(xs.iterator)
  override def apply(i: Int) = xs(i)
  override def length = xs.length
}

class ScalaIndexedSeqAsPoly[T](xs: sc.IndexedSeq[T]) extends AbstractIndexedSeq[T] {
  def fastLength: Int = xs.length
  def fastApply(i: Int): T = xs(i)
}

class ScalaSetAsPoly[T](xs: sc.Set[T]) extends AbstractSet[T] {
  def keyEq = Hash.default[T]
  def keys = new ScalaIterableAsPoly(xs)
  def contains(x: T): Boolean = xs contains x
}

class ScalaSortedSetAsPoly[T](xs: sc.SortedSet[T]) extends AbstractSortedSet[T] {
  def keyOrder: Order[T] = xs.ordering
  def keys = new ScalaIterableAsPoly(xs).asIfSorted(xs.ordering)
  def contains(x: T): Boolean = xs contains x
}

class ScalaMapAsPoly[K, V](xs: sc.Map[K, V]) extends AbstractMap[K, V] {
  def keySet = new ScalaSetAsPoly(xs.keySet)
  def ?(k: K) = xs get k
  def apply(k: K) = xs(k)
  override def pairs = new ScalaIterableAsPoly(xs)
}

class ScalaSortedMapAsPoly[K, V](xs: sc.SortedMap[K, V]) extends AbstractKeySortedMap[K, V] {
  def keySet = new ScalaSortedSetAsPoly(xs.keySet)
  def ?(k: K) = xs get k
  def apply(k: K) = xs(k)
  override def pairs = new ScalaIterableAsPoly(xs).asIfSorted((xs.ordering: Order[K]) on first)
}

class ScalaStackAsPoly[T](ss: scm.Stack[T]) extends Queue[T] {
  def elements = new ScalaIterableAsPoly(ss)
  def enqueue(x: T) = ss push x
  def front = ss.top
  def dequeue() = ss.pop()
}

class ScalaArrayStackAsPoly[T](sas: scm.ArrayStack[T]) extends Queue[T] {
  def elements = new ScalaIterableAsPoly(sas)
  def enqueue(x: T) = sas push x
  def front = sas.top
  def dequeue() = sas.pop()
}

class ScalaQueueAsPoly[T](sq: scm.Queue[T]) extends Queue[T] {
  def elements = new ScalaIterableAsPoly(sq)
  def enqueue(x: T) = sq += x
  def front = sq.front
  def dequeue() = sq.dequeue()
}

class ScalaPriorityQueueAsPoly[T](spq: scm.PriorityQueue[T]) extends PriorityQueue[T] {
  def elements = new ScalaIterableAsPoly(spq)
  def elementOrder = spq.ord
  def enqueue(x: T) = spq += x
  def front = spq.head
  def dequeue() = spq.dequeue()
}
