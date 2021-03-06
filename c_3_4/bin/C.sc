import scala.collection.concurrent.TrieMap

object C {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
 
  val graph = TrieMap[Int, Int]() ++= (0 until 100000).map(i => (i, i + 1))
                                                  //> graph  : scala.collection.concurrent.TrieMap[Int,Int] = TrieMap(0 -> 1, 3080
                                                  //| 9 -> 30810, 36210 -> 36211, 37574 -> 37575, 68383 -> 68384, 5401 -> 5402, 40
                                                  //| 37 -> 4038, 42975 -> 42976, 75148 -> 75149, 73784 -> 73785, 10802 -> 10803, 
                                                  //| 9438 -> 9439, 41611 -> 41612, 79185 -> 79186, 16203 -> 16204, 48376 -> 48377
                                                  //| , 80549 -> 80550, 53777 -> 53778, 85950 -> 85951, 15449 -> 15450, 20850 -> 2
                                                  //| 0851, 53023 -> 53024, 27615 -> 27616, 59788 -> 59789, 843 -> 844, 2207 -> 22
                                                  //| 08, 33016 -> 33017, 65189 -> 65190, 34380 -> 34381, 97362 -> 97363, 7608 -> 
                                                  //| 7609, 38417 -> 38418, 70590 -> 70591, 39781 -> 39782, 71954 -> 71955, 75991 
                                                  //| -> 75992, 45182 -> 45183, 77355 -> 77356, 82756 -> 82757, 12255 -> 12256, 19
                                                  //| 020 -> 19021, 25785 -> 25786, 24421 -> 24422, 56594 -> 56595, 94168 -> 94169
                                                  //| , 31186 -> 31187, 29822 -> 29823, 61995 -> 61996, 63359 -> 63360, 36587 -> 3
                                                  //| 6588, 67396 -> 67397, 68760 -> 68761, 99569 -> 99570, 74161 -> 74162, 3660 -
                                                  //| > 3661, 10425 -> 10426, 
                                                  //| Output exceeds cutoff limit.
  graph(graph.size - 1) = 0
  val previous = graph.snapshot()                 //> previous  : scala.collection.concurrent.TrieMap[Int,Int] = TrieMap(0 -> 1, 3
                                                  //| 0809 -> 30810, 36210 -> 36211, 37574 -> 37575, 68383 -> 68384, 5401 -> 5402,
                                                  //|  4037 -> 4038, 42975 -> 42976, 75148 -> 75149, 73784 -> 73785, 10802 -> 1080
                                                  //| 3, 9438 -> 9439, 41611 -> 41612, 79185 -> 79186, 16203 -> 16204, 48376 -> 48
                                                  //| 377, 80549 -> 80550, 53777 -> 53778, 85950 -> 85951, 15449 -> 15450, 20850 -
                                                  //| > 20851, 53023 -> 53024, 27615 -> 27616, 59788 -> 59789, 843 -> 844, 2207 ->
                                                  //|  2208, 33016 -> 33017, 65189 -> 65190, 34380 -> 34381, 97362 -> 97363, 7608 
                                                  //| -> 7609, 38417 -> 38418, 70590 -> 70591, 39781 -> 39782, 71954 -> 71955, 759
                                                  //| 91 -> 75992, 45182 -> 45183, 77355 -> 77356, 82756 -> 82757, 12255 -> 12256,
                                                  //|  19020 -> 19021, 25785 -> 25786, 24421 -> 24422, 56594 -> 56595, 94168 -> 94
                                                  //| 169, 31186 -> 31187, 29822 -> 29823, 61995 -> 61996, 63359 -> 63360, 36587 -
                                                  //| > 36588, 67396 -> 67397, 68760 -> 68761, 99569 -> 99570, 74161 -> 74162, 366
                                                  //| 0 -> 3661, 10425 -> 1042
                                                  //| Output exceeds cutoff limit.
  for ((k, v) <- graph.par) graph(k) = previous(v)
  val violation = graph.find({ case (i, v) => v != (i + 2) % graph.size })
                                                  //> violation  : Option[(Int, Int)] = None
  println(s"violation: $violation")               //> violation: None
}