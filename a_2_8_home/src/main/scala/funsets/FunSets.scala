package funsets

/**
 * 2. Purely Functional Sets.
 */
object FunSets {
  // TODO 1 WRITE YOUR TESTS
  /**
   * We represent a set by its characteristic function, i.e.
   * its `contains` predicate.
   */
  type Set = Int => Boolean

  /**
   * Indicates whether a set contains a given element.
   */
  def contains(s: Set, elem: Int): Boolean = {
    // TODO 2 FIX MISTAKE ON GIVEN TEST assert(contains(x => true, 100))
    // test("union contains all elements of each set") {
    // new TestSets {
     //        val s = union(s1, s2)
     //   assert(contains(s, 1), "Union 1")
     //   assert(contains(s, 2), "Union 2")
     //   assert(!contains(s, 3), "Union 3")
     // }
    // }

    // s(elem)

    s(elem)
  }

  /**
   * Returns the set of the one given element.
   */
    def singletonSet(elem: Int): Set = {
      // TODO 3 STEP 1
      // Define a function singletonSet which creates a singleton set from one integer value:
      // the set represents the set of the one given element.
      // Now that we have a way to create singleton sets,
      // we want to define a function that allow us to build bigger sets from smaller ones.
      def func(x: Int): Boolean = {
        if(x == elem) true else false
      }
      func
    }

  /**
   * Returns the union of the two given sets,
   * the sets of all elements that are in either `s` or `t`.
   */
    def union(s: Set, t: Set): Set = {
      // TODO 3 STEP 2.A
      // Define the functions union,intersect, and diff, which takes two sets,
      // and return, respectively, their union, intersection and differences.
      // diff(s, t) returns a set which contains all the elements of the set s that are not in the set t.
      x => contains(s, x) || contains(t, x)
    }

  /**
   * Returns the intersection of the two given sets,
   * the set of all elements that are both in `s` and `t`.
   */
    def intersect(s: Set, t: Set): Set = {
      // TODO 3 STEP 2.B
      // Define the functions union,intersect, and diff, which takes two sets,
      // and return, respectively, their union, intersection and differences.
      // diff(s, t) returns a set which contains all the elements of the set s that are not in the set t.
      x => contains(s, x) && contains(t, x)
    }
  
  /**
   * Returns the difference of the two given sets,
   * the set of all elements of `s` that are not in `t`.
   */
    def diff(s: Set, t: Set): Set = {
      // TODO 3 STEP 2.C
      // Define the functions union,intersect, and diff, which takes two sets,
      // and return, respectively, their union, intersection and differences.
      // diff(s, t) returns a set which contains all the elements of the set s that are not in the set t.
      x => contains(s, x) && !contains(t, x) || !contains(s, x) && contains(t, x)
    }

  /**
   * Returns the subset of `s` for which `p` holds.
   */
    def filter(s: Set, p: Int => Boolean): Set =  {
      // TODO 3 STEP 3
      // Define the function filter which selects only the elements of a set that are accepted by a given predicate p.
      // The filtered elements are returned as a new set.
      x => p(x) && s(x)
    }

  /**
   * The bounds for `forall` and `exists` are +/- 1000.
   */
  val bound = 1000

  /**
   * Returns whether all bounded integers within `s` satisfy `p`.
   */
    def forall(s: Set, p: Int => Boolean): Boolean = {
      // TODO 3 EXTRA.A Queries and Transformations on Sets
      // The first function tests whether a given predicate is true for all elements of the set.
      // 1. Implement forall using linear recursion. For this, use a helper function nested in forall.

      // def iter(a: Int): Boolean = {
      //  if (???) ???
      //  else if (???) ???
      //  else iter(???)
      // }
      // iter(???)

      def iter(a: Int): Boolean = {
        if (contains(s,a) && !contains(filter(s, p),a)) false
        else if (a > bound) true
        else iter(a + 1)
      }
      iter(-bound)
  }

  /**
   * Returns whether there exists a bounded integer within `s`
   * that satisfies `p`.
   */
    def exists(s: Set, p: Int => Boolean): Boolean = {
      // TODO 3 EXTRA.B Queries and Transformations on Sets
      // The first function tests whether a given predicate is true for all elements of the set.
      // 2. Using forall, implement a function exists which tests whether a set contains at least one element
      // for which the given predicate is true. Note that the functions forall and exists behave like the universal
      // and existential quantifiers of first-order logic.
      !forall(filter(s, p), x => false)
    }

  /**
   * Returns a set transformed by applying `f` to each element of `s`.
   */
    def map(s: Set, f: Int => Int): Set = {
      // TODO 3 EXTRA.C Queries and Transformations on Sets
      // The first function tests whether a given predicate is true for all elements of the set.
      // 3. Finally, write a function map which transforms a given set into another one by applying
      // to each of its elements the given function.
      def func(x: Int): Boolean = {
        s(f(x))
      }
      func
    }

  /**
   * Displays the contents of a set
   */
  def toString(s: Set): String = {
    val xs = for (i <- -bound to bound if contains(s, i)) yield i
    xs.mkString("{", ",", "}")
  }

  /**
   * Prints the contents of a set on the console.
   */
  def printSet(s: Set) {
    println(toString(s))
  }
}
