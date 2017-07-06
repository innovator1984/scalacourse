object A {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  
  //   a + b  ^?  c ?^ d   less   a ===> b  | c
  // ((a + b) ^? (c ?^ d)) less ((a ===> b) | c)
  
  val x = new Rational(1, 3)                      //> x  : Rational = 1/3
  val y = new Rational(5, 7)                      //> y  : Rational = 5/7
  val z = new Rational(3, 2)                      //> z  : Rational = 3/2
  
  x.sub(y).sub(z)                                 //> res0: Rational = -79/42
  x.add(y)                                        //> res1: Rational = 22/21
  x.less(y)                                       //> res2: Boolean = true
  x.max(y)                                        //> res3: Rational = 5/7
  
  // val strange = new Rational(1, 0)
  // strange.add(strange)
  
  val x2 = new Rational(1, 2)                     //> x2  : Rational = 1/2
  x2.numer                                        //> res4: Int = 1
  x2.denom                                        //> res5: Int = 2

  val y2 = new Rational(2, 3)                     //> y2  : Rational = 2/3
  x2.add(y2)                                      //> res6: Rational = 7/6
  
  
  new Rational(1, 2).numer                        //> res7: Int = 1
  
  new Rational(1, 2).less(new Rational(2, 3))     //> res8: Boolean = true
  
  x < y                                           //> res9: Boolean = true
  x.max3(y)                                       //> res10: Rational = 5/7
  x + y                                           //> res11: Rational = 22/21
  x - y  - z                                      //> res12: Rational = -79/42
}

class Rational(x: Int, y: Int) {
  require(y != 0, "denominator must be nonzero")
  
  def this(x: Int) = {
     this(x, 1)
     require(1 != 0, "denominator must be nonzero")
  }
  
  private def gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)

//  private val g = gcd(x, y)
//  def numer = x / g
//  def denom = y / g

  def numer = x
  def denom = y
  
  def less(that: Rational) = numer * that.denom < that.numer * denom
  
  def max(that: Rational) = if (this.less(that)) that else this
  
  def add(that: Rational) =
    new Rational(
      numer * that.denom + that.numer * denom,
      denom * that.denom)
      
  def neg: Rational = new Rational(-numer, denom)
  def sub(that: Rational) = add(that.neg)
  
  override def toString = {
    val g = gcd(numer, denom)
    numer/g + "/" + denom/g
  }
  
  def <(that: Rational) = numer * that.denom < that.numer * denom
  
  def max3(that: Rational) = if (this < that) that else this

  def unary_- : Rational = new Rational(-numer, denom)
  def +(that: Rational) =
    new Rational(
      numer * that.denom + that.numer * denom,
      denom * that.denom)
  def -(that: Rational) = this + -that
}