package reductions

import org.scalameter._
import common._

object ParallelCountChangeRunner {

  @volatile var seqResult = 0

  @volatile var parResult = 0

  val standardConfig = config(
    Key.exec.minWarmupRuns -> 20,
    Key.exec.maxWarmupRuns -> 40,
    Key.exec.benchRuns -> 80,
    Key.verbose -> true
  ) withWarmer(new Warmer.Default)

  def main(args: Array[String]): Unit = {
    val amount = 250
    val coins = List(1, 2, 5, 10, 20, 50)
    val seqtime = standardConfig measure {
      seqResult = ParallelCountChange.countChange(amount, coins)
    }
    println(s"sequential result = $seqResult")
    println(s"sequential count time: $seqtime ms")

    def measureParallelCountChange(threshold: ParallelCountChange.Threshold): Unit = {
      val fjtime = standardConfig measure {
        parResult = ParallelCountChange.parCountChange(amount, coins, threshold)
      }
      println(s"parallel result = $parResult")
      println(s"parallel count time: $fjtime ms")
      println(s"speedup: ${seqtime / fjtime}")
    }

    measureParallelCountChange(ParallelCountChange.moneyThreshold(amount))
    measureParallelCountChange(ParallelCountChange.totalCoinsThreshold(coins.length))
    measureParallelCountChange(ParallelCountChange.combinedThreshold(amount, coins))
  }
}

object ParallelCountChange {

  /** Returns the number of ways change can be made from the specified list of
   *  coins for the specified amount of money.
   */
  def countChange(money: Int, coins: List[Int]): Int = {
    // TODO 2.A Parallel Counting Change
    //
    // If you took the course Functional Programming in Scala,
    // you surely recall the assignment in which you had to count the number of ways in which
    // you can make the change for a given amount of money. The text of that assignment was as follows:
    //
    // Write a recursive function that counts how many different ways you can make change for an amount,
    // given a list of coin denominations. For example, there are 3 ways to give change for 4
    // if you have coins with denomination 1 and 2: 1+1+1+1, 1+1+2, 2+2.
    //
    // In this assignment, you will repeat the same task, but this time, your implementation will be parallel.
    // Start with the sequential version of this problem once more -- the countChange function
    // takes the amount of money and the list of different coin denominations.
    // It returns the total number of different ways you can give change:
    //     def countChange(money: Int, coins: List[Int]): Int
    //
    // Note that the solution to this problem is recursive.
    // In every recursive call, we either decide to continue subtracting the next coin in the coins list
    // from the money amount, or we decide to drop the coin from the list of coins.
    // For example, if we have 4 CHF, and coin denominations of 1 and 2, the call graph,
    // in which every node depicts one invocation of the countChange method, is as follows:
    //                                  4,[1, 2]
    // 3,[1, 2]          +            4,[2]
    // 2,[1, 2]    +     3,[2]          2,[2]   +   4,[]
    // 1,[1, 2] + 2,[2]   1,[2] + 3,[]    0,[2] + 2,[]    0
    // 0,[1, 2] + 1,[2]   1       0      0       1       0
    // 1        0
    //
    // QQQ
    if (money < 0) 0
    else if (money == 0) 1
    else if (coins.isEmpty) 0
    else countChange(money - coins.head, coins) + countChange(money, coins.tail)
  }

  type Threshold = (Int, List[Int]) => Boolean

