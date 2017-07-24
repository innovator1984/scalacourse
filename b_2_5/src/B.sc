

object A {
  println("Welcome")                              //> Welcome
  val x = List(1, 2, 3)                           //> x  : List[Int] = List(1, 2, 3)
  val y = List(4, 5, 6)                           //> y  : List[Int] = List(4, 5, 6)

  x ::: y                                         //> res0: List[Int] = List(1, 2, 3, 4, 5, 6)
  x ++ y                                          //> res1: List[Int] = List(1, 2, 3, 4, 5, 6)

  x :: y                                          //> res2: List[Any] = List(List(1, 2, 3), 4, 5, 6)
  
  val z = x :: y                                  //> z  : List[Any] = List(List(1, 2, 3), 4, 5, 6)
  z match {
    case ::( q,p) => println("::")
    case _ => println("_")
  }                                               //> ::

  // @SerialVersionUID(509929039250432923L) // value computed by serialver for 2.11.2, annotation added in 2.11.4
  // final case class ::[B](override val head: B, private[scala] var tl: List[B]) extends List[B] {
  //    override def tail : List[B] = tl
  //    override def isEmpty: Boolean = false
  // }
  
  val a = 2                                       //> a  : Int = 2
  val b = 3                                       //> b  : Int = 3
  val c = a + b                                   //> c  : Int = 5
  val d = a - b                                   //> d  : Int = -1
  

  // z match {
  //   case p - q => println("minus")
  //   case p + q => println("plus")
  // }

}


class Pouring(capacity: Vector[Int]) {

// States
  type State = Vector[Int]
  val initialState = capacity map (x => 0)
  
// Moves
  trait Move {
    def change(state: State): State
  }
  
  case class Empty(glass: Int) extends Move {
    def change(state: State) = state updated (glass, 0)
  }
  
  case class Fill(glass: Int) extends Move {
    def change(state: State) = state updated (glass, capacity(glass))
  }
  
  case class Pour(from: Int, to: Int) extends Move {
    def change(state: State) = {
      val amount = state(from) min (capacity(to) - state(to))
      state updated (from, state(from) - amount) updated (to, state(to) + amount)
    }
  }
  
  val glasses = 0 until capacity.length
  
  val moves =
    (for (g <- glasses) yield Empty(g)) ++
    (for (g <- glasses) yield Fill(g)) ++
    (for (from <- glasses; to <- glasses if from != to) yield Pour(from, to))

// Paths
  class Path(history: List[Move], val endState: State) {
    private def trackState(xs: List[Move]): State = xs match {
      case Nil => initialState
      case move :: xs1 => move change trackState(xs1)
    }
    // def endState: State = (history foldRight initialState) (_ change _)
    def extend(move: Move) = new Path(move :: history, move change endState)
    override def toString = (history.reverse mkString " ") + "--> " + endState
  }

  val initialPath = new Path(Nil, initialState)

  def from(paths: Set[Path], explored: Set[State]): Stream[Set[Path]] = {
    if (paths.isEmpty) Stream.empty
    else {
      val more = for {
        path <- paths
        next <- moves map path.extend
        if !(explored contains next.endState)
      } yield next
      paths #:: from(more, explored ++ (more map (_.endState)))
    }
  }
  
  val pathSets = from(Set(initialPath), Set(initialState))
  
  def solutions(targetVolume: Int): Stream[Path] = {
    for {
      pathSet <- pathSets
      path <- pathSet
      if path.endState contains targetVolume
    } yield path
  }

}

object B {
  println("Welcome to the Scala worksheet")
  
  val problem = new Pouring(Vector(4, 9))
  problem.moves
  problem.solutions(6)
  val problem2 = new Pouring(Vector(4, 9, 19))
  problem2.moves
  problem2.solutions(17)
}