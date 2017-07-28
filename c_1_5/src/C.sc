import math.abs

object C {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
 
  def power(x: Int, p: Double): Int = math.exp(p * math.log(abs(x))).toInt
                                                  //> power: (x: Int, p: Double)Int
  
  def sumSegment(a: Array[Int], p: Double, s: Int, t: Int): Int = {
    var i = s; var sum: Int = 0
    while (i < t) {
      sum = sum + power(a(i), p)
      i = i + 1
    }
    sum
  }                                               //> sumSegment: (a: Array[Int], p: Double, s: Int, t: Int)Int
  
  def pNormTwoPart(a: Array[Int], p: Double): Int = {
    val m = a.length / 2
    val (sum1, sum2) = (sumSegment(a, p, 0, m),
                        sumSegment(a, p, m, a.length))
    power(sum1 + sum2, 1/p)
  }                                               //> pNormTwoPart: (a: Array[Int], p: Double)Int
}