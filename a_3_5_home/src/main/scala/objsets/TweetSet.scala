package objsets

import TweetReader._

/**
 * A class to represent tweets.
 */
class Tweet(val user: String, val text: String, val retweets: Int) {
  override def toString: String =
    "User: " + user + "\n" +
    "Text: " + text + " [" + retweets + "]"
}

/**
 * This represents a set of objects of type `Tweet` in the form of a binary search
 * tree. Every branch in the tree has two children (two `TweetSet`s). There is an
 * invariant which always holds: for every branch `b`, all elements in the left
 * subtree are smaller than the tweet at `b`. The elements in the right subtree are
 * larger.
 *
 * Note that the above structure requires us to be able to compare two tweets (we
 * need to be able to say which of two tweets is larger, or if they are equal). In
 * this implementation, the equality / order of tweets is based on the tweet's text
 * (see `def incl`). Hence, a `TweetSet` could not contain two tweets with the same
 * text from different users.
 *
 *
 * The advantage of representing sets as binary search trees is that the elements
 * of the set can be found quickly. If you want to learn more you can take a look
 * at the Wikipedia page [1], but this is not necessary in order to solve this
 * assignment.
 *
 * [1] http://en.wikipedia.org/wiki/Binary_search_tree
 */
abstract class TweetSet {

  /**
   * This method takes a predicate and returns a subset of all the elements
   * in the original set for which the predicate is true.
   *
   * Question: Can we implment this method here, or should it remain abstract
   * and be implemented in the subclasses?
   */
    def filter(p: Tweet => Boolean): TweetSet = {
      // TODO 2.A Implement filtering on tweet sets. Complete the stubs for the methods filter and filterAcc.
      // For example, the following call:
      //   tweets.filter(tweet => tweet.retweets > 10)
      // applied to a set tweets of two tweets, say, where the first tweet was not retweeted
      // and the second tweet was retweeted 20 times should return a set containing only the second tweet.
      filterAcc(p, new Empty)
    }

  /**
   * This is a helper method for `filter` that propagetes the accumulated tweets.
   */
  def filterAcc(p: Tweet => Boolean, acc: TweetSet): TweetSet

  /**
   * Returns a new `TweetSet` that is the union of `TweetSet`s `this` and `that`.
   *
   * Question: Should we implement this method here, or should it remain abstract
   * and be implemented in the subclasses?
   */
  def union(that: TweetSet): TweetSet

  /**
   * Returns the tweet from this set which has the greatest retweet count.
   *
   * Calling `mostRetweeted` on an empty set should throw an exception of
   * type `java.util.NoSuchElementException`.
   *
   * Question: Should we implment this method here, or should it remain abstract
   * and be implemented in the subclasses?
   */
    def mostRetweeted: Tweet = {
      // TODO 5.A Sorting Tweets by Their Influence
      // Hint: start by implementing the method mostRetweeted which returns the most popular tweet of a TweetSet.
      descendingByRetweet.head
    }

  /**
   * Returns a list containing all tweets of this set, sorted by retweet count
   * in descending order. In other words, the head of the resulting list should
   * have the highest retweet count.
   *
   * Hint: the method `remove` on TweetSet will be very useful.
   * Question: Should we implment this method here, or should it remain abstract
   * and be implemented in the subclasses?
   */
    def descendingByRetweet: TweetList
  
  /**
   * The following methods are already implemented
   */

  /**
   * Returns a new `TweetSet` which contains all elements of this set, and the
   * the new element `tweet` in case it does not already exist in this set.
   *
   * If `this.contains(tweet)`, the current set is returned.
   */
  def incl(tweet: Tweet): TweetSet

  /**
   * Returns a new `TweetSet` which excludes `tweet`.
   */
  def remove(tweet: Tweet): TweetSet

  /**
   * Tests if `tweet` exists in this `TweetSet`.
   */
  def contains(tweet: Tweet): Boolean

  /**
   * This method takes a function and applies it to every element in the set.
   */
  def foreach(f: Tweet => Unit): Unit
}

