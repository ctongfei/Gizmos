package poly.collection.factory

import poly.collection._
import poly.collection.conversion.Scala._
import scala.language.higherKinds

/**
 * @author Tongfei Chen (ctongfei@gmail.com).
 */
trait IntIndexedGraphFactory[+IG[_, _]] {

   implicit def newBuilder[V, E]: GraphBuilder[Int, V, E, IG[V, E]]

   def empty[V, E]: IG[V, E] = newBuilder[V, E].result

   def apply[V, E](vs: V*)(es: (Int, Int, E)*): IG[V, E] = {
     val b = newBuilder[V, E]
     b addNodes vs.zipWithIndex.map(_.swap) //TODO: efficiency?
     b addEdges es
     b.result
   }
  
 }
