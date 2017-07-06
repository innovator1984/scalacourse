object A {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  
  val x = new Rational(1, 3)                      //> x  : Rational = 1/3
  val y = new Rational(5, 7)                      //> y  : Rational = 5/7
  val z = new Rational(3, 2)                      //> z  : Rational = 3/2
  
  x.sub(y).sub(z)                                 //> res0: Rational = -79/42
  
  val x2 = new Rational(1, 2)                     //> x2  : Rational = 1/2
  x2.numer                                        //> res1: Int = 1
  x2.denom                                        //> res2: Int = 2

  val y2 = new Rational(2, 3)                     //> y2  : Rational = 2/3
  x2.add(y2)                                      //> res3: Rational = 7/6
}

class Rational(x: Int, y: Int) {
  def numer = x
  def denom = y
  def add(that: Rational) =
    new Rational(
      numer * that.denom + that.numer * denom,
      denom * that.denom)
      
  def neg: Rational = new Rational(-numer, denom)
  def sub(that: Rational) = add(that.neg)
  
  
  override def toString = numer + "/" + denom
}