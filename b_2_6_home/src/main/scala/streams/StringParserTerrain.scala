package streams

import common._

/**
 * This component implements a parser to define terrains from a
 * graphical ASCII representation.
 *
 * When mixing in that component, a level can be defined by
 * defining the field `level` in the following form:
 *
 *   val level =
 *     """------
 *       |--ST--
 *       |--oo--
 *       |--oo--
 *       |------""".stripMargin
 *
 * - The `-` character denotes parts which are outside the terrain
 * - `o` denotes fields which are part of the terrain
 * - `S` denotes the start position of the block (which is also considered
     inside the terrain)
 * - `T` denotes the final position of the block (which is also considered
     inside the terrain)
 *
 * In this example, the first and last lines could be omitted, and
 * also the columns that consist of `-` characters only.
 */
trait StringParserTerrain extends GameDef {

  /**
   * A ASCII representation of the terrain. This field should remain
   * abstract here.
   */
  val level: String

  /**
   * This method returns terrain function that represents the terrain
   * in `levelVector`. The vector contains parsed version of the `level`
   * string. For example, the following level
   *
   *   val level =
   *     """ST
   *       |oo
   *       |oo""".stripMargin
   *
   * is represented as
   *
   *   Vector(Vector('S', 'T'), Vector('o', 'o'), Vector('o', 'o'))
   *
   * The resulting function should return `true` if the position `pos` is
   * a valid position (not a '-' character) inside the terrain described
   * by `levelVector`.
   */
  def terrainFunction(levelVector: Vector[Vector[Char]]): Pos => Boolean = {
    // TODO 2.B Game Setup
    // Your first task is to implement two methods in trait StringParserTerrainthat
    // are used to parse the terrain and the start / end positions.
    // The Scaladoc comments give precise instructions how they should be implemented.
    // QQQ
/*
    pos => {
      if (pos.col < 0 || pos.row < 0) false
      else if(pos.row >= levelVector.length) false
      else if(pos.col >= levelVector(pos.row).length) false
      else if(levelVector(pos.row)(pos.col) == '-') false
      else List('o', 'S', 'T') contains levelVector(pos.row)(pos.col)
    }
*/
      pos => {
        if (pos.row >= 0 && pos.row < levelVector.length) {
          val row = levelVector(pos.row)
          if (pos.col >= 0 && pos.col < row.length) {
            row(pos.col) != '-'
          } else {
            false
          }
        } else {
          false
        }
      }

  }

  /**
   * This function should return the position of character `c` in the
   * terrain described by `levelVector`. You can assume that the `c`
   * appears exactly once in the terrain.
   *
   * Hint: you can use the functions `indexWhere` and / or `indexOf` of the
   * `Vector` class
   */
  def findChar(c: Char, levelVector: Vector[Vector[Char]]): Pos = {
    // TODO 2.C Game Setup
    // Your first task is to implement two methods in trait StringParserTerrainthat
    // are used to parse the terrain and the start / end positions.
    // The Scaladoc comments give precise instructions how they should be implemented.
    // QQQ
    val row = levelVector.indexWhere(a => a.contains(c))
    val col = levelVector(row).indexOf(c)
    Pos(row, col)
/*
    def accumulate(level: Vector[Vector[Char]], row: Int): Pos = {
      val index = level.head.indexOf(c)
      if (index >= 0) {
        Pos(row, index)
      } else {
        accumulate(level.tail, row + 1)
      }
    }
    accumulate(levelVector, 0)
*/
  }

  private lazy val vector: Vector[Vector[Char]] =
    Vector(level.split("\n").map(str => Vector(str: _*)): _*)

  lazy val terrain: Terrain = terrainFunction(vector)
  lazy val startPos: Pos = findChar('S', vector)
  lazy val goal: Pos = findChar('T', vector)

}
