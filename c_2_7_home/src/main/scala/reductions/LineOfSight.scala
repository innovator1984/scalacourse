package reductions

import org.scalameter._
import common._

object LineOfSightRunner {
  
  val standardConfig = config(
    Key.exec.minWarmupRuns -> 40,
    Key.exec.maxWarmupRuns -> 80,
    Key.exec.benchRuns -> 100,
    Key.verbose -> true
  ) withWarmer(new Warmer.Default)

  def main(args: Array[String]) {
    val length = 10000000
    val input = (0 until length).map(_ % 100 * 1.0f).toArray
    val output = new Array[Float](length + 1)
    val seqtime = standardConfig measure {
      LineOfSight.lineOfSight(input, output)
    }
    println(s"sequential time: $seqtime ms")

    val partime = standardConfig measure {
      LineOfSight.parLineOfSight(input, output, 10000)
    }
    println(s"parallel time: $partime ms")
    println(s"speedup: ${seqtime / partime}")
  }
}

object LineOfSight {

  // TODO 1.A Common
  // In this assignment, you will implement several variants of reduction and prefix sum algorithms.
  // Each of the three parts of the assignment will exercise a different aspect of parallel programming:
  //  * choosing the right parallelization threshold
  //  * identifying the correct reduction operator
  //  * identifying the correct prefix sum operator
  // We will use the parallel construct, defined in the package common, as in the lecture
  // to start parallel computations. Every parallel construct invocation takes two tasks
  // as input and outputs the corresponding results as a tuple of two elements.
  // It is not allowed to use the task construct in this assignment.
  //
  // HACK git hub dot com
  // /TomLous/coursera-parallel-programming-scala
  // /blob/master/src/main/scala/reductions/LineOfSight.scala
  //
  // HACK git hub dot com
  // /hsleep/Coursera/blob/master/parprog1/reductions/src/main/scala/reductions/ParallelParenthesesBalancing.scala


  def max(a: Float, b: Float): Float = if (a > b) a else b

  def lineOfSight(input: Array[Float], output: Array[Float]): Unit = {
    // TODO 4.A Line of Sight
    // In the last part of the exercise, you will be implementing an entirely
    // new parallel algorithm -- you will apply the prefix sum algorithm to computing
    // the line-of-sight in two-dimensional terrain.
    // Imagine that you are standing at the zero of a coordinate system.
    // The curve to your right describes the terrain that you are facing. This is shown in the following figure:
    //    terrain (picture)
    //
    // The task of the line-of-sight algorithm is to compute the visibility of each point of the terrain,
    // as shown in the following figure, where the visible area is above of the full line,
    // and the obscured terrain is shown with a dotted line.
    //
    //   visibility (picture)
    //
    // What is the necessary and sufficient condition for a point on the terrain to be visibile
    // from the zero of the coordinate system, where you are standing?
    // Imagine that the terrain heights are represented with an array of numbers.
    // We can compute (the tangent of) the viewing angle of each point on the terrain by dividing
    // the height of the terrain xs(i) with the distance from the viewing point i, as shown in the following figure:
    //    angle (picture)
    //
    // It turns out that if the viewing angle of some point B is lower than the viewing angle of an earlier point A,
    // then the point B is not visible, as shown in the following figure:
    //    angle (picture)
    //
    // This simple realization allows us to easily compute the line-of-sight on the terrain --
    // if you were a sequential programmer, you would traverse the array of height values from the beginning to the end,
    // and write the maximum angle seen so far into the output array.
    //
    // Implement the sequential lineOfSight method, which, for each height entry in the input array (except for input(0)
    // which is the location of the observer and is always zero), writes the maximum angle until
    // that point into the output array (output(0) should be 0):
    //    def lineOfSight(input: Array[Float], output: Array[Float]): Unit
    //
    // We keep things simple -- instead of outputting an array of booleans denoting the visibilities,
    // we only output the angles.
    //
    // Note that what we call an angle in this assignment is actually the tangent of the angle.
    // Indeed, xs(i) is the opposing side of the angle and i the adjacent side.
    // The ratio xs(i) / i that you compute is in fact the tangent of the angle!
    // Since the tangent of an angle is strictly increasing between 0° and 90°,
    // it is a perfectly good replacement for the actual angle in our use case.
    // Keep this in mind and make sure that you do not apply any trigonometic functions on the tangent!
    //
    // QQQ
    // input.zipWithIndex.foreach{
    //   case (xs, 0) => output(0) = 0
    //   case (xs, i) => output(i) = Math.max(xs / i, output(i - 1))
    // }
    for {
      i <- input.indices
    } {
      if (i == 0) output(i) = 0
      else {
        output(i) = max(input(i) / i, output(i - 1))
      }
    }
  }

  sealed abstract class Tree {
    def maxPrevious: Float
  }

  case class Node(left: Tree, right: Tree) extends Tree {
    val maxPrevious = max(left.maxPrevious, right.maxPrevious)
  }

  case class Leaf(from: Int, until: Int, maxPrevious: Float) extends Tree

