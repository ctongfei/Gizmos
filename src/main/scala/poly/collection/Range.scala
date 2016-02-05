package poly.collection

import poly.algebra._
import poly.algebra.syntax._
import poly.macroutil._
import scala.reflect.macros.blackbox._
import scala.language.experimental.macros

/**
 * Represents an immutable integer range.
 *
 * The difference between this class and [[scala.collection.immutable.Range]] is that
 * [[poly.collection.Range]] will attempt to inline the loop body when iterating over
 * the range using macros, which potentially makes it more efficient.
 * @author Tongfei Chen
 * @since 0.1.0
 */
sealed trait Range extends SortedIndexedSeq[Int] { self =>

  def left: Int

  def right: Int

  def step: Int

  lazy val fastLength = {
    val gap = right - left
    val len = gap / step + (if (gap % step != 0) 1 else 0)
    if (len < 0) 0 else len
  }

  def fastApply(i: Int): Int = left + i * step
  
  // HELPER FUNCTIONS
  override def head = left

  def asSet: Set[Int] = new AbstractSet[Int] {
    def equivOnKey = Equiv[Int]
    def keys = self
    def contains(x: Int) =
      if (step > 0) x >= left && x < right && (x - left) % step == 0
      else x <= left && x > right && (left - x) % (-step) == 0
  }
}

object Range {

  final class Ascending(
    val left: Int,
    val right: Int,
    val step: Int = 1
  ) extends Range { require(step > 0)

    override def foreach[U](f: Int => U) = macro ascendingMacroImpl[U]
    def order = TotalOrder[Int]
    override def tail = new Range.Ascending(left + step, right, step)
    override def reverse = new Range.Descending(left + step * (length - 1), left - math.signum(step), -step)
  }

  final class Descending(
    val left: Int,
    val right: Int,
    val step: Int = 1
  ) extends Range { require(step < 0)

    override def foreach[U](f: Int => U) = macro descendingMacroImpl[U]
    def order = TotalOrder[Int].reverse
    override def tail = new Range.Descending(left + step, right, step)
    override def reverse = new Range.Ascending(left + step * (length - 1), left - math.signum(step), -step)
  }

  /** Creates a left-inclusive-right-exclusive range [0, ''r''). */
  def apply(r: Int) = new Range.Ascending(0, r)

  /** Creates a left-inclusive-right-exclusive range [''l'', ''r''). */
  def apply(l: Int, r: Int) = new Range.Ascending(l, r)

  /** Creates a left-inclusive-right-exclusive range with the specific step size (can be negative). */
  def apply(l: Int, r: Int, step: Int) = {
    if (step > 0) new Range.Ascending(l, r, step)
    else new Range.Descending(l, r, step)
  }

  /** Creates a closed range [0, ''r'']. */
  def inclusive(r: Int) = new Range.Ascending(0, r + 1)

  /** Creates a closed range [''l'', ''r'']. */
  def inclusive(l: Int, r: Int) = new Range.Ascending(l, r + 1)

  /** Creates a closed range with the specific step size (can be negative). */
  def inclusive(l: Int, r: Int, step: Int) = {
    if (step > 0) new Range.Ascending(l, r + math.signum(step), step)
    else new Range.Descending(l, r + math.signum(step), step)
  }

  def ascendingMacroImpl[V](c: Context)(f: c.Expr[Int => V]): c.Expr[Unit] = {
    import c.universe._
    val i = TermName(c.freshName("poly$i"))
    val range = TermName(c.freshName("poly$range"))
    val limit = TermName(c.freshName("poly$limit"))
    val step = TermName(c.freshName("poly$step"))
    val tree = c.macroApplication match {
      case q"$r.foreach[$ty]($f)" =>
        q"""
          val $range = $r
          var $i = $range.left
          val $limit = $range.right
          val $step = $range.step
          while ($i < $limit) {
            $f($i)
            $i += $step
          }
        """
    }
    new InlineUtil[c.type](c).inlineAndReset[Unit](tree)
  }

  def descendingMacroImpl[V](c: Context)(f: c.Expr[Int => V]): c.Expr[Unit] = {
    import c.universe._
    val i = TermName(c.freshName("poly$i"))
    val range = TermName(c.freshName("poly$range"))
    val limit = TermName(c.freshName("poly$limit"))
    val step = TermName(c.freshName("poly$step"))
    val tree = c.macroApplication match {
      case q"$r.foreach[$ty]($f)" =>
        q"""
          val $range = $r
          var $i = $range.left
          val $limit = $range.right
          val $step = $range.step
          while ($i > $limit) {
            $f($i)
            $i += $range.step
          }
        """
    }
    new InlineUtil[c.type](c).inlineAndReset[Unit](tree)
  }

}
