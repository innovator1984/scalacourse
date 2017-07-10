package objsets

import org.scalatest.FunSuite


import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class TweetSetSuite extends FunSuite {
  trait TestSets {
    val set1 = new Empty
    val set2 = set1.incl(new Tweet("a", "a body", 20))
    val set3 = set2.incl(new Tweet("b", "b body", 20))
    val c = new Tweet("c", "c body", 7)
    val d = new Tweet("d", "d body", 9)
    val set4c = set3.incl(c)
    val set4d = set3.incl(d)
    val set5 = set4c.incl(d)
  }

  def asSet(tweets: TweetSet): Set[Tweet] = {
    var res = Set[Tweet]()
    tweets.foreach(res += _)
    res
  }

  def size(set: TweetSet): Int = asSet(set).size

  test("filter: on empty set") {
    new TestSets {
      assert(size(set1.filter(tw => tw.user == "a")) === 0)
    }
  }

  test("filter: a on set5") {
    new TestSets {
      assert(size(set5.filter(tw => tw.user == "a")) === 1)
    }
  }

  test("filter: 20 on set5") {
    new TestSets {
      assert(size(set5.filter(tw => tw.retweets == 20)) === 2)
    }
  }

  test("union: set4c and set4d") {
    new TestSets {
      assert(size(set4c.union(set4d)) === 4)
    }
  }

  test("union: with empty set (1)") {
    new TestSets {
      assert(size(set5.union(set1)) === 4)
    }
  }

  test("union: with empty set (2)") {
    new TestSets {
      assert(size(set1.union(set5)) === 4)
    }
  }

  test("descending: set5") {
    new TestSets {
      val trends = set5.descendingByRetweet
      assert(!trends.isEmpty)
      assert(trends.head.user == "a" || trends.head.user == "b")
    }
  }

  // ----- MY TESTS -----

  test("Empty.contains pos 01") {
    // TODO 1.A first study the already implemented methods contains and incl for inspiration.
    val s0 = new Empty
    assert(size(s0) === 0)
  }

  test("Empty.contains neg 01") {
    // TODO 1.A first study the already implemented methods contains and incl for inspiration.
    val s0 = new Empty
    assert(!s0.contains(new Tweet("a", "a body", 20)))
  }

  test("Empty.incl pos 01") {
    // TODO 1.B first study the already implemented methods contains and incl for inspiration.
    val s0 = new Empty
    val t1 = new Tweet("a", "a body", 20)
    val s1 = s0.incl(t1)
    assert(size(s1) === 1)
  }

  test("Empty.incl neg 01") {
    // TODO 1.B first study the already implemented methods contains and incl for inspiration.
    val s0 = new Empty
    val t1 = new Tweet("a", "a body", 20)
    val s1 = s0.incl(t1)
    assert(!s1.contains(new Tweet("b", "b body", 30)))
  }

  test("Empty.remove pos 01") {
    // TODO 1.C first study the already implemented methods contains and incl for inspiration.
    val s0 = new Empty
    val t1 = new Tweet("a", "a body", 20)
    val s1 = s0.incl(t1)
    val s2 = s1.remove(t1)
    assert(size(s2) === 0)
  }

  test("Empty.remove neg 01") {
    // TODO 1.C first study the already implemented methods contains and incl for inspiration.
    val s0 = new Empty
    val t1 = new Tweet("a", "a body", 20)
    val s1 = s0.incl(t1)
    val s2 = s1.remove(t1)
    assert(!s2.contains(t1))
  }

  test("Empty.foreach pos 01") {
    // TODO 1.D first study the already implemented methods contains and incl for inspiration.
    def func(t: Tweet): Unit = {
      ()
    }
    val s0 = new Empty
    s0.foreach(func)
    assert(size(s0) === 0)
  }

  test("Empty.foreach neg 01") {
    // TODO 1.D first study the already implemented methods contains and incl for inspiration.
    def func(t: Tweet): Unit = {
      ()
    }
    val s0 = new Empty
    s0.foreach(func)
    assert(!s0.contains(new Tweet("a", "a body", 20)))
  }

  test("NonEmpty.contains pos 01") {
    // TODO 1.E first study the already implemented methods contains and incl for inspiration.
    val s0 = new NonEmpty(new Tweet("a", "a body", 20), new Empty, new Empty)
    assert(s0.contains(new Tweet("a", "a body", 20)))
  }

  test("NonEmpty.contains neg 01") {
    // TODO 1.E first study the already implemented methods contains and incl for inspiration.
    val s0 = new NonEmpty(new Tweet("a", "a body", 20), new Empty, new Empty)
    assert(!s0.contains(new Tweet("b", "b body", 30)))
  }

  test("NonEmpty.incl pos 01") {
    // TODO 1.F first study the already implemented methods contains and incl for inspiration.
    val s0 = new NonEmpty(new Tweet("a", "a body", 20), new Empty, new Empty)
    val t1 = new Tweet("a", "a body", 20)
    val s1 = s0.incl(t1)
    assert(size(s1) === 1)
  }

  test("NonEmpty.incl neg 01") {
    // TODO 1.F first study the already implemented methods contains and incl for inspiration.
    val s0 = new NonEmpty(new Tweet("a", "a body", 20), new Empty, new Empty)
    val t1 = new Tweet("a", "a body", 20)
    val s1 = s0.incl(t1)
    assert(!s1.contains(new Tweet("b", "b body", 30)))
  }

  test("NonEmpty.remove pos 01") {
    // TODO 1.G first study the already implemented methods contains and incl for inspiration.
    val s0 = new NonEmpty(new Tweet("a", "a body", 20), new Empty, new Empty)
    val t1 = new Tweet("a", "a body", 20)
    val s1 = s0.incl(t1)
    val s2 = s1.remove(t1)
    assert(size(s2) === 0)
  }

  test("NonEmpty.remove neg 01") {
    // TODO 1.G first study the already implemented methods contains and incl for inspiration.
    val s0 = new NonEmpty(new Tweet("a", "a body", 20), new Empty, new Empty)
    val t1 = new Tweet("a", "a body", 20)
    val s1 = s0.incl(t1)
    val s2 = s1.remove(t1)
    assert(!s2.contains(t1))
  }

  test("NonEmpty.foreach pos 01") {
    // TODO 1.H first study the already implemented methods contains and incl for inspiration.
    def func(t: Tweet): Unit = {
      ()
    }
    val s0 = new NonEmpty(new Tweet("a", "a body", 20), new Empty, new Empty)
    s0.foreach(func)
    assert(size(s0) === 1)
  }

  test("NonEmpty.foreach neg 01") {
    // TODO 1.H first study the already implemented methods contains and incl for inspiration.
    def func(t: Tweet): Unit = {
      ()
    }
    val s0 = new NonEmpty(new Tweet("b", "b body", 30), new Empty, new Empty)
    s0.foreach(func)
    assert(!s0.contains(new Tweet("a", "a body", 20)))
  }

  test("TweetList.foreach pos 01") {
    // TODO 1.I first study the already implemented methods contains and incl for inspiration.
    def func(t: Tweet): Unit = {
      ()
    }
    val v0: TweetList = Nil
    v0.foreach(func)
    assert(v0.isEmpty)
  }

  test("TweetList.foreach neg 01") {
    // TODO 1.I first study the already implemented methods contains and incl for inspiration.
    def func(t: Tweet): Unit = {
      ()
    }
    val v0: TweetList = Nil
    v0.foreach(func)
    assert(true)
  }

  test("TweetList.filter pos 01") {
    // TODO 2.A Implement filtering on tweet sets. Complete the stubs for the methods filter and filterAcc.
    val s0 = new NonEmpty(new Tweet("b", "b body", 30), new Empty, new Empty)
    val s1 = s0.incl(new Tweet("a", "a body", 20))
    assert(size(s1.filter(tw => tw.user == "a")) === 1)
  }

  test("TweetSet.filter neg 01") {
    // TODO 2.A Implement filtering on tweet sets. Complete the stubs for the methods filter and filterAcc.
    val s0 = new Empty
    val s1 = s0.filter(tw => tw.user == "a")
    assert(!s1.contains(new Tweet("a", "a body", 20)))
  }

  test("Empty.filterAcc pos 01") {
    // TODO 2.B Implement filtering on tweet sets. Complete the stubs for the methods filter and filterAcc.
    val s0 = new Empty
    val s1 = s0.filterAcc(tw => tw.user == "a", new Empty)
    assert(size(s1) === 0)
  }

  test("Empty.filterAcc neg 01") {
    // TODO 2.B Implement filtering on tweet sets. Complete the stubs for the methods filter and filterAcc.
    val s0 = new Empty
    val s1 = s0.filterAcc(tw => tw.user == "a", new Empty)
    assert(!s1.contains(new Tweet("a", "a body", 20)))
  }

  test("NonEmpty.filterAcc pos 01") { // FIXME
    // TODO 2.C Implement filtering on tweet sets. Complete the stubs for the methods filter and filterAcc.
    val s0 = new NonEmpty(new Tweet("b", "b body", 30), new Empty, new Empty)
    val s1 = s0.incl(new Tweet("a", "a body", 20))
    assert(size(s1.filterAcc(tw => tw.user == "a", new Empty)) === 1)
  }

  test("NonEmpty.filterAcc neg 01") {
    // TODO 2.C Implement filtering on tweet sets. Complete the stubs for the methods filter and filterAcc.
    val s0 = new NonEmpty(new Tweet("b", "b body", 30), new Empty, new Empty)
    val s1 = s0.incl(new Tweet("a", "a body", 20))
    val s2 = s1.filterAcc(tw => tw.user == "a", new Empty)
    assert(!s2.contains(new Tweet("b", "b body", 30)))
  }

  test("TweetSet.union pos 01") {
    // TODO 3.A Implement union on tweet sets. Complete the stub for the method union.
    val s0 = new NonEmpty(new Tweet("a", "a body", 20), new Empty, new Empty)
    val s1 = new NonEmpty(new Tweet("b", "b body", 30), new Empty, new Empty)
    val s2 = s0.union(s1)
    assert(size(s2) == 2)
  }

  test("TweetSet.union neg 01") {
    // TODO 3.A Implement union on tweet sets. Complete the stub for the method union.
    val s0 = new NonEmpty(new Tweet("a", "a body", 20), new Empty, new Empty)
    val s1 = new NonEmpty(new Tweet("a", "a body", 20), new Empty, new Empty)
    val s2 = s0.union(s1)
    assert(size(s2) != 2)
  }

  test("Empty.unionUnk pos 01") {
    // TODO 3.B Implement union on tweet sets. Complete the stub for the method union.
    val s0 = new Empty
    val s1 = new Empty
    val s2 = s0.union(s1)
    assert(size(s2) == 0)
  }

  test("Empty.unionUnk neg 01") {
    // TODO 3.B Implement union on tweet sets. Complete the stub for the method union.
    val s0 = new Empty
    val s1 = new NonEmpty(new Tweet("a", "a body", 20), new Empty, new Empty)
    val s2 = s0.union(s1)
    assert(size(s2) != 0)
  }

  test("NonEmpty.unionUnk pos 01") {
    // TODO 3.C Implement union on tweet sets. Complete the stub for the method union.
    val s0 = new NonEmpty(new Tweet("a", "a body", 20), new Empty, new Empty)
    val s1 = new NonEmpty(new Tweet("a", "a body", 20), new Empty, new Empty)
    val s2 = s0.union(s1)
    assert(size(s2) == 1)
  }

  test("NonEmpty.unionUnk neg 01") {
    // TODO 3.C Implement union on tweet sets. Complete the stub for the method union.
    val s0 = new NonEmpty(new Tweet("a", "a body", 20), new Empty, new Empty)
    val s1 = new Empty
    val s2 = s0.union(s1)
    assert(size(s2) != 0)
  }

  test("TweetSet.descendingByRetweet pos 01") {
    // TODO 4.A Sorting Tweets by Their Influence
    val s0 = new NonEmpty(new Tweet("a", "a body", 20), new Empty, new Empty)
    val s1 = new NonEmpty(new Tweet("b", "b body", 30), new Empty, new Empty)
    val s2 = s0.union(s1)
    val res = s2.descendingByRetweet
    assert(!res.isEmpty)
    assert(res.head.user == "b")
    assert(!res.tail.isEmpty)
    assert(res.tail.head.user == "a")
  }

  test("TweetSet.descendingByRetweet neg 01") {
    // TODO 4.A Sorting Tweets by Their Influence
    val s0 = new NonEmpty(new Tweet("a", "a body", 20), new Empty, new Empty)
    val s1 = new NonEmpty(new Tweet("b", "b body", 30), new Empty, new Empty)
    val s2 = s0.union(s1)
    val res = s2.descendingByRetweet
    assert(!res.isEmpty)
    assert(res.head.user != "a")
    assert(!res.tail.isEmpty)
    assert(res.tail.head.user != "b")
  }

  test("TweetSet.mostRetweeted pos 01") {
    // TODO 5.A Sorting Tweets by Their Influence
    val s0 = new NonEmpty(new Tweet("a", "a body", 20), new Empty, new Empty)
    val s1 = new NonEmpty(new Tweet("b", "b body", 30), new Empty, new Empty)
    val s2 = s0.union(s1)
    assert(s2.mostRetweeted.user == "b")
  }

  test("TweetSet.mostRetweeted neg 01") {
    // TODO 5.A Sorting Tweets by Their Influence
    val s0 = new NonEmpty(new Tweet("a", "a body", 20), new Empty, new Empty)
    val s1 = new NonEmpty(new Tweet("b", "b body", 30), new Empty, new Empty)
    val s2 = s0.union(s1)
    assert(s2.mostRetweeted.user != "a")
  }

  test("GoogleVsApple.new googleTweets pos 01") {
    // TODO 6.A Tying everything together
    assert(true)
  }

  test("GoogleVsApple.new googleTweets neg 01") {
    // TODO 6.A Tying everything together
    assert(true)
  }

  test("GoogleVsApple.new appleTweets pos 01") {
    // TODO 6.B Tying everything together
    assert(true)
  }

  test("GoogleVsApple.new appleTweets neg 01") {
    // TODO 6.B Tying everything together
    assert(true)
  }

  test("GoogleVsApple.new trending pos 01") {
    // TODO 6.C Tying everything together
    assert(true)
  }

  test("GoogleVsApple.new trending neg 01") {
    // TODO 6.C Tying everything together
    assert(true)
  }
  }
