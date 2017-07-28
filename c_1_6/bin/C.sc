import scala.util.Random

object C {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
 
  def mcCount(iter: Int): Int = {
    val randomX = new Random
    val randomY = new Random
    var hits = 0
    for (i <- 0 until iter) {
      val x0to1 = randomX.nextDouble
      val y0to1 = randomY.nextDouble
      if (x0to1 * x0to1 + y0to1 * y0to1 < 1) hits = hits + 1
    }
    hits
  }                                               //> mcCount: (iter: Int)Int
  def oldMonteCarloPiSeq(iter: Int): Double = 4.0 * mcCount(iter) / iter
                                                  //> oldMonteCarloPiSeq: (iter: Int)Double

  def monteCarloPiSeq(iter: Int): Double = {
    val ((pi1, pi2), (pi3, pi4)) = (
    (mcCount(iter/4), mcCount(iter/4)),
    (mcCount(iter/4), mcCount(iter - 3*(iter/4))))
    4.0 * (pi1 + pi2 + pi3 + pi4) / iter
  }                                               //> monteCarloPiSeq: (iter: Int)Double
  monteCarloPiSeq(1000000)                        //> res0: Double = 3.140644
  
}