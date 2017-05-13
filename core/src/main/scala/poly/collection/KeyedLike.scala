package poly.collection


/**
  * Represents a data structure that can be indexed by keys of a specific type.
  * @author Tongfei Chen
  * @since 0.1.0
  */
trait KeyedLike[@specialized(Int) K, +Coll <: KeyedLike[K, Coll]] extends Keyed[K] { self =>

  /** Tests if this structure contains an item with the specified key. */
  def containsKey(k: K): Boolean
  
  /** Tests if the specific key is absent in this structure. */
  final def notContainsKey(k: K) = !containsKey(k)

  /** Returns an iterable collection of all the keys in this structure. */
  def keys: Iterable[K]

  /** Returns a set of all the keys in this structure. */
  def keySet: Set[K]

  /** Returns the restricted substructure obtained by choosing a smaller domain for the key given a specific predicate. */
  def filterKeys(f: K => Boolean): Coll

}
