package reductions

import java.util.concurrent._
import scala.collection._
import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import common._

import ParallelParenthesesBalancing._

@RunWith(classOf[JUnitRunner])
class ParallelParenthesesBalancingSuite extends FunSuite {

  test("balance should work for empty string") {
    def check(input: String, expected: Boolean) =
      assert(balance(input.toArray) == expected,
        s"balance($input) should be $expected")

    check("", true)
  }

  test("balance should work for string of length 1") {
    def check(input: String, expected: Boolean) =
      assert(balance(input.toArray) == expected,
        s"balance($input) should be $expected")

    check("(", false)
    check(")", false)
    check(".", true)
  }

  test("balance should work for string of length 2") {
    def check(input: String, expected: Boolean) =
      assert(balance(input.toArray) == expected,
        s"balance($input) should be $expected")

    check("()", true)
    check(")(", false)
    check("((", false)
    check("))", false)
    check(".)", false)
    check(".(", false)
    check("(.", false)
    check(").", false)
  }

  // ===== MY TESTS N_3 =====
  test("balance pos 01") {
    // TODO 3.A Parallel Parentheses Balancing
    assert(true)
  }

  test("balance neg 01") {
    // TODO 3.A Parallel Parentheses Balancing
    assert(true)
  }

  test("parBalance pos 01") {
    // TODO 3.B Parallel Parentheses Balancing
    assert(true)
  }

  test("parBalance neg 01") {
    // TODO 3.B Parallel Parentheses Balancing
    assert(true)
  }

  test("traverse pos 01") {
    // TODO 3.C Parallel Parentheses Balancing
    assert(true)
  }

  test("traverse neg 01") {
    // TODO 3.C Parallel Parentheses Balancing
    assert(true)
  }

  test("reduce pos 01") {
    // TODO 3.D Parallel Parentheses Balancing
    assert(true)
  }

  test("reduce neg 01") {
    // TODO 3.D Parallel Parentheses Balancing
    assert(true)
  }

}