object A {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  
  def init[T](xs: List[T]): List[T] = xs match {
    case List() => throw new Error("init of empty list")
    case List(x) => List()
    case y :: ys => y :: init(ys)
  }                                               //> init: [T](xs: List[T])List[T]
  
  def removeAt[T](n: Int, xs: List[T]) = (xs take n) ::: (xs drop n + 1)
                                                  //> removeAt: [T](n: Int, xs: List[T])List[T]
  
  removeAt(1, List('a', 'b', 'c', 'd'))           //> res0: List[Char] = List(a, c, d)
  
  def flatten(xs: List[Any]): List[Any] = xs match {
    case List() => List()
    case (y :: ys) :: yss => flatten(y :: ys) ::: flatten(yss)
    case y :: ys => y :: flatten(ys)
  }                                               //> flatten: (xs: List[Any])List[Any]

  flatten(List(List(1, 1), 2, List(3, List(5, 8))))
                                                  //> res1: List[Any] = List(1, 1, 2, 3, 5, 8)
  
}