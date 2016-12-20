package poly

import poly.algebra.specgroup._
import poly.collection.conversion._
import poly.collection.conversion.FromScala._
import poly.collection.mut._

import scala.language.implicitConversions

package object collection extends ImplicitOperators {

  type <=>[A, B] = Bijection[A, B]

  /** Expresses a type constraint that a type is `Int`. */
  type IsInt[A] = A =:= Int

  private[poly] val singletonIsIntEv = new Object
  /** Forcibly prove that type K is `Int`. Use with caution. */
  private[poly] def evInt[K] = singletonIsIntEv.asInstanceOf[K =:= Int]

  /** Returns the first element of two elements. */
  //@inline def first[@sp(di) α, @sp(di) β](a: α, b: β) = a

  /** Returns the second element of two elements. */
  //@inline def second[@sp(di) α, @sp(di) β](a: α, b: β) = b

  /** Returns the first element of a pair. */
  @inline def first[@sp(spTuple2) α, @sp(spTuple2) β](pair: (α, β)) = pair._1

  /** Returns the second element of a pair. */
  @inline def second[@sp(spTuple2) α, @sp(spTuple2) β](pair: (α, β)) = pair._2

  @inline def third[γ](triple: (_, _, γ)) = triple._3

  @inline private[poly] def hashByRef(x: AnyRef) = System.identityHashCode(x)

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

  /**
   * Gets the underlying `Array[T]` from a varargs argument of type `T*`.
   */
  private[poly] def getArrayFromVarargs[T](xs: scala.Seq[T]): Array[T] = xs match {
    case xs: scala.collection.mutable.WrappedArray[T] => xs.array
    case _ => scalaIterableAsPoly(xs).to(ArraySeq).data.data.asInstanceOf[Array[T]] // this cast is safe!
  }


  private[poly] implicit class WithModOps(val x: Int) extends AnyVal {
    def %+(mod: Int) = {
      val r = x % mod
      if (r >= 0) r else r + mod
    }
  }

  implicit def arrayAsPoly[T](a: Array[T]): IndexedSeq[T] = new ArrayAsPoly[T](a)
  implicit def charSequenceAsPoly(s: CharSequence): IndexedSeq[Char] = new JavaCharSequenceAsPoly(s)
  implicit def booleanFunctionAsPoly[T](f: T => Boolean): Predicate[T] = new BooleanFunctionAsPredicate[T](f)
  implicit def stringBuilderAsPoly(sb: StringBuilder): Builder[Char, String] = new ScalaStringBuilderAsBuilder(sb)
  implicit def javaStringBuilderAsPoly(sb: java.lang.StringBuilder): Builder[Char, String] = new JavaStringBuilderAsPoly(sb)


}
