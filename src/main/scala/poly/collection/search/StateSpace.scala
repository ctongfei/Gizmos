package poly.collection.search

import poly.collection._

/**
 * @author Tongfei Chen
 */
trait StateSpace[S] { self =>

  /** Returns the successive states of the specified state under this state space. */
  def succ(x: S): Traversable[S]

  def filterKeys(f: S => Boolean): StateSpace[S] = new StateSpaceT.KeyFiltered(self, f)

  /**
   * Depth-first traverses this state space from the given starting state.
   * This method uses tree traversal: The user must guarantee that the state space is a tree.
   * Otherwise, use the [[depthFirstTraversal]] method.
   * $LAZY */
  def depthFirstTreeTraversal(start: S) =
    Iterable.ofIterator(new DepthFirstTreeIterator(this, start))

  /** Breadth-first traverses this state space from the given starting state.
   * This method uses tree traversal: The user must guarantee that the state space is a tree.
   * Otherwise, use the [[breadthFirstTraversal]] method.
   * $LAZY */
  def breadthFirstTreeTraversal(start: S) =
    Iterable.ofIterator(new BreadthFirstTreeIterator(this, start))

}

object StateSpace {

  def create[S](f: S => Traversable[S]): StateSpace[S] = new StateSpaceT.BySucc(f)

}

private[poly] object StateSpaceT {

  class BySucc[S](f: S => Traversable[S]) extends StateSpace[S] {
    def succ(x: S) = f(x)
  }

  class KeyFiltered[S](self: StateSpace[S], f: S => Boolean) extends StateSpace[S] {
    def succ(x: S) = self.succ(x) filter f
  }

}