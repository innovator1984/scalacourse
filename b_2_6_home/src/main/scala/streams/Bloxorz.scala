package streams

/**
 * A main object that can be used to execute the Bloxorz solver
 */
object Bloxorz extends App {

  // TODO 1.A Bloxorz
  // Bloxorz is a game in Flash, which you can access here. As a first step for this assignment,
  // play it for a few levels.
  // The objective of Bloxorz is simple; you must navigate your rectangular
  // block to the hole at the end of the board, by rolling it, in the fewest number of moves possible.
  // A block can be moved in 4 possible directions, left, right, up, down, using the appropriate keys on the keyboard.
  // You will quickly notice that for many levels, you are, in your head, trying to walk through
  // different configurations/positions of where the block can be in order to reach it to the goal position.
  // Equipped with some new programming skills, you can now let your computer do the work!
  // The idea of this assignment is to code a solver for a simplified version of this game, with no orange tiles,
  // circles or crosses on the terrain. The goal of your program, given a terrain configuration
  // with a start position and a goal position, is to return the exact sequence of keys to type in order
  // to reach the goal position. Naturally, we will be interested in getting the shortest path as well.

  // State-space Exploration
  // The theory behind coding a solver for this game is in fact be applicable to many different problems.
  // The general problem we are trying to solve is the following:
  // * We start at some initial state S, and we are trying to reach an end stateT.
  // * From every state, there are possible transitions to other states, some of which are out of bounds.
  // We explore the states, starting from S. by exploring its neighbors and following the chain, until we reach T.
  // There are different ways of exploring the state space.
  // On the two ends of the spectrum are the following techniques:
  // * depth-first search: when we see a new state, we immediately explore its direct neighbors,
  // and we do this all the way down, until we reach a roadblock.
  // Then we backtrack until the first non-explored neighbor, and continue in the same vein.
  // * breadth-first search: here, we proceed more cautiously.
  // When we find the neighbors of our current state, we explore each of them for each step.
  // The respective neighbors of these states are then stored to be explored at a later time.

  // HACK git hub dot com
  // /ronxin/stolzen
  // /blob/master/courses/coursera
  // /Functional%20Programming%20Principles%20in%20Scala/progfun-code/src/main/scala/streams/GameDef.scala

  /**
   * A level constructed using the `InfiniteTerrain` trait which defines
   * the terrain to be valid at every position.
   */
  object InfiniteLevel extends Solver with InfiniteTerrain {
    val startPos = Pos(1,3)
    val goal = Pos(5,8)
  }

  println(InfiniteLevel.solution)

  /**
   * A simple level constructed using the StringParserTerrain
   */
  abstract class Level extends Solver with StringParserTerrain

  object Level0 extends Level {
    val level =
      """------
        |--ST--
        |--oo--
        |--oo--
        |------""".stripMargin
  }

  println(Level0.solution)

  /**
   * Level 1 of the official Bloxorz game
   */
  object Level1 extends Level {
    val level =
      """ooo-------
        |oSoooo----
        |ooooooooo-
        |-ooooooooo
        |-----ooToo
        |------ooo-""".stripMargin
  }

  println(Level1.solution)
}