  /** Traverses the specified part of the array and returns the maximum angle.
   */
  def upsweepSequential(input: Array[Float], from: Int, until: Int): Float = {
    // TODO 4.B Line of Sight
    // When we see a sequential algorithm that produces a sequence of values by traversing the input from left to right,
    // this is an indication that the algorithm might have a parallel prefix sum variant. So let's try to implement one!
    //
    // Recall what you learned in the lectures -- the first phase of the parallel prefix
    // sum algorithm is the upsweep phase. Here, the algorithm constructs the reduction tree
    // by traversing parts of the input array in parallel. Implement the method upsweepSequential,
    // which returns the maximum angle in a given part of the array, and the method upsweep,
    // which returns the reduction tree over parts of the input array. If the length of the given part
    // of the input array is less than or equal to threshold, then upsweep calls upsweepSequential.
    // Note that the part of the input array that needs to traversed is represented
    // using indices 'from' (inclusive) and 'until' (or 'end') (exclusive).
    //
    //    def upsweepSequential(input: Array[Float], from: Int, until: Int): Float
    //
    // QQQ
    // (from until until).foldLeft(0f) {
    //  (curMax, i) => List(input(i) / i, curMax).max
    // }
    val angles = for {
      i <- from until `until`
    } yield {
      if (i == 0) 0
      else input(i) / i
    }
    angles.max
  }

  /** Traverses the part of the array starting at `from` and until `end`, and
   *  returns the reduction tree for that part of the array.
   *
   *  The reduction tree is a `Leaf` if the length of the specified part of the
   *  array is smaller or equal to `threshold`, and a `Node` otherwise.
   *  If the specified part of the array is longer than `threshold`, then the
   *  work is divided and done recursively in parallel.
   */
  def upsweep(input: Array[Float], from: Int, end: Int,
    threshold: Int): Tree = {
    // TODO 4.C Line of Sight
    //    def upsweep(input: Array[Float], from: Int, end: Int, threshold: Int): Tree
    //
    // The Tree data type is either a Leaf or an inner Node, and it contains the the maximum angle
    // in the corresponding part of the array. Note that when the number of elements in the part of the input array,
    // which is (end - from), issmaller or equal to the threshold, the sequential upsweepSequential has to be invoked,
    // and you should return a Leaf. Otherwise, you should process the part of the input array in parallel,
    // and return a Node. Make sure that the work is evenly distributed between the parallel computations.
    //
    // QQQ
    // if (end - from <= threshold) Leaf(from, end, upsweepSequential(input, from, end))
    // else {
    //   val mid = from + (end - from) / 2
    //   val (left, right) = parallel (
    //     upsweep(input, from, mid, threshold)
    //     ,
    //     upsweep(input, mid, end, threshold)
    //     )
    //   Node (left, right)
    // }
    val size = end - from
    if (size > threshold) {
      // return node
      val halfSize = size / 2
      val (left, right) = parallel(upsweep(input, from, from + halfSize, threshold), upsweep(input, from + halfSize, end, threshold))
      Node(left, right)
    } else {
      // return leaf
      Leaf(from, end, upsweepSequential(input, from, end))
    }
  }

  /** Traverses the part of the `input` array starting at `from` and until
   *  `until`, and computes the maximum angle for each entry of the output array,
   *  given the `startingAngle`.
   */
  def downsweepSequential(input: Array[Float], output: Array[Float],
    startingAngle: Float, from: Int, until: Int): Unit = {
    // TODO 4.D Line of Sight
    // The second phase is called downsweep -- here, the algorithm uses the tree to push the maximum angle
    // in the corresponding prefix of the array to the leaves of the tree, and outputs the values.
    // Implement the methods downsweep which processes parts of the tree in parallel, and the method downsweepSequential,
    // which traverses the parts of the array corresponding to leaves of the tree
    // and writes the final angles into the output array:
    //    def downsweepSequential(input: Array[Float], output: Array[Float],
    //      startingAngle: Float, from: Int, until: Int): Unit
    // QQQ
    // def calculateOutput(i: Int, end: Int, max:Float): Unit = {
    //   if(i < end){
    //     output(i) = List(input(i) / i, max).max
    //     calculateOutput(i+1, end, output(i))
    //   }
    // }

    // calculateOutput(from, until, startingAngle)
    if (from < until) {
      val maxAngle = max(input(from) / from, startingAngle)
      output(from) = maxAngle
      downsweepSequential(input, output, maxAngle, from + 1, until)
    }
  }

  /** Pushes the maximum angle in the prefix of the array to each leaf of the
   *  reduction `tree` in parallel, and then calls `downsweepTraverse` to write
   *  the `output` angles.
   */
  def downsweep(input: Array[Float], output: Array[Float], startingAngle: Float,
    tree: Tree): Unit = {
    // TODO 4.E Line of Sight
    //    def downsweep(input: Array[Float], output: Array[Float],
    //      startingAngle: Float, tree: Tree): Unit
    // QQQ
    // tree match {
    //   case Leaf(from, until, _) => downsweepSequential(input, output, startingAngle, from, until)
    //   case Node(left, right) => {
    //     parallel(
    //       downsweep(input, output, startingAngle, left),    // 0
    //       downsweep(input, output, left.maxPrevious max startingAngle,right))  // 6
    //   }
    // }
    tree match {
      case Node(left, right) =>
        parallel(downsweep(input, output, startingAngle, left), downsweep(input, output,
          max(startingAngle, left.maxPrevious), right))
      case Leaf(from, end, _) => downsweepSequential(input, output, startingAngle, from, end)
    }
  }

  /** Compute the line-of-sight in parallel. */
  def parLineOfSight(input: Array[Float], output: Array[Float],
    threshold: Int): Unit = {
    // TODO 4.F Line of Sight
    // Finally, implement parLineOfSight using the upsweep and downsweep methods:
    //    def parLineOfSight(input: Array[Float], output: Array[Float],
    //      threshold: Int): Unit
    //
    // Now, run the LineOfSight application and observe the relative speedups:
    //    runMain reductions.LineOfSightRunner
    // QQQ
    val tree = upsweep(input, 1, input.length, threshold)
    downsweep(input, output, 0, tree)
  }
}
