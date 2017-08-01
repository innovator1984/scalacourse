package reductions

import scala.annotation._
import org.scalameter._
import common._

object ParallelParenthesesBalancingRunner {

  @volatile var seqResult = false

  @volatile var parResult = false

  val standardConfig = config(
    Key.exec.minWarmupRuns -> 40,
    Key.exec.maxWarmupRuns -> 80,
    Key.exec.benchRuns -> 120,
    Key.verbose -> true
  ) withWarmer(new Warmer.Default)

  def main(args: Array[String]): Unit = {
    val length = 100000000
    val chars = new Array[Char](length)
    val threshold = 10000
    val seqtime = standardConfig measure {
      seqResult = ParallelParenthesesBalancing.balance(chars)
    }
    println(s"sequential result = $seqResult")
    println(s"sequential balancing time: $seqtime ms")

    val fjtime = standardConfig measure {
      parResult = ParallelParenthesesBalancing.parBalance(chars, threshold)
    }
    println(s"parallel result = $parResult")
    println(s"parallel balancing time: $fjtime ms")
    println(s"speedup: ${seqtime / fjtime}")
  }
}

object ParallelParenthesesBalancing {

  /** Returns `true` iff the parentheses in the input `chars` are balanced.
   */
  def balance(chars: Array[Char]): Boolean = {
    // TODO 3.A Parallel Parentheses Balancing
    // In this part of the assignment, we recall the Parenthesis Balancing assignment
    // that might be familiar to you from the Functional Programming in Scala course.
    // Here, the task is to, given an array of characters, decide if the parentheses in the array are balanced.
    //
    // Let us recall a few examples of strings in which parentheses are correctly balanced:
    //    (if (zero? x) max (/ 1 x))
    //    I told him (that it's not (yet) done). (But he wasn't listening)
    // Similarly, the parentheses in the following strings are not balanced:
    //    (o_()
    //    :-)
    //    ())(
    // Implement a sequential function balance, which returns true iff the parentheses in the array are balanced:
    //    def balance(chars: Array[Char]): Boolean
    //
    // QQQ
    def checkOpenClose(chars: Array[Char], openCount: Int): Boolean = {
      if (chars.isEmpty) openCount == 0
      else if (openCount < 0) false
      else if (chars.head == '(') checkOpenClose(chars.tail, openCount + 1)
      else if (chars.head == ')') checkOpenClose(chars.tail, openCount - 1)
      else checkOpenClose(chars.tail, openCount)
    }
    checkOpenClose(chars, 0)
  }

  /** Returns `true` iff the parentheses in the input `chars` are balanced.
   */
  def parBalance(chars: Array[Char], threshold: Int): Boolean = {
    // TODO 3.B Parallel Parentheses Balancing
    // Next, you will implement a parallel version of this method. By now, you're already an expert
    // at implementing the structure of a reduction algorithm, so you should have no problem there.
    // The tricky part in parallel parentheses balancing is choosing the reduction operator -- you probably
    // implemented balance by keeping an integer accumulator, incrementing it for left parentheses
    // and decrementing it for the right ones, taking care that this accumulator does not drop below zero.
    // Parallel parentheses balancing will require a bit more ingenuity on your part,
    // so we will give you a hint -- you will need two integer values for the accumulator.
    //
    // Implement the parBalance method, which checks if the parentheses in the input array are balanced
    // using two helper methods reduce and traverse. These methods implement the parallel reduction
    // and the sequential traversal part, respectively:
    //    def parBalance(chars: Array[Char], threshold: Int): Boolean = {
    //      def traverse(from: Int, until: Int, _???_: Int, _???_: Int): ???
    //      def reduce(from: Int, until: Int): ??? = ???
    //      reduce(0, chars.length) == ???
    //    }
    // In this case, we again use the fixed threshold parameter, as we did in the lectures.
    // Sections with size smaller or equal to the threshold should be processed sequentially.
    // For maximum performance, use a while loop in the traverse method,
    // or make traverse tail-recursive -- do not use a Range.
    //
    // Now, run the ParallelParenthesesBalancing application:
    //    runMain reductions.ParallelParenthesesBalancingRunner
    //
    // How large was your speedup?
    // If you are looking for additional challenges, prove that your reduction operator is associative!

    def traverse(idx: Int, until: Int, arg1: Int, arg2: Int): (Int, Int) = {
      if (idx < until) {
        chars(idx) match {
          case '(' => traverse(idx + 1, until, arg1 + 1, arg2)
          case ')' =>
            if (arg1 > 0) traverse(idx + 1, until, arg1 - 1, arg2)
            else traverse(idx + 1, until, arg1, arg2 + 1)
          case _ => traverse(idx + 1, until, arg1, arg2)
        }
      } else (arg1, arg2)
    }

    def oldTraverse(idx: Int, until: Int/*, arg1: Int, arg2: Int*/) : (Int, Int) = {
      // TODO 3.C Parallel Parentheses Balancing
      // Implement the parBalance method, which checks if the parentheses in the input array are balanced
      // using two helper methods reduce and traverse. These methods implement the parallel reduction
      // and the sequential traversal part, respectively:
      //      def traverse(from: Int, until: Int, _???_: Int, _???_: Int): ???
      // QQQ
      var i = idx
      var begin = 0
      var end = 0
      var switched = false

      while (i < until) {
        switched = begin < 0
        if(chars(i) == '(') {
          end = if(switched) end + 1 else end
          begin = if(!switched) begin + 1 else begin
        }
        if(chars(i) == ')') {
          end = if(switched) end - 1 else end
          begin = if(!switched) begin - 1 else begin
        }
        i += 1
      }
      (begin, end)
    }

    def oldReduce(from: Int, until: Int): (Int, Int) = {
      // TODO 3.D Parallel Parentheses Balancing
      // Implement the parBalance method, which checks if the parentheses in the input array are balanced
      // using two helper methods reduce and traverse. These methods implement the parallel reduction
      // and the sequential traversal part, respectively:
      //      def reduce(from: Int, until: Int): ??? = ???
      // QQQ
      if (until - from <= threshold) oldTraverse(from, until)
      else {
        val mid = from + (until - from) / 2
        val (pair1, pair2) = parallel(reduce(from, mid), reduce(mid, until))

        if (pair1._1 < 0 && pair2._1 > 0) (pair1._1, pair2._1 + pair1._2 + pair2._2)
        else if(pair2._1 < 0 && pair1._2 > 0) (pair1._1 + pair2._1 + pair1._2, pair2._2)
        else (pair1._1 + pair2._1, pair1._2 + pair2._2)
      }
    }

    def reduce(from: Int, until: Int): (Int, Int) = {
      val size = until - from
      if (size > threshold) {
        val halfSize = size / 2
        val ((a1, a2), (b1, b2)) = parallel(reduce(from, from + halfSize), reduce(from + halfSize, until))
        if (a1 > b2) {
          // )))((())(( => )))(((
          (a1 - b2 + b1) -> a2
        } else {
          // )))(()))(( => ))))((
          b1 -> (b2 - a1 + a2)
        }
      }
      else {
        traverse(from, until, 0, 0)
      }
    }

    reduce(0, chars.length) == (0, 0)

    // val res = reduce(0, chars.length)
    // res._1 + res._2 == 0 && res._1 >= 0
  }

  // For those who want more:
  // Prove that your reduction operator is associative!

}
