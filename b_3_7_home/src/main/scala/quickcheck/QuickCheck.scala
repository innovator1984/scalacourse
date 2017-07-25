package quickcheck

import common._

import org.scalacheck._
import Arbitrary._
import Gen._
import Prop._

abstract class QuickCheckHeap extends Properties("Heap") with IntHeap {

  lazy val genHeap: Gen[H] = {
    // TODO 1.A Common
    // In this assignment, you will work with the ScalaCheck library for automated specification-based testing.
    //
    // You’re given several implementations of a purely functional data structure: a heap,
    // which is a priority queue supporting operations insert, meld, findMin, deleteMin. Here is the interface:
    // trait Heap {
    //   type H // type of a heap
    //   type A // type of an element
    //   def ord: Ordering[A] // ordering on elements
    //   def empty: H // the empty heap
    //   def isEmpty(h: H): Boolean // whether the given heap h is empty
    //   def insert(x: A, h: H): H // the heap resulting from inserting x into h
    //   def meld(h1: H, h2: H): H // the heap resulting from merging h1 and h2
    //   def findMin(h: H): A // a minimum of the heap h
    //   def deleteMin(h: H): H // a heap resulting from deleting a minimum of h
    // }
    // All these operations are pure; they never modify the given heaps, and may return new heaps.
    // This purely functional interface is taken from Brodal & Okasaki’s paper, Optimal Purely Functional Priority Queues.
    // http://www.brics.dk/RS/96/37/BRICS-RS-96-37.pdf
    //
    // HACK git hub dot com
    // /jcouyang/progfun
    // /blob/master/quickcheck/src/test/scala/quickcheck/QuickCheckSuite.scala

    // QQQ
    for {
      k <- arbitrary[A]
      m <- oneOf(const(empty), genHeap)
    } yield insert(k, m)
  }
  implicit lazy val arbHeap: Arbitrary[H] = Arbitrary(genHeap)

  property("gen1") = forAll { (h: H) =>
    val m = if (isEmpty(h)) 0 else findMin(h)
    findMin(insert(m, h)) == m
  }

  // TODO 2.A Hints
  // Here are some possible properties we suggest you write.
  //  * If you insert any two elements into an empty heap, finding the minimum of the resulting heap should get
  // the smallest of the two elements back.
  property("min1") = forAll { a: Int =>
    val h = insert(a, empty)
    findMin(h) == a
  }

  // TODO 2.B Hints
  // Here are some possible properties we suggest you write.
  //  * If you insert an element into an empty heap, then delete the minimum, the resulting heap should be empty.
  property("gen2") = forAll { (is: (Int, Int)) =>
    val min1 = is._1
    val min2 = is._2
    val h = insert(min1, insert(min2, empty))
    findMin(h) == (if(min1>min2)min2 else min1)
  }

  // TODO 2.C Hints
  // Here are some possible properties we suggest you write.
  //  * Given any heap, you should get a sorted sequence of elements when continually finding and deleting minima.
  // (Hint: recursion and helper functions are your friends.)
  def getSortedList(h:H):List[A] = {
    if(isEmpty(h)) Nil
    else
      findMin(h)::getSortedList(deleteMin(h))
  }

  property("deleteMin1") = forAll { a: Int =>
    val h = insert(a, empty)
    deleteMin(h) == empty
  }

  property("deleteMin2") = forAll {(h:H, i:Int) =>
    val min = findMin(h)
    val minimal = if(min < i) min else i
    val h1 = insert(minimal, h)
    deleteMin(h1) == h
  }

  property("delete 3") = forAll { (l: List[Int], h:H) =>
    val h = l.foldLeft(empty)((acc, n)=>insert(n, acc))
    getSortedList(h) == l.sorted
  }

  // TODO 2.D Hints
  // Here are some possible properties we suggest you write.
  //  * Finding a minimum of the melding of any two heaps should return a minimum of one or the other.
  property("meld") = forAll { (h1:H,h2:H) =>
    val min1 = findMin(h1)
    val min2 = findMin(h2)
    findMin(meld(h1, h2)) == (if(min1>min2)min2 else min1)
  }
}
