package recfun

object Main {
  def main(args: Array[String]) {
    println("Pascal's Triangle")
    for (row <- 0 to 10) {
      for (col <- 0 to row)
        print(pascal(col, row) + " ")
      println()
    }
  }

  /**
   * Exercise 1
   */
  def pascal(c: Int, r: Int): Int = {
    if (c == 0 || c == r) 1
    else pascal(c - 1, r - 1) + pascal(c, r - 1)
  }

  /**
   * Exercise 2
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
   * Exercise 3
   */
  def countChange(money: Int, coins: List[Int]): Int = {
    def calc(it: Iterator[scala.collection.mutable.ListBuffer[Int]]): Integer = {
      var dict = scala.collection.mutable.Map[String, Int]()
      for (lst <- it) if (lst.sum == money) dict += lst.mkString("\n") -> 1
      dict.keySet.size
    }
    var multi = scala.collection.mutable.ListBuffer[Int]()
    for (i <- coins.indices; t <- 0 until money / coins(i) if coins(i) != 0) multi += coins(i)
    var count = 0
    for (n <- multi.indices) count += calc(multi.combinations(n))
    count
  }
  }
