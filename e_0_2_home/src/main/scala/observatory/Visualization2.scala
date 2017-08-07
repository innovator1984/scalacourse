package observatory

import com.sksamuel.scrimage.{Image, Pixel}
import scala.math.{pow}
/**
  * 5th milestone: value-added information visualization
  */
object Visualization2 {

  /**
    * @param x X coordinate between 0 and 1
    * @param y Y coordinate between 0 and 1
    * @param d00 Top-left value
    * @param d01 Bottom-left value
    * @param d10 Top-right value
    * @param d11 Bottom-right value
    * @return A guess of the value at (x, y) based on the four known values, using bilinear interpolation
    *         See https://en.wikipedia.org/wiki/Bilinear_interpolation#Unit_Square
    */
  def bilinearInterpolation(
    x: Double,
    y: Double,
    d00: Double,
    d01: Double,
    d10: Double,
    d11: Double
  ): Double = {
    // TODO 1.A bilinearInterpolation
    ??? // This milestone is disabled. To enable it, set the 'Grading.milestone' value to '5'.
    // QQQ
    // (d00 * (1.0 - x) * (1.0 - y)) + (d10 * x * (1.0 - y)) + (d01 * (1.0 - x) * y) + (d11 * x * y)
  }

  /**
    * @param grid Grid to visualize
    * @param colors Color scale to use
    * @param zoom Zoom level of the tile to visualize
    * @param x X value of the tile to visualize
    * @param y Y value of the tile to visualize
    * @return The image of the tile at (x, y, zoom) showing the grid using the given color scale
    */
  def visualizeGrid(
    grid: (Int, Int) => Double,
    colors: Iterable[(Double, Color)],
    zoom: Int,
    x: Int,
    y: Int
  ): Image = {
    // TODO 1.B visualizeGrid
    ??? // This milestone is disabled. To enable it, set the 'Grading.milestone' value to '5'.
    // QQQ
    // val alpha = 127
    // val width = 256
    // val height = 256
    // val colorMap = colors.toList.sortWith(_._1 < _._1).toArray

    // def colorToPixel(c: Color): Pixel = {
    // Pixel(c.red, c.green, c.blue, alpha)
    // }
    // NOTICE: Tile offset of this tile in the zoom+8 coordinate system
    // val x0 = pow(2.0, 8).toInt * x
    // val y0 = pow(2.0, 8). toInt * y
    // val buffer = new Array[Pixel](width * height)

    // for (tileY <- 0 until height) {
    // for (tileX <- 0 until width) {
    // val loc = Interaction.tileLocation(zoom + 8, x0 + tileX, y0 + tileY)
    //  val lonFloor = loc.lon.floor.toInt
    //  val lonCeil = loc.lon.ceil.toInt
    //  val latFloor = loc.lat.floor.toInt
    //  val latCeil = loc.lat.ceil.toInt

    // try {
    //    val d00 = grid(latFloor, lonFloor)
    //    val d01 = grid(latCeil, lonFloor)
    //    val d10 = grid(latFloor, lonCeil)
    //    val d11 = grid(latCeil, lonCeil)

    //    val xDelta = loc.lon - lonFloor
    //    val yDelta = loc.lat - latFloor

    //           val iValue = bilinearInterpolation(xDelta, yDelta, d00, d01, d10, d11)
    //    buffer(tileY * width + tileX) = colorToPixel(Visualization.interpolateColor(colorMap, iValue))
    //  } catch {
    //    case e: ArrayIndexOutOfBoundsException => {
    //      println(s"index error loc = $loc, $x0 $tileX $y0 $tileY")
    //      throw e
    //    }
    //  }
    //   }
    // }
    // Image(width, height, buffer)
  }

}
