import math.abs

object C {
  println("Welcome to the Scala worksheet")
  
  def merge(src: Array[Int], dst: Array[Int]) {
  }
  
  def sort(from: Int, until: Int, depth: Int): Unit = {
    if (depth == maxDepth) {
      quickSort(xs, from, until - from)
    } else {
      val mid = (from + until) / 2
      (sort(mid( until, depth + 1), sort(from, mid, depth + 1))
      val flip = (maxDepth - depth) % 2 == 0
      val src = if (flip) ys else xs
      val dst = if(flip) xs else ys
      
      merge(src, dst, from, mid, until)
    }
  }
  val xs = List(1, 3, 2)
  sort(0, xs.length, 0)
}