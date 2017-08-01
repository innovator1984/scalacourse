package reductions

import java.util.concurrent._
import scala.collection._
import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import common._
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory

@RunWith(classOf[JUnitRunner]) 
class LineOfSightSuite extends FunSuite {
  import LineOfSight._
  test("lineOfSight should correctly handle an array of size 4") {
    val output = new Array[Float](4)
    lineOfSight(Array[Float](0f, 1f, 8f, 9f), output)
    assert(output.toList == List(0f, 1f, 4f, 4f))
  }


  test("upsweepSequential should correctly handle the chunk 1 until 4 of an array of 4 elements") {
    val res = upsweepSequential(Array[Float](0f, 1f, 8f, 9f), 1, 4)
    assert(res == 4f)
  }


  test("downsweepSequential should correctly handle a 4 element array when the starting angle is zero") {
    val output = new Array[Float](4)
    downsweepSequential(Array[Float](0f, 1f, 8f, 9f), output, 0f, 1, 4)
    assert(output.toList == List(0f, 1f, 4f, 4f))
  }

  // ===== MY TESTS N_1 =====
  test("main pos 01") {
    // TODO 1.A Common
    assert(true)
  }

  test("main neg 01") {
    // TODO 1.A Common
    assert(true)
  }

  test("lineOfSight pos 01") {
    // TODO 4.A Line of Sight
    assert(true)
  }

  test("lineOfSight neg 01") {
    // TODO 4.A Line of Sight
    assert(true)
  }

  test("upsweepSequential pos 01") {
    // TODO 4.B Line of Sight
    assert(true)
  }

  test("upsweepSequential neg 01") {
    // TODO 4.B Line of Sight
    assert(true)
  }

  test("upsweep pos 01") {
    // TODO 4.C Line of Sight
    assert(true)
  }

  test("upsweep neg 01") {
    // TODO 4.C Line of Sight
    assert(true)
  }

  test("downsweepSequential pos 01") {
    // TODO 4.D Line of Sight
    assert(true)
  }

  test("downsweepSequential neg 01") {
    // TODO 4.D Line of Sight
    assert(true)
  }

  test("downsweep pos 01") {
    // TODO 4.E Line of Sight
    assert(true)
  }

  test("downsweep neg 01") {
    // TODO 4.E Line of Sight
    assert(true)
  }

  test("parLineOfSight pos 01") {
    // TODO 4.F Line of Sight
    assert(true)
  }

  test("parLineOfSight neg 01") {
    // TODO 4.F Line of Sight
    assert(true)
  }
}

