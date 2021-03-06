package observatory

import com.sksamuel.scrimage.{Image, Pixel}
import scala.math.{pow, atan, sinh, Pi}
import scala.concurrent.{Future, Await}
import scala.concurrent.duration.{Duration}
import scala.concurrent.ExecutionContext.Implicits.global

// USE import scala.concurrent.ExecutionContext.Implicits.global
// INSTEAD OF implicit val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(10))

/**
  * 3rd milestone: interactive visualization
  */
object Interaction {

  /**
    * @param zoom Zoom level
    * @param x X coordinate
    * @param y Y coordinate
    * @return The latitude and longitude of the top-left corner of the tile, as per http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
    */
  def tileLocation(zoom: Int, x: Int, y: Int): Location = {
    // TODO 3.A tileLocation
    ??? // This milestone is disabled. To enable it, set the 'Grading.milestone' value to '3'.
    // QQQ
    // val n = pow(2.0, zoom)
    // val lon = ((x.toDouble / n) % 1.0) * 360.0 - 180.0
    // val lat = ((atan(sinh(Pi * (1.0 - 2.0 * y / n))).toDegrees + 90) % 180.0) - 90
    // Location(lat, lon)
  }

  import Visualization.Visualizer
  class MyTileVisualizer(zoom: Int, colors: Iterable[(Double, Color)], x: Int, y: Int) extends Visualizer {
    // TODO 3.C tile.MyTileVisualizer
    val alpha = 127
    val width = 256
    val height = 256
    val colorMap = colors.toList.sortWith(_._1 < _._1).toArray

    // Tile offset of this tile in the zoom+8 coordinate system
    val x0 = pow(2.0, 8).toInt * x
    val y0 = pow(2.0, 8).toInt * y

    def xyToLocation(x: Int, y: Int): Location = {
      tileLocation(zoom + 8, x0 + x, y0 + y)
    }
  }

  /**
    * @param temperatures Known temperatures
    * @param colors Color scale
    * @param zoom Zoom level
    * @param x X coordinate
    * @param y Y coordinate
    * @return A 256×256 image showing the contents of the tile defined by `x`, `y` and `zooms`
    */
  def tile(temperatures: Iterable[(Location, Double)], colors: Iterable[(Double, Color)], zoom: Int, x: Int, y: Int): Image = {
    // TODO 3.B tile
    ??? // This milestone is disabled. To enable it, set the 'Grading.milestone' value to '3'.
    // QQQ
    // val vis = new MyTileVisualizer(zoom, colors, x, y)
    // vis.visualize(temperatures)
  }

  /**
    * Generates all the tiles for zoom levels 0 to 3 (included), for all the given years.
    * @param yearlyData Sequence of (year, data), where `data` is some data associated with
    *                   `year`. The type of `data` can be anything.
    * @param generateImage Function that generates an image given a year, a zoom level, the x and
    *                      y coordinates of the tile and the data to build the image from
    */
  def generateTiles[Data](
    yearlyData: Iterable[(Int, Data)],
    generateImage: (Int, Int, Int, Int, Data) => Unit
  ): Unit = {
    // TODO 3.D generateTiles
    ??? // This milestone is disabled. To enable it, set the 'Grading.milestone' value to '3'.
    // QQQ
    // val tileTasks = for {
    //  (year, data) <- yearlyData
    //  zoom <- 0 until 4
    //  x <- 0 until pow(2.0, zoom).toInt
    //  y <- 0 until pow(2.0, zoom).toInt
    // } yield Future(generateImage(year, zoom, x, y, data))
    // val _ = Await.result(Future.sequence(tileTasks), Duration.Inf)
  }

  // implicit val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(10))
}
