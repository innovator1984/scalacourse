package funsets

import org.scalatest.FunSuite


import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
 * This class is a test suite for the methods in object FunSets. To run
 * the test suite, you can either:
 *  - run the "test" command in the SBT console
 *  - right-click the file in eclipse and chose "Run As" - "JUnit Test"
 */
@RunWith(classOf[JUnitRunner])
class FunSetSuite extends FunSuite {

  // TODO 1 : MAKE TESTS

  /**
   * Link to the scaladoc - very clear and detailed tutorial of FunSuite
   *
   * http://doc.scalatest.org/1.9.1/index.html#org.scalatest.FunSuite
   *
   * Operators
   *  - test
   *  - ignore
   *  - pending
   */

  /**
   * Tests are written using the "test" operator and the "assert" method.
   */
  // test("string take") {
  //   val message = "hello, world"
  //   assert(message.take(5) == "hello")
  // }

  /**
   * For ScalaTest tests, there exists a special equality operator "===" that
   * can be used inside "assert". If the assertion fails, the two values will
   * be printed in the error message. Otherwise, when using "==", the test
   * error message will only say "assertion failed", without showing the values.
   *
   * Try it out! Change the values so that the assertion fails, and look at the
   * error message.
   */
  // test("adding ints") {
  //   assert(1 + 2 === 3)
  // }


  import FunSets._

  // TODO 2: check this already implemented test
  // Therefore, we choose to represent a set by its characteristic function
  // and define a type alias for this representation:
  //       type Set = Int => Boolean
  // Using this representation, we define a function that tests for the presence of a value in a set:
  //       def contains(s: Set, elem: Int): Boolean = s(elem)

  test("contains is implemented") {
    assert(contains(x => true, 100))
  }

  // TODO 3: some implementations
  // Start by implementing basic functions on sets.
  // 1. Define a function singletonSet which creates a singleton set from one integer value:
  // the set represents the set of the one given element.
  // Now that we have a way to create singleton sets,
  // we want to define a function that allow us to build bigger sets from smaller ones.
  //
  // 2. Define the functions union,intersect, and diff, which takes two sets, and return,
  // respectively, their union, intersection and differences. diff(s, t)
  // returns a set which contains all the elements of the set s that are not in the set t.
  //
  // 3. Define the function filter which selects only the elements of a set
  // that are accepted by a given predicate p. The filtered elements are returned as a new set.

  // TODO EXTRA Queries and Transformations on Sets
  // This forall function has the following signature:
  //   def forall(s: Set, p: Int => Boolean): Boolean

  /**
   * When writing tests, one would often like to re-use certain values for multiple
   * tests. For instance, we would like to create an Int-set and have multiple test
   * about it.
   *
   * Instead of copy-pasting the code for creating the set into every test, we can
   * store it in the test class using a val:
   *
   *   val s1 = singletonSet(1)
   *
   * However, what happens if the method "singletonSet" has a bug and crashes? Then
   * the test methods are not even executed, because creating an instance of the
   * test class fails!
   *
   * Therefore, we put the shared values into a separate trait (traits are like
   * abstract classes), and create an instance inside each test method.
   *
   */

  trait TestSets {
    val s1 = singletonSet(1)
    val s2 = singletonSet(2)
    val s3 = singletonSet(3)
  }

  /**
   * This test is currently disabled (by using "ignore") because the method
   * "singletonSet" is not yet implemented and the test would fail.
   *
   * Once you finish your implementation of "singletonSet", exchange the
   * function "ignore" by "test".
   */
  test("singletonSet(1) contains 1") {

    /**
     * We create a new instance of the "TestSets" trait, this gives us access
     * to the values "s1" to "s3".
     */
    new TestSets {
      /**
       * The string argument of "assert" is a message that is printed in case
       * the test fails. This helps identifying which assertion failed.
       */
      assert(contains(s1, 1), "Singleton")
    }
  }