  /** In parallel, counts the number of ways change can be made from the
   *  specified list of coins for the specified amount of money.
   */
  def parCountChange(money: Int, coins: List[Int], threshold: Threshold): Int = {
    // TODO 2.B Parallel Counting Change
    // We can take advantage of this recursive structure by evaluating different subtrees in parallel.
    // This is the next part of the assignment -- implement the method parCountChange
    // that counts the amount of change in parallel:
    //    def parCountChange(money: Int, coins: List[Int], threshold: Threshold): Int
    //
    // As we learned in the lectures, the parCountChange should not spawn parallel computations
    // after reaching the leaf in the call graph -- the synchronization costs of doing this are way too high.
    // Instead, we need to agglomerate parts of the computation.
    // We do this by calling the sequential countChange method when we decide that the amount of work
    // is lower than a certain value, called the threshold. To separate the concern of deciding on the threshold value
    // from the implementation of our parallel algorithm, we implement the threshold functionality in a separate function,
    // described by the Threshold type alias:
    //    type Threshold = (Int, List[Int]) => Boolean
    //
    // When a threshold function returns true for a given amount of money and the given list of coins,
    // the sequential countChange implementation must be called.
    // Implement parCountChange!
    // QQQ
    ???
    if (threshold(money, coins) || money <= 0 || coins.isEmpty) countChange(money, coins)
    else {
      val (left, right)  = parallel[Int, Int](parCountChange(money - coins.head, coins, threshold),
        parCountChange(money, coins.tail, threshold))
      left + right
    }
  }

  /** Threshold heuristic based on the starting money. */
  def moneyThreshold(startingMoney: Int): Threshold = {
    // TODO 2.C Parallel Counting Change
    // Now that we have the parCountChange method, we ask ourselves what is the right implementation
    // of the threshold function? Recall the examples from the lectures,
    // such as summing the array values and computing the norm, where this was easy --
    // we exactly knew the amount of work required to traverse a subrange of the array,
    // so threshold could return true when the length of the subrange was smaller than a certain value.
    //
    // Sadly, the total amount of work for a given parCountChange invocation is hard to evaluate
    // from the remaining amount of money and a list of coins.
    // In fact, the amount of work directly corresponds to the count that parCountChange returns,
    // which is the value that we are trying to compute. Counting change is a canonical
    // example of a task-parallel problem in which the partitioning the workload across processors
    // is solution-driven -- to know how to optimally partition the work, we would first need to solve the problem itself.
    //
    // For this reason, many parallel algorithms in practice rely on heuristics to assess the amount of work in a subtask.
    // We will implement several such heuristics in this exercise, and assess the effect on performance.
    // First, implement the moneyThreshold method, which creates a threshold function that returns true
    // when the amount of money is less than or equal to 2 / 3 of the starting amount:
    //    def moneyThreshold(startingMoney: Int): Threshold
    //
    // Remember that a / b will return the integer division of a and b when both operands are Ints.
    // To avoid this problem, be sure to always do the multiplication of startingMoney by 2
    // before doing the division by 3.
    //
    // Now run the ParallelCountChange application and observe the speedup:
    //    runMain reductions.ParallelCountChangeRunner
    //
    // QQQ
    (money, coins) => money <= (2 * startingMoney) / 3
  }

  /** Threshold heuristic based on the total number of initial coins. */
  def totalCoinsThreshold(totalCoins: Int): Threshold = {
    // TODO 2.D Parallel Counting Change
    // The previous heuristic did not take into account how many coins were left on the coins list,
    // so try two other heuristics. Implement the method totalCoinsThreshold,
    // which returns a threshold function that returns true when the number of coins is less than or equal to the 2 / 3
    // of the initial number of coins:
    //    def totalCoinsThreshold(totalCoins: Int): Threshold
    // Again, be careful about the order of operations.
    // QQQ
    (money, coins) => coins.length <= (2 * totalCoins) / 3
  }

  /** Threshold heuristic based on the starting money and the initial list of coins. */
  def combinedThreshold(startingMoney: Int, allCoins: List[Int]): Threshold = {
    // TODO 2.E Parallel Counting Change
    // Then, implement the method combinedThreshold, which returns a threshold function that returns true
    // when the amount of money multiplied with the number of remaining coins is less
    // than or equal to the starting money multiplied with the initial number of coins divided by 2:
    //    def combinedThreshold(startingMoney: Int, allCoins: List[Int]): Threshold
    // Which of the three threshold heuristics gives the best speedup?
    // Can you think of a heuristic that improves performance even more?
    // QQQ
    (money, coins) => moneyThreshold(startingMoney)(money, coins) && totalCoinsThreshold(allCoins.length)(money, coins)
  }
}
