package streams

import org.scalatest.FunSuite

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import Bloxorz._

@RunWith(classOf[JUnitRunner])
class BloxorzSuite extends FunSuite {

  trait SolutionChecker extends GameDef with Solver with StringParserTerrain {
    /**
     * This method applies a list of moves `ls` to the block at position
     * `startPos`. This can be used to verify if a certain list of moves
     * is a valid solution, i.e. leads to the goal.
     */
    def solve(ls: List[Move]): Block =
      ls.foldLeft(startBlock) { case (block, move) =>
        require(block.isLegal) // The solution must always lead to legal blocks
        move match {
          case Left => block.left
          case Right => block.right
          case Up => block.up
          case Down => block.down
        }
    }
  }

  trait Level1 extends SolutionChecker {
      /* terrain for level 1*/

    val level =
    """ooo-------
      |oSoooo----
      |ooooooooo-
      |-ooooooooo
      |-----ooToo
      |------ooo-""".stripMargin

    val optsolution = List(Right, Right, Down, Right, Right, Right, Down)
  }


	test("terrain function level 1") {
    new Level1 {
      assert(terrain(Pos(0,0)), "0,0")
      assert(terrain(Pos(1,1)), "1,1") // start
      assert(terrain(Pos(4,7)), "4,7") // goal
      assert(terrain(Pos(5,8)), "5,8")
      assert(!terrain(Pos(5,9)), "5,9")
      assert(terrain(Pos(4,9)), "4,9")
      assert(!terrain(Pos(6,8)), "6,8")
      assert(!terrain(Pos(4,11)), "4,11")
      assert(!terrain(Pos(-1,0)), "-1,0")
      assert(!terrain(Pos(0,-1)), "0,-1")
    }
  }

	test("findChar level 1") {
    new Level1 {
      assert(startPos == Pos(1,1))
    }
  }


	test("optimal solution for level 1") {
    new Level1 {
      assert(solve(solution) == Block(goal, goal))
    }
  }


	test("optimal solution length for level 1") {
    new Level1 {
      assert(solution.length == optsolution.length)
    }
  }

  // ===== MY TESTS =====
  test("main pos 01") {
    // TODO 1.A Bloxorz
    assert(true)
  }

  test("main neg 01") {
    // TODO 1.A Bloxorz
    assert(true)
  }

  test("GameDef pos 01") {
    // TODO 2.A Game Setup
    assert(true)
  }

  test("GameDef neg 01") {
    // TODO 2.A Game Setup
    assert(true)
  }

  test("terrainFunction pos 01") {
    // TODO 2.B Game Setup
    assert(true)
  }

  test("terrainFunction neg 01") {
    // TODO 2.B Game Setup
    assert(true)
  }

  test("findChar pos 01") {
    // TODO 2.C Game Setup
    assert(true)
  }

  test("findChar neg 01") {
    // TODO 2.C Game Setup
    assert(true)
  }

  test("isStanding pos 01") {
    // TODO 3.A Game Setup
    assert(true)
  }

  test("isStanding neg 01") {
    // TODO 3.A Game Setup
    assert(true)
  }

  test("isLegal pos 01") {
    // TODO 3.B Game Setup
    assert(true)
  }

  test("isLegal neg 01") {
    // TODO 3.B Game Setup
    assert(true)
  }

  test("startBlock pos 01") {
    // TODO 3.C Game Setup
    assert(true)
  }

  test("startBlock neg 01") {
    // TODO 3.C Game Setup
    assert(true)
  }

  test("neighbors pos 01") {
    // TODO 4.A Moves and Neighbors
    assert(true)
  }

  test("neighbors neg 01") {
    // TODO 4.A Moves and Neighbors
    assert(true)
  }

  test("legalNeighbors pos 01") {
    // TODO 4.B Moves and Neighbors
    assert(true)
  }

  test("legalNeighbors neg 01") {
    // TODO 4.B Moves and Neighbors
    assert(true)
  }

  test("done pos 01") {
    // TODO 5.A Solving the Game
    assert(true)
  }

  test("done neg 01") {
    // TODO 5.A Solving the Game
    assert(true)
  }

  test("neighborsWithHistory pos 01") {
    // TODO 5.B Solving the Game
    assert(true)
  }

  test("neighborsWithHistory neg 01") {
    // TODO 5.B Solving the Game
    assert(true)
  }

  test("newNeighborsOnly pos 01") {
    // TODO 5.C Solving the Game
    assert(true)
  }

  test("newNeighborsOnly neg 01") {
    // TODO 5.C Solving the Game
    assert(true)
  }

  test("from pos 01") {
    // TODO 5.D Solving the Game
    assert(true)
  }

  test("from neg 01") {
    // TODO 5.D Solving the Game
    assert(true)
  }

  test("pathsFromStart pos 01") {
    // TODO 6.A Putting Things together
    assert(true)
  }

  test("pathsFromStart neg 01") {
    // TODO 6.A Putting Things together
    assert(true)
  }

  test("pathsToGoal pos 01") {
    // TODO 6.B Putting Things together
    assert(true)
  }

  test("pathsToGoal neg 01") {
    // TODO 6.B Putting Things together
    assert(true)
  }

  test("solution pos 01") {
    // TODO 6.C Putting Things together
    assert(true)
  }

  test("solution neg 01") {
    // TODO 6.C Putting Things together
    assert(true)
  }
}
