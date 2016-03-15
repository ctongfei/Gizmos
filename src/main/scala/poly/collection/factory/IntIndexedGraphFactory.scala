package poly.collection.factory

import poly.collection._
import poly.collection.builder._
import poly.collection.conversion.FromScala._
import scala.language.higherKinds

/**
 * @author Tongfei Chen
 */
trait IntIndexedGraphFactory[+IG[_, _]] {

   implicit def newBuilder[V, E]: GraphBuilder[Int, V, E, IG[V, E]]

   def empty[V, E]: IG[V, E] = newBuilder[V, E].result

   def apply[V, E](vs: V*)(es: (Int, Int, E)*): IG[V, E] = {
     val b = newBuilder[V, E]
     for ((i, v) ← vs.indexed) b.addNode(i, v)
     for ((i, j, e) ← es) b.addEdge(i, j, e)
     b.result
   }
  
 }
