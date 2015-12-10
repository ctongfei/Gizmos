package poly.collection.search

import poly.collection._
import poly.collection.mut._
import poly.collection.exception._

/**
  * An extremely generic iterator that executes a search algorithm.
  * @tparam S Type of state
  * @tparam N Type of search node
  * @param S The searching state space
  * @param N A typeclass instance that witnesses the additional information stored on search nodes
  * @param pruning A typeclass instance that dictates which nodes should be pruned in the searching process
  * @param fringe A fringe for storing the search nodes
  * @param start Starting state
  * @author Yuhuan Jiang (jyuhuan@gmail.com).
  * @author Tongfei Chen (ctongfei@gmail.com).
  * @since 0.1.0
  */
abstract class Searcher[S, N](
  pruning: NodePruning[N],
  fringe: Queue[N],
  start: S)
  (implicit S: StateSpace[S], N: SearchNodeInfo[N, S]) extends SearchIterator[S, N] {

  private[this] var curr: N = throw new DummyNodeException

  fringe += N.startNode(start)

  def currentNode = curr
  def currentState = N.state(curr)

  def advance() = {
    if (fringe.notEmpty) {
      curr = fringe.pop()
      if (!pruning.shouldBePruned(curr))
        fringe ++= S.succ(N.state(curr)).map(N.nextNode(curr))
      true
    }
    else false
  }
}

class DepthFirstTreeIterator[S](ss: StateSpace[S], start: S)
  extends Searcher[S, S](NodePruning.None, ArrayStack[S](), start)(ss, SearchNodeInfo.None)

class BreadthFirstTreeIterator[S](ss: StateSpace[S], start: S)
  extends Searcher[S, S](NodePruning.None, ArrayQueue[S](), start)(ss, SearchNodeInfo.None)

class DepthFirstIterator[S](ss: StateSpace[S], start: S)
  extends Searcher[S, S](NodePruning.None, DistinctQueue[ArrayStack, S](), start)(ss, SearchNodeInfo.None)

class BreadthFirstIterator[S](ss: StateSpace[S], start: S)
  extends Searcher[S, S](NodePruning.None, DistinctQueue[ArrayQueue, S](), start)(ss, SearchNodeInfo.None)
