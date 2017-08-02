import math.abs

object C {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
 
  class ABC {
    private def computePixel(xc: Double, yc: Double, maxIterations: Int): Int = {
      var image = Array(1, 2, 3)
      def coordinatesFor(idx: Int): (Double, Double) = {
        (0f + idx, 0f)
      }
      def parRender(): Unit = {
        for (idx <- (0 until image.length).par) {
          val (xc , yc) = coordinatesFor(idx)
          image(idx) = computePixel(xc, yc, maxIterations)
        }
      }
      var i = 0
      var x, y = 0.0
      while (x * x + y * y < 4 && i < maxIterations) {
        val xt = x * x - y * y + xc
        val yt = 2 * x * y + yc
        x = xt; y = yt
        i += 1
      }
      i
      // color(i)
      // TODO: what is color?
    }
    private def initializeArray(xs: Array[Int])(v: Int): Unit = {
      for (i <- (0 until xs.length).par) {
        xs(i) = v
        xs(0) = i
      }
    }

    
  }
}