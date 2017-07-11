package week6

object test {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  
  val xs = Array(1,2,3,44)                        //> xs  : Array[Int] = Array(1, 2, 3, 44)
  xs map (x => x * 2)                             //> res0: Array[Int] = Array(2, 4, 6, 88)
  val s = "Hello World"                           //> s  : String = Hello World
  s filter (c => c.isUpper)                       //> res1: String = HW
  
  
  s exists (c => c.isUpper)                       //> res2: Boolean = true
  s forall (c => c.isUpper)                       //> res3: Boolean = false
  
  val pairs = List(1,2,3) zip s                   //> pairs  : List[(Int, Char)] = List((1,H), (2,e), (3,l))
  pairs.unzip                                     //> res4: (List[Int], List[Char]) = (List(1, 2, 3),List(H, e, l))
  
  s flatMap (x => List('.', x))                   //> res5: String = .H.e.l.l.o. .W.o.r.l.d
  
  xs.min                                          //> res6: Int = 1
  xs.max                                          //> res7: Int = 44
  
  def isPrime(n: Int): Boolean = (2 until n) forall (d => n % d != 0)
                                                  //> isPrime: (n: Int)Boolean
  
  isPrime(17)                                     //> res8: Boolean = true
}