package poly

import poly.algebra.specgroup._
import poly.collection.builder._

import scala.collection._
import scala.language.implicitConversions

package object collection extends ImplicitWrappers {
  /** Returns the first element of two elements. */
  @inline def first[@sp(fdi) α, @sp(fdi) β](a: α, b: β) = a

  /** Returns the second element of two elements. */
  @inline def second[@sp(fdi) α, @sp(fdi) β](a: α, b: β) = b

  /** Returns the first element of a pair. */
  @inline def firstOfPair[@sp(fdi) α, @sp(fdi) β](pair: (α, β)) = pair._1

  /** Returns the second element of a pair. */
  @inline def secondOfPair[@sp(fdi) α, @sp(fdi) β](pair: (α, β)) = pair._2

  @inline def thirdOfTriple[γ](triple: (_, _, γ)) = triple._3

  @inline private[poly] def default[T]: T = {
    class Default {
      var default: T = _
    }
    (new Default).default
  }
  
  private[poly] def nextPowerOfTwo(x: Int): Int = {
    var c = x - 1
    c |= c >>> 1
    c |= c >>> 2
    c |= c >>> 4
    c |= c >>> 8
    c |= c >>> 16
    c + 1
  }

  private[poly] def getArrayFromVarargs[T](xs: scala.Seq[T]): Array[T] = xs match {
    case xs: mutable.WrappedArray[T] => xs.array
  }

}
