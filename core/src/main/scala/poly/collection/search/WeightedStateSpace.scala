package poly.collection.search

import algebra.ring.AdditiveMonoid
import poly.collection._
import poly.collection.specgroup._

/**
 * Represents a search state space whose edges are weighted (and states are equatable).
 * @author Tongfei Chen
 * @since 0.1.0
 */
trait WeightedStateSpace[S, @sp(fdi) C] extends EqStateSpace[S] {

  implicit def costMonoid: AdditiveMonoid[C]
  implicit def costOrder: Order[C]
  def succWithCost(x: S): Traversable[(S, C)]
  def succ(x: S) = succWithCost(x) map first

  def uniformCostTraversal(start: S) =
    Iterable.ofIterator(new UniformCostIterator[S, C](this, start))

  def greedyBestFirstTraversal(start: S)(heuristic: S => C) =
    Iterable.ofIterator(new GreedyBestFirstIterator[S, C](this, start, heuristic))

  def aStarTraversal(start: S)(heuristic: S => C) =
    Iterable.ofIterator(new AStarIterator[S, C](this, start, heuristic))

  def uniformCostSearch(start: S, goal: S => Boolean) =
    EqStateSpace.searchByIterator(new UniformCostIterator[S, C](this, start), goal)

  def greedyBestFirstSearch(start: S, goal: S => Boolean)(heuristic: S => C) =
    EqStateSpace.searchByIterator(new GreedyBestFirstIterator[S, C](this, start, heuristic), goal)

  def aStarSearch(start: S, goal: S => Boolean)(heuristic: S => C) =
    EqStateSpace.searchByIterator(new AStarIterator[S, C](this, start, heuristic), goal)

}

object WeightedStateSpace {

  def apply[S: Eq, C: Order : AdditiveMonoid](f: S => Traversable[(S, C)]): WeightedStateSpace[S, C] = new WeightedStateSpace[S, C] {
    def costMonoid = AdditiveMonoid[C]
    def costOrder = Order[C]
    def keyEq = Eq[S]
    def succWithCost(x: S) = f(x)
  }

}