class Empty extends TweetSet {
  def filterAcc(p: Tweet => Boolean, acc: TweetSet): TweetSet = {
    // TODO 2.B Implement filtering on tweet sets. Complete the stubs for the methods filter and filterAcc.
    // For example, the following call:
    //   tweets.filter(tweet => tweet.retweets > 10)
    // applied to a set tweets of two tweets, say, where the first tweet was not retweeted
    // and the second tweet was retweeted 20 times should return a set containing only the second tweet.
    this
  }

  def union(that: TweetSet): TweetSet = {
    // TODO 3.B Implement union on tweet sets. Complete the stub for the method union.
    // The method union takes another set that, and computes a new set which is the union of this and that,
    // i.e. a set that contains exactly the elements that are either in this or in that, or in both.

    // in this exercise it is your task to find out in which class(es)
    // to define the union method (should it be abstract in class TweetSet?).
    that
  }

  def descendingByRetweet: TweetList = {
    // TODO 4.A Sorting Tweets by Their Influence
    // The idea is to start with the empty list Nil (containing no tweets),
    // and to find the tweet with the most retweets in the input TweetSet.
    // This tweet is removed from the TweetSet
    // (that is, we obtain a new TweetSet that has all the tweets
    // of the original set except for the tweet that was “removed”;
    // this immutable set operation, remove, is already implemented for you),
    // and added to the result list by creating a new Cons.
    // After that, the process repeats itself, but now we are searching through a TweetSet with one less tweet.

    // Hint: start by implementing the method mostRetweeted which returns the most popular tweet of a TweetSet.
    Nil
  }

  /**
   * The following methods are already implemented
   */

  def contains(tweet: Tweet): Boolean = {
    // TODO 1.A first study the already implemented methods contains and incl for inspiration.
    false
  }

  def incl(tweet: Tweet): TweetSet = {
    // TODO 1.B first study the already implemented methods contains and incl for inspiration.
    new NonEmpty(tweet, new Empty, new Empty)
  }

  def remove(tweet: Tweet): TweetSet = {
    // TODO 1.C first study the already implemented methods contains and incl for inspiration.
    this
  }

  def foreach(f: Tweet => Unit): Unit = {
    // TODO 1.D first study the already implemented methods contains and incl for inspiration.
    ()
  }
}

class NonEmpty(elem: Tweet, left: TweetSet, right: TweetSet) extends TweetSet {

  def filterAcc(p: Tweet => Boolean, acc: TweetSet): TweetSet = {
    // TODO 2.C Implement filtering on tweet sets. Complete the stubs for the methods filter and filterAcc.
    // For example, the following call:
    //   tweets.filter(tweet => tweet.retweets > 10)
    // applied to a set tweets of two tweets, say, where the first tweet was not retweeted
    // and the second tweet was retweeted 20 times should return a set containing only the second tweet.
    val found = acc.contains(elem)
    val needed = if(found) true else p(elem)
    if (needed) new NonEmpty(elem, left.filterAcc(p, acc.incl(elem)), right.filterAcc(p, acc.incl(elem)))
    else left.filterAcc(p, acc).union(right.filterAcc(p, acc))
  }

  def union(that: TweetSet): TweetSet = {
    // TODO 3.B Implement union on tweet sets. Complete the stub for the method union.
    // The method union takes another set that, and computes a new set which is the union of this and that,
    // i.e. a set that contains exactly the elements that are either in this or in that, or in both.

    // in this exercise it is your task to find out in which class(es)
    // to define the union method (should it be abstract in class TweetSet?).
    var res: TweetSet = new NonEmpty(elem, left, right)
    that.foreach(t => res = res.incl(t))
    res
  }

  def descendingByRetweet: TweetList = {
    // TODO 4.A Sorting Tweets by Their Influence
    // The idea is to start with the empty list Nil (containing no tweets),
    // and to find the tweet with the most retweets in the input TweetSet.
    // This tweet is removed from the TweetSet
    // (that is, we obtain a new TweetSet that has all the tweets
    // of the original set except for the tweet that was “removed”;
    // this immutable set operation, remove, is already implemented for you),
    // and added to the result list by creating a new Cons.
    // After that, the process repeats itself, but now we are searching through a TweetSet with one less tweet.

    // Hint: start by implementing the method mostRetweeted which returns the most popular tweet of a TweetSet.
    var res: TweetList = Nil
    var cur: Tweet = null
    var tmp: TweetSet = new NonEmpty(elem, left, right)
    this.foreach(z => {
      tmp.foreach(t => cur = if(cur == null || t.retweets < cur.retweets) t else cur)
      if (cur != null) {
        res = new Cons(cur, res)
        tmp = tmp.remove(cur)
        cur = null
      }
    })
    res
  }

