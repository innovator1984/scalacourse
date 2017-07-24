package streams

import common._

/**
 * This component implements the solver for the Bloxorz game
 */
trait Solver extends GameDef {

  /**
   * Returns `true` if the block `b` is at the final position
   */
  def done(b: Block): Boolean = {
    // TODO 5.A Solving the Game
    // Now that everything is set up, we can concentrate
    // on actually coding our solver which is defined in the file Solver.scala.
    // We could represent a path to a solution as a Stream[Block].
    // We however also need to make sure we keep the history on our way to the solution.
    // Therefore, a path is represented as a Stream[(Block, List[Move])],
    // where the second part of the pair records the history of moves so far.
    // Unless otherwise noted, the last move is the head element of the List[Move].
    // First, implement a function done which determines when we have reached the goal:
    // QQQ
    b.b1 == goal && b.b2 == goal
  }

  /**
   * This function takes two arguments: the current block `b` and
   * a list of moves `history` that was required to reach the
   * position of `b`.
   *
   * The `head` element of the `history` list is the latest move
   * that was executed, i.e. the last move that was performed for
   * the block to end up at position `b`.
   *
   * The function returns a stream of pairs: the first element of
   * the each pair is a neighboring block, and the second element
   * is the augmented history of moves required to reach this block.
   *
   * It should only return valid neighbors, i.e. block positions
   * that are inside the terrain.
   */
  def neighborsWithHistory(b: Block, history: List[Move]): Stream[(Block, List[Move])] = {
    // TODO 5.B Solving the Game
    // Finding Neighbors
    // Then, implement a function neighborsWithHistory, which,
    // given a block, and its history, returns a stream of neighboring blocks with the corresponding moves.
    // As mentioned above, the history is ordered so that the most recent move is the head of the list.
    // If you consider Level 1 as defined in Bloxorz.scala, then
    //     neighborsWithHistory(Block(Pos(1,1),Pos(1,1)), List(Left,Up))
    // results in a stream with the following elements (given as a set):
    //   Set(
    //     (Block(Pos(1,2),Pos(1,3)), List(Right,Left,Up)),
    //     (Block(Pos(2,1),Pos(3,1)), List(Down,Left,Up))
    //   )
    // You should implement the above example as a test case in the test suiteBloxorzSuite.
    // QQQ
    /*
    for {
      n <- b.legalNeighbors.toStream
    } yield (n._1, n._2 :: history)
    */
    for ((nextBlock, move) <- b.legalNeighbors.toStream) yield (nextBlock, move :: history)
  }

  /**
   * This function returns the list of neighbors without the block
   * positions that have already been explored. We will use it to
   * make sure that we don't explore circular paths.
   */
  def newNeighborsOnly(neighbors: Stream[(Block, List[Move])],
                       explored: Set[Block]): Stream[(Block, List[Move])] = {
    // TODO 5.C Solving the Game
    // Avoiding Circles
    // While exploring a path, we will also track all the blocks we have seen so far,
    // so as to not get lost in circles of movements (such as sequences of left-right-left-right).
    // Implement a function newNeighborsOnly to this effect:
    //   def newNeighborsOnly(neighbors: Stream[(Block, List[Move])],
    //     explored: Set[Block]): Stream[(Block, List[Move])] = ???
    // Example usage:
    // newNeighborsOnly(
    // Set(
    // (Block(Pos(1,2),Pos(1,3)), List(Right,Left,Up)),
    // (Block(Pos(2,1),Pos(3,1)), List(Down,Left,Up))
    // ).toStream,
    // Set(Block(Pos(1,2),Pos(1,3)), Block(Pos(1,1),Pos(1,1)))
    // )
    // returns
    //   Set(
    //     (Block(Pos(2,1),Pos(3,1)), List(Down,Left,Up))
    //   ).toStream
    // Again, you should convert this example into a test case.
    // QQQ
    /*
    for {
      (block, move) <- neighbors
      if (!explored.contains(block))
    } yield(block, move)
    */
    neighbors.filterNot { case (block, history) => explored.contains(block) }
  }

  /**
   * The function `from` returns the stream of all possible paths
   * that can be followed, starting at the `head` of the `initial`
   * stream.
   *
   * The blocks in the stream `initial` are sorted by ascending path
   * length: the block positions with the shortest paths (length of
   * move list) are at the head of the stream.
   *
   * The parameter `explored` is a set of block positions that have
   * been visited before, on the path to any of the blocks in the
   * stream `initial`. When search reaches a block that has already
   * been explored before, that position should not be included a
   * second time to avoid cycles.
   *
   * The resulting stream should be sorted by ascending path length,
   * i.e. the block positions that can be reached with the fewest
   * amount of moves should appear first in the stream.
   *
   * Note: the solution should not look at or compare the lengths
   * of different paths - the implementation should naturally
   * construct the correctly sorted stream.
   */
  def from(initial: Stream[(Block, List[Move])],
           explored: Set[Block]): Stream[(Block, List[Move])] = {
    // TODO 5.D Solving the Game
    // Finding Solutions
    // Now to the crux of the solver. Implement a function from, which,
    // given an initial stream and a set of explored blocks, creates a stream containing the possible paths
    // starting from the head of the initial stream:
    // Note: pay attention to how the path is constructed: as discussed in the introduction,
    // the key to getting the shortest path for the problem is to explore the space in a breadth-first manner.
    // Hint: The case study lecture about the water pouring problem (7.5) might help you.
    // QQQ
    if (initial.isEmpty) {
      Stream()
    } else {
      val (block, history) = initial.head

      val neighbors = neighborsWithHistory(block, history)
      val newNeighbours = newNeighborsOnly(neighbors, explored)

      val newMoves = initial.tail #::: newNeighbours
      initial.head #:: from(newMoves, explored + block)
    }
  }

  /**
   * The stream of all paths that begin at the starting block.
   */
  lazy val pathsFromStart: Stream[(Block, List[Move])] = {
    // TODO 6.A Putting Things together
    // Finally we can define a lazy val pathsFromStart which is a stream of all the paths that begin
    // at the starting block:
    // QQQ
    from(Stream((startBlock, List())), Set())
  }

  /**
   * Returns a stream of all possible pairs of the goal block along
   * with the history how it was reached.
   */
  lazy val pathsToGoal: Stream[(Block, List[Move])] = {
    // TODO 6.B Putting Things together
    // We can also define pathToGoal which is a stream of all possible pairs of goal blocks along with their history.
    // Indeed, there can be more than one road to Rome!
    // QQQ
    pathsFromStart.filter { case (block, moves) => done(block) }
  }

  /**
   * The (or one of the) shortest sequence(s) of moves to reach the
   * goal. If the goal cannot be reached, the empty list is returned.
   *
   * Note: the `head` element of the returned list should represent
   * the first move that the player should perform from the starting
   * position.
   */
  lazy val solution: List[Move] = {
    // TODO 6.C Putting Things together
    // To finish it off, we define solution to contain the (or one of the)
    // shortest list(s) of moves that lead(s) to the goal.
    // Note: the head element of the returned List[Move]
    // should represent the first move that the player should perform from the starting position.
    // QQQ
    pathsToGoal match {
      case Stream() => List()
      case (_, path) #:: _ => path.reverse
    }
  }
}