  test("union contains all elements of each set") {
    new TestSets {
      val s = union(s1, s2)
      assert(contains(s, 1), "Union 1")
      assert(contains(s, 2), "Union 2")
      assert(!contains(s, 3), "Union 3")
    }
  }

  // ----- MY TESTS -----

  test("singletonSet pos 01") {
    // TODO TEST STEP 1 POS 01
    // Define a function singletonSet which creates a singleton set from one integer value:
    // the set represents the set of the one given element.
    // Now that we have a way to create singleton sets,
    // we want to define a function that allow us to build bigger sets from smaller ones.
    val s1 = singletonSet(1)
    assert(contains(s1, 1))
  }

  test("singletonSet neg 01") {
    // TODO TEST STEP 1 NEG 01
    // Define a function singletonSet which creates a singleton set from one integer value:
    // the set represents the set of the one given element.
    // Now that we have a way to create singleton sets,
    // we want to define a function that allow us to build bigger sets from smaller ones.
    val s0 = singletonSet(0)
    assert(!contains(s0, 1))
  }

  test("union pos 01") {
    // TODO TEST STEP 2.A POS 01
    // Define the functions union,intersect, and diff, which takes two sets,
    // and return, respectively, their union, intersection and differences.
    // diff(s, t) returns a set which contains all the elements of the set s that are not in the set t.
    val s1 = singletonSet(1)
    val s2 = singletonSet(2)
    val s3 = union(s1, s2)
    assert(contains(s3, 1))
    assert(contains(s3, 2))
  }

  test("union neg 01") {
    // TODO TEST STEP 2.A NEG 01
    // Define the functions union,intersect, and diff, which takes two sets,
    // and return, respectively, their union, intersection and differences.
    // diff(s, t) returns a set which contains all the elements of the set s that are not in the set t.
    val s0 = singletonSet(0)
    val s2 = singletonSet(2)
    val s3 = union(s0, s2)
    assert(contains(s3, 0))
    assert(contains(s3, 2))
    assert(!contains(s3, 1))
  }

  test("intersect pos 01") {
    // TODO TEST STEP 2.B POS 01
    // Define the functions union,intersect, and diff, which takes two sets,
    // and return, respectively, their union, intersection and differences.
    // diff(s, t) returns a set which contains all the elements of the set s that are not in the set t.
    val s1 = singletonSet(1)
    val s2 = singletonSet(1)
    val s3 = intersect(s1, s2)
    assert(contains(s3, 1))
  }

  test("intersect neg 01") {
    // TODO TEST STEP 2.B NEG 01
    // Define the functions union,intersect, and diff, which takes two sets,
    // and return, respectively, their union, intersection and differences.
    // diff(s, t) returns a set which contains all the elements of the set s that are not in the set t.
    val s1 = singletonSet(1)
    val s2 = singletonSet(2)
    val s3 = intersect(s1, s2)
    assert(!contains(s3, 1))
    assert(!contains(s3, 2))
  }

  test("diff pos 01") {
    // TODO TEST STEP 2.C POS 01
    // Define the functions union,intersect, and diff, which takes two sets,
    // and return, respectively, their union, intersection and differences.
    // diff(s, t) returns a set which contains all the elements of the set s that are not in the set t.
    val s1 = singletonSet(1)
    val s2 = singletonSet(2)
    val s3 = diff(s1, s2)
    assert(contains(s3, 1))
    assert(contains(s3, 2))
  }

  test("diff neg 01") {
    // TODO TEST STEP 2.C NEG 01
    // Define the functions union,intersect, and diff, which takes two sets,
    // and return, respectively, their union, intersection and differences.
    // diff(s, t) returns a set which contains all the elements of the set s that are not in the set t.
    val s1 = singletonSet(1)
    val s2 = singletonSet(1)
    val s3 = diff(s1, s2)
    assert(!contains(s3, 1))
  }

