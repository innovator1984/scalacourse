package observatory

import com.sksamuel.scrimage.{Image, Pixel}

import scala.annotation.{tailrec}
import scala.math.{Pi, acos, sin, cos, pow}

/**
  * 2nd milestone: basic visualization
  */
object Visualization {

  val EARTH_RADIUS = 6371.0
  val P = 4.0
  val MIN_ARC_DISTANCE = 1.0
  val TO_RADIANS = Pi / 180.0

  def myDist(x: Location, xi: Location): Double = {
    // TODO 2.B predictTemperature.myDist
    val deltaLambda = ((x.lon max xi.lon) - (x.lon min xi.lon)) * TO_RADIANS
    val sigma = acos(sin(x.lat * TO_RADIANS) * sin(xi.lat * TO_RADIANS)
      + cos(x.lat * TO_RADIANS)  * cos(xi.lat * TO_RADIANS) * cos(deltaLambda))
    EARTH_RADIUS * sigma
  }

  def myIdw(sample: Iterable[(Location, Double)], x: Location, p: Double): Double = {
    // TODO 2.C predictTemperature.myIDW
    @tailrec
    def recIdw(values: Iterator[(Location, Double)], accVals: Double, accWeights: Double): Double = {
      values.next match {
        case (xi, ui) => {
          val arcDist = myDist(x, xi)
          if (arcDist < MIN_ARC_DISTANCE) {
            ui
          } else {
            val w = 1.0 / pow(arcDist, p)
            if (values.hasNext) recIdw(values, accVals + w * ui, accWeights + w)
            else (accVals + w * ui) / (accWeights + w)
          }
        }
      }
    }
    recIdw(sample.toIterator, 0.0, 0.0)
  }

  /**
    * @param temperatures Known temperatures: pairs containing a location and the temperature at this location
    * @param location Location where to predict the temperature
    * @return The predicted temperature at `location`
    */
  def predictTemperature(temperatures: Iterable[(Location, Double)], location: Location): Double = {
    // TODO 2.A predictTemperature
    ??? // This milestone is disabled. To enable it, set the 'Grading.milestone' value to '2'.
    // QQQ
    // myIdw(temperatures, location, P)
  }

  /**
    * @param points Pairs containing a value and its associated color
    * @param value The value to interpolate
    * @return The color that corresponds to `value`, according to the color scale defined by `points`
    */
  def interpolateColor(points: Iterable[(Double, Color)], value: Double): Color = {
    // TODO 2.D interpolateColor
    ??? // This milestone is disabled. To enable it, set the 'Grading.milestone' value to '2'.
    // QQQ
    // val sortedPoints = points.toList.sortWith(_._1 < _._1).toArray
    // for (i <- 0 until sortedPoints.length - 1) {
    //   (sortedPoints(i), sortedPoints(i + 1)) match {
    //     case ((v1, Color(r1, g1, b1)), (v2, Color(r2, g2, b2))) => {
    //       if (v1 > value) {
    //         Color(r1, g1, b1)
    //       } else if (v2 > value) {
    //         val ratio = (value - v1) / (v2 - v1)
    //         Color(
    //           math.round(r1 + (r2 - r1) * ratio).toInt,
    //           math.round(g1 + (g2 - g1) * ratio).toInt,
    //           math.round(b1 + (b2 - b1) * ratio).toInt
    //         )
    //       }
    //     }
    //   }
    // }
    // sortedPoints(sortedPoints.length - 1)._2
    // NOTICE: Value is not within the colormap.  Return maximum color
  }

  trait Visualizer {
    // TODO 2.F visualize.Visualizer
    val alpha: Int
    val width: Int
    val height: Int
    val colorMap: Array[(Double, Color)]

    def colorToPixel(c: Color): Pixel = {
      Pixel.apply(c.red, c.green, c.blue, alpha)
    }

    def xyToLocation(x: Int, y: Int): Location

    def visualize(temperatures: Iterable[(Location, Double)]): Image = {
      val buffer = new Array[Pixel](width * height)
      for (y <- 0 until height) {
        for (x <- 0 until width) {
          val temp = Visualization.myIdw(temperatures, xyToLocation(x, y), Visualization.P)
          buffer(y * width + x) = colorToPixel(Visualization.interpolateColor(colorMap, temp))
        }
      }

      Image(width, height, buffer)
    }
  }

  class MyVisualizer(colors: Iterable[(Double, Color)]) extends Visualizer {
    // TODO 2.G visualize.MyVisualizer
    val alpha = 255
    val width = 360
    val height = 180
    val colorMap = colors.toList.sortWith(_._1 < _._1).toArray

    def xyToLocation(x: Int, y: Int): Location = Location(90 - y, x - 180)
  }

  /**
    * @param temperatures Known temperatures
    * @param colors Color scale
    * @return A 360×180 image where each pixel shows the predicted temperature at its location
    */
  def visualize(temperatures: Iterable[(Location, Double)], colors: Iterable[(Double, Color)]): Image = {
    // TODO 2.E visualize
    ??? // This milestone is disabled. To enable it, set the 'Grading.milestone' value to '2'.
    // QQQ
    // val visualizer = new MyVisualizer(colors)
    // visualizer.visualize(temperatures)
  }

}

