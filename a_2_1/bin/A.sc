object A {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  
  def xsum(f: Int => Int)(a: Int, b: Int): Int = {
    def loop(a: Int, acc: Int): Int = {
      if (a > b) acc
      else loop(a + 1, f(a) + acc)
    }
    loop(a, 0)
  }                                               //> xsum: (f: Int => Int)(a: Int, b: Int)Int
  
  xsum(x => x * x)(3, 5)                          //> res0: Int = 50
}