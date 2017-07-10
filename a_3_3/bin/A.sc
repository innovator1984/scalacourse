package week

object A {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet

  def singleton[T](elem: T) = new Cons[T](elem, new Nil[T])
                                                  //> singleton: [T](elem: T)week.Cons[T]
  
  singleton[Int](1)                               //> res0: week.Cons[Int] = week.Cons@6a5fc7f7
  singleton[Boolean](true)                        //> res1: week.Cons[Boolean] = week.Cons@3b6eb2ec
  
  singleton(1)                                    //> res2: week.Cons[Int] = week.Cons@1e643faf
  singleton(true)                                 //> res3: week.Cons[Boolean] = week.Cons@6e8dacdf
  
  
  def nth[T](n: Int, xs: List[T]): T =
    if (xs.isEmpty) throw new IndexOutOfBoundsException
    else if (n ==0) xs.head
    else nth(n - 1, xs.tail)                      //> nth: [T](n: Int, xs: week.List[T])T
    
  val list = new Cons(1, new Cons(2, new Cons(3, new Nil)))
                                                  //> list  : week.Cons[Int] = week.Cons@7a79be86
  nth(2, list)                                    //> res4: Int = 3
  nth(-2, list)                                   //> java.lang.IndexOutOfBoundsException
                                                  //| 	at week.A$$anonfun$main$1.nth$1(week.A.scala:16)
                                                  //| 	at week.A$$anonfun$main$1.apply$mcV$sp(week.A.scala:22)
                                                  //| 	at org.scalaide.worksheet.runtime.library.WorksheetSupport$$anonfun$$exe
                                                  //| cute$1.apply$mcV$sp(WorksheetSupport.scala:76)
                                                  //| 	at org.scalaide.worksheet.runtime.library.WorksheetSupport$.redirected(W
                                                  //| orksheetSupport.scala:65)
                                                  //| 	at org.scalaide.worksheet.runtime.library.WorksheetSupport$.$execute(Wor
                                                  //| ksheetSupport.scala:75)
                                                  //| 	at week.A$.main(week.A.scala:3)
                                                  //| 	at week.A.main(week.A.scala)
}

trait List[T] {
  def isEmpty: Boolean
  def head: T
  def tail: List[T]
}

class Cons[T](val head: T, val tail: List[T]) extends List[T] {
  def isEmpty = false

}

class Nil[T] extends List[T] {
  def isEmpty = true
  def head: Nothing = throw new NoSuchElementException("Nil.head")
  def tail: Nothing = throw new NoSuchElementException("Nil.head")
}