  // TODO 3.C Implement union on tweet sets. Complete the stub for the method union.
  // in this exercise it is your task to find out in which class(es)
  // to define the union method (should it be abstract in class TweetSet?).

  /**
   * The following methods are already implemented
   */

  def contains(x: Tweet): Boolean = {
    // TODO 1.E first study the already implemented methods contains and incl for inspiration.
    if (x.text < elem.text) left.contains(x)
    else if (elem.text < x.text) right.contains(x)
    else true
  }

  def incl(x: Tweet): TweetSet = {
    // TODO 1.F first study the already implemented methods contains and incl for inspiration.
    if (x.text < elem.text) new NonEmpty(elem, left.incl(x), right)
    else if (elem.text < x.text) new NonEmpty(elem, left, right.incl(x))
    else this
  }

  def remove(tw: Tweet): TweetSet = {
    // TODO 1.G first study the already implemented methods contains and incl for inspiration.
    if (tw.text < elem.text) new NonEmpty(elem, left.remove(tw), right)
    else if (elem.text < tw.text) new NonEmpty(elem, left, right.remove(tw))
    else left.union(right)
  }

  def foreach(f: Tweet => Unit): Unit = {
    // TODO 1.H first study the already implemented methods contains and incl for inspiration.
    f(elem)
    left.foreach(f)
    right.foreach(f)
  }
}

trait TweetList {
  def head: Tweet
  def tail: TweetList
  def isEmpty: Boolean
  def foreach(f: Tweet => Unit): Unit = {
    // TODO 1.I first study the already implemented methods contains and incl for inspiration.
    if (!isEmpty) {
      f(head)
      tail.foreach(f)
    }
  }
}

object Nil extends TweetList {
  def head = throw new java.util.NoSuchElementException("head of EmptyList")
  def tail = throw new java.util.NoSuchElementException("tail of EmptyList")
  def isEmpty = true
}

class Cons(val head: Tweet, val tail: TweetList) extends TweetList {
  def isEmpty = false
}

object GoogleVsApple {
  val google = List("android", "Android", "galaxy", "Galaxy", "nexus", "Nexus")
  val apple = List("ios", "iOS", "iphone", "iPhone", "ipad", "iPad")

  // TODO 6.A Tying everything together
  // As a first step, use the functionality you implemented in the first parts of this assignment
  // to create two different TweetSets, googleTweets andappleTweets.
  // The first TweetSet, googleTweets, should contain all tweets that mention (in their “text”)
  // one of the keywords in the google list. The second TweetSet, appleTweets, should contain
  // all tweets that mention one of the keyword in the apple list. Their signature is as follows:
  //   lazy val googleTweets: TweetSet
  //   lazy val appleTweets: TweetSet
  // Hint: use the exists method of List and contains method of classjava.lang.String.

  lazy val googleTweets: TweetSet = TweetReader.allTweets.filter(t => {
    google.map(s => t.text.contains(s)).foldLeft(false)(_||_)
  })
  // FIXME lazy val googleTweets: TweetSet = new Nothing()

  // TODO 6.B Tying everything together
  lazy val appleTweets: TweetSet = TweetReader.allTweets.filter(t => {
    apple.map(s => t.text.contains(s)).foldLeft(false)(_||_)
  })
  // FIXME lazy val appleTweets: TweetSet = new Nothing()

  /**
   * A list of all tweets mentioning a keyword from either apple or google,
   * sorted by the number of retweets.
   */
  // TODO 6.C Tying everything together
  // From the union of those two TweetSets, produce trending, an instance of class TweetList
  // representing a sequence of tweets ordered by their number of retweets:

  // lazy val trending: TweetList

  lazy val trending: TweetList = googleTweets.union(appleTweets).descendingByRetweet
  // FIXME lazy val trending: TweetList = new Nothing()
  }

object Main extends App {
  // Print the trending tweets
  GoogleVsApple.trending foreach println
}
