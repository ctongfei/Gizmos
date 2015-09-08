package poly.collection

import poly.algebra.hkt._
import poly.collection.exception._
import poly.collection.node._
import poly.util.typeclass._
import poly.util.typeclass.ops._

/**
 * Represents a binary tree.
 * @author Tongfei Chen (ctongfei@gmail.com).
 * @since 0.1.0
 */
trait BinaryTree[+T] extends PartialFunction[Int, T] { self =>

  import BinaryTree._

  def rootNode: BinaryTreeNode[T]

  def root: T = rootNode.data

  /** Returns the maximal depth of this tree. */
  def depth: Int = ???

  /** Returns the number of nodes in this tree. */
  def size: Int = rootNode.preOrder.size

  def isEmpty = rootNode.isDummy

  /**
   * Returns the ''i''th node of this binary tree.
   * The ''i''th is defined as follows:
   *  - The index of the root node is 0;
   *  - The left child of node ''i'' is 2''i'' + 1;
   *  - The right child of node ''i'' is 2''i'' + 2.
   * @param i Index
   * @return The ''i''th node with the index defined above
   */
  def apply(i: Int): T = { //TODO: currently wrong
    var x = i + 1
    var curr = rootNode
    var depth = 1
    while (depth <= x) {
      x / depth match {
        case 0 => curr = curr.left
        case 1 => curr = curr.right
      }
      if (curr.isDummy) throw new NoSuchElementException
      x %= depth
      depth <<= 1
    }
    curr.data
  }

  def isDefinedAt(i: Int): Boolean = ???

  // HELPER FUNCTIONS

  /**
   * Returns the left subtree of this binary tree. $LAZY $CX_1
   * @return The left subtree
   */
  def left: BinaryTree[T] = ofNode(rootNode.left)

  /**
   * Returns the right subtree of this binary tree. $LAZY $CX_1
   * @return The right subtree
   */
  def right: BinaryTree[T] = ofNode(rootNode.right)

  def map[U](f: T => U): BinaryTree[U] = ofNode(rootNode.map(f))

  /** Folds a binary tree bottom-up. This is analogous to the sequence `foldRight`. */
  def foldBottomUp[U](z: U)(f: (T, U, U) => U): U = { //TODO: change to non-recursive version
    if (self.rootNode.isDummy) z
    else f(self.root, self.left.foldBottomUp(z)(f), self.right.foldBottomUp(z)(f))
  }

  def fold[U >: T](z: U)(f: (U, U, U) => U) = foldBottomUp(z)(f)

  def zip[U](that: BinaryTree[U]): BinaryTree[(T, U)] = ofNode(self.rootNode zip that.rootNode)

  def preOrder = rootNode.preOrder
  def inOrder = rootNode.inOrder
  def postOrder = rootNode.postOrder

  /**
   * Performs inverse Knuth transform on this binary tree, i.e., recover the multi-way tree
   * compactly represented by this binary tree through Knuth transform (left-child-right-sibling
   * representation). $LAZY $CX_1
   */
  def inverseKnuthTransform: Tree[T] = {
    class InverseKnuthTransformedTreeNode(val node: BinaryTreeNode[T]) extends TreeNode[T] {
      override def isDummy = node.isDummy
      def children = ??? //LinearSeq.iterate(node.left)(_.right).takeUntil(_.isDummy).map(btn => new InverseKnuthTransformedTreeNode(btn))
      def data = node.data
    }
    Tree.ofNode(new InverseKnuthTransformedTreeNode(self.rootNode))
  }

  def toGraph: Graph[Int, T, Nothing] = ???

  override def toString() = self.str

}

object BinaryTree {

  @inline def parentIndex(i: Int) = (i - 1) / 2
  @inline def leftChildIndex(i: Int) = 2 * i + 1
  @inline def rightChildIndex(i: Int) = 2 * i + 2

  def unapply[T](bt: BinaryTree[T]) = {
    if (bt.isEmpty) None
    else Some((bt.root, bt.left, bt.right))
  }

  object empty extends BinaryTree[Nothing] {
    def rootNode: BinaryTreeNode[Nothing] = throw new NoSuchElementException
  }

  def ofNode[T](n: BinaryTreeNode[T]): BinaryTree[T] = new BinaryTree[T] {
    def rootNode = n
  }

  implicit object Functor extends Functor[BinaryTree] {
    def map[T, U](t: BinaryTree[T])(f: T => U): BinaryTree[U] = t map f
  }

  implicit def Formatter[T: Formatter]: Formatter[BinaryTree[T]] = new Formatter[BinaryTree[T]] {
    def str(x: BinaryTree[T]): String =
      if (x.isEmpty) "#" else s"(${x.root.str} ${x.left.str} ${x.right.str})" //TODO: change to non-recursion
  }

}

abstract class AbstractBinaryTree[+T] extends BinaryTree[T]
