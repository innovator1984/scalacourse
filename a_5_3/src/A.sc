import week5.mergesort._

object A {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  val nums = List(2, -4, 5, 7, 1)                 //> nums  : List[Int] = List(2, -4, 5, 7, 1)
  val fruits = List("apple", "pineapple", "orange", "banana")
                                                  //> fruits  : List[String] = List(apple, pineapple, orange, banana)
  msort(nums)(Ordering.Int)                       //> res0: List[Int] = List(-4, 1, 2, 5, 7)
  msort(fruits)(Ordering.String)                  //> res1: List[String] = List(apple, banana, orange, pineapple)
}