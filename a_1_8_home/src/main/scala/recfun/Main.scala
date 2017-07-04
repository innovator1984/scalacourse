package recfun

object Main {
  def main(args: Array[String]) {
    println("Pascal's Triangle")
    for (row <- 0 to 10) {
      for (col <- 0 to row)
        print(pascal(col, row) + " ")
      println()
    }
    // println("1+2 => 4: " + countChange(4,List(1,2)))
  }

  /**
   * Exercise 1 Pascal’s Triangle
    * 1
    * 1 1
    * 1 2 1 pascal(1,2)=2 pascal(0,2)=1
    * 1 3 3 1 pascal(1,3)=3
    * 1 4 6 4 1
   */
    def pascal(c: Int, r: Int): Int = {
        if (c == 0 || c == r) 1
        else pascal(c - 1, r - 1) + pascal(c, r - 1)
    }

  /**
   * Exercise 2 Balancing of parentheses
    * The function should return true for the following strings:
    * (if (zero? x) max (/ 1 x))
    * I told him (that it’s not (yet) done). (But he wasn’t listening)
    *
    * The function should return false for the following strings:
    * :-)
    * ())(
   */
    def balance(chars: List[Char]): Boolean = {
        def loop(chars: List[Char], index: Int, last: Int): Integer = {
          if(index >= chars.size) return last
          if(chars(index) == ')' && last <= 0) return -1
          if(chars(index) == ')') return loop(chars, index + 1, last - 1)
          if(chars(index) == '(') return loop(chars, index + 1, last + 1)
          loop(chars, index + 1, last)
        }
        loop(chars, 0, 0) == 0
    }

  /**
   * Exercise 3 Counting Change
    * Counts how many different ways you can make change for an amount, given a list of coin denominations.
    * For example, there are 3 ways to give change for 4 if you have coins with denomination 1 and 2:
    * 1+1+1+1, 1+1+2, 2+2.
    * HINT: How many ways can you give change for 0 CHF(swiss money)? How many ways can you give change for >0 CHF,
    * if you have no coins?
   */
    def countChange(money: Int, coins: List[Int]): Int = {
      def calc(it: Iterator[scala.collection.mutable.ListBuffer[Int]]): Integer = {
        var dict = scala.collection.mutable.Map[String, Int]()
        for (lst <- it) if (lst.sum == money) dict += lst.mkString("\n") -> 1
        dict.keySet.size
      }
      var multi = scala.collection.mutable.ListBuffer[Int]()
      for (i <- coins.indices; t <- 0 until money / coins(i)) multi += coins(i)
      var count = 0
      for (n <- multi.indices) count += calc(multi.combinations(n))
      count
    }
  }