  test("filter pos 01") {
    // TODO TEST STEP 3 POS 01
    // Define the function filter which selects only the elements of a set that are accepted by a given predicate p.
    // The filtered elements are returned as a new set.
    val s1 = singletonSet(1)
    val s2 = singletonSet(2)
    val s3 = union(s1, s2)
    val s0 = filter(s3, x => x != 1)
    assert(contains(s0, 2))
  }

  test("filter neg 01") {
    // TODO TEST STEP 3 NEG 01
    // Define the function filter which selects only the elements of a set that are accepted by a given predicate p.
    // The filtered elements are returned as a new set.
    val s1 = singletonSet(1)
    val s2 = singletonSet(2)
    val s3 = union(s1, s2)
    val s0 = filter(s3, x => x != 1)
    assert(!contains(s0, 1))
  }

  test("forall pos 01") {
    // TODO TEST EXTRA.A POS 01
    // The first function tests whether a given predicate is true for all elements of the set.
    // 1. Implement forall using linear recursion. For this, use a helper function nested inforall.
    val s1 = singletonSet(-bound)
    val s2 = singletonSet(bound)
    val s3 = union(singletonSet(0), union(s1, s2))
    val s0 = filter(s3, x => x != -bound)
    assert(contains(s0, bound))
    assert(forall(s0, x => x != -bound))
  }

  test("forall neg 01") {
    // TODO TEST EXTRA.A NEG 01
    // The first function tests whether a given predicate is true for all elements of the set.
    // 1. Implement forall using linear recursion. For this, use a helper function nested inforall.
    val s1 = singletonSet(-bound)
    val s2 = singletonSet(bound)
    val s3 = union(singletonSet(0), union(s1, s2))
    val s0 = filter(s3, x => x != bound)
    assert(contains(s0, -bound))
    assert(!forall(s0, x => x == bound))
  }

  test("exists pos 01") {
    // TODO TEST EXTRA.B POS 01
    // The first function tests whether a given predicate is true for all elements of the set.
    // 2. Using forall, implement a function exists which tests whether a set contains at least one element
    // for which the given predicate is true. Note that the functions forall and exists behave like the universal
    // and existential quantifiers of first-order logic.
    val s1 = singletonSet(-bound)
    val s2 = singletonSet(bound)
    val s3 = union(singletonSet(0), union(s1, s2))
    val s0 = filter(s3, x => x != -bound)
    assert(contains(s0, bound))
    assert(exists(s0, x => x != -bound))
  }

  test("exists neg 01") {
    // TODO TEST EXTRA.B NEG 01
    // The first function tests whether a given predicate is true for all elements of the set.
    // 2. Using forall, implement a function exists which tests whether a set contains at least one element
    // for which the given predicate is true. Note that the functions forall and exists behave like the universal
    // and existential quantifiers of first-order logic.
    val s1 = singletonSet(-bound)
    val s2 = singletonSet(bound)
    val s3 = union(singletonSet(0), union(s1, s2))
    val s0 = filter(s3, x => x != bound)
    assert(contains(s0, -bound))
    assert(!exists(s0, x => x == bound))
  }

  test("map pos 01") {
    // TODO TEST EXTRA.C POS 01
    // The first function tests whether a given predicate is true for all elements of the set.
    // 3. Finally, write a function map which transforms a given set into another one by applying
    // to each of its elements the given function.
    val s1 = singletonSet(0)
    val s2 = singletonSet(bound)
    val s3 = union(s1, s2)
    val s0 = map(s3, x => -x)
    assert(contains(s0, -bound))
    assert(exists(s0, x => x == -bound))
  }

  test("map neg 01") {
    // TODO TEST EXTRA.C NEG 01
    // The first function tests whether a given predicate is true for all elements of the set.
    // 3. Finally, write a function map which transforms a given set into another one by applying
    // to each of its elements the given function.
    val s1 = singletonSet(0)
    val s2 = singletonSet(bound)
    val s3 = union(s1, s2)
    val s0 = map(s3, x => -x)
    assert(contains(s0, -bound))
    assert(!exists(s0, x => x == bound))
  }

}
