package scalashop

import java.awt._
import java.awt.event._
import java.awt.image._
import java.io._
import javax.imageio._
import javax.swing._
import javax.swing.event._
import common._

class PhotoCanvas extends JComponent {

  // TODO 1.A Common
  // In this assignment, we will implement a box blur filter, used in applications like Adobe® PhotoShop®
  // to blur images. For the purposes of this assignment, a filter is an algorithm that takes an input image
  // and transforms it in some way into an output image. The box blur filter outputs an image
  // in which every pixel has an average value of the surrounding pixels from the original image.
  // The box blur filter is an example of an embarrasingly parallel problem -- no or very little effort
  // is required to separate it into parallel tasks. Every pixel of the output image can be computed independently
  // of the other pixels, and in parallel.
  //
  // We will proceed in four steps. First, we will implement the kernel of the box blur filter,
  // a method used to compute a single pixel of the output image.
  // Then we will implement two versions of the parallel box blur,
  // and measure the performance difference. Finally, we will try out our parallel box blur
  // implementations on real images, using ScalaShop -- the image manipulation tool
  // that would impress even the Adobe® folks.
  //
  // By the time you are finished with this assignment, you will:
  //  * understand how the box blur filter works
  //  * be able to recognize embarrasingly parallel problems
  //  * know how to measure the performance of a parallel algorithm
  //  * see how to agglomerate parallel computations into batches
  //  * better understand how memory access patterns affect performance of parallel algorithms
  //
  // Preliminaries
  // Before we begin, we need to cover some basic data types and helper methods.
  // These utilities have already been implemented in the package object for this exercise.
  // First, we will use the RGBA type to refer to the value of an image pixel.
  // We will limit ourselves to 32-bit pixel depth images, so we define RGBA to be equal to the 32-bit integer type:
  //   type RGBA = Int
  // Why do we call this type RGBA? This is because each pixel value is composed
  // from 4 components - red, green, blue and alpha, where alpha denotes the amount of transparency
  // in the respective pixel. These components are referred to as channels.
  // The value of each channel is at least 0 and at most 255.
  //
  // We can extract the red, green, blue and alpha channel using the following utility methods,
  // which use bit masking and bit shifting:
  //  def red(c: RGBA): Int = (0xff000000 & c) >>> 24
  //  def green(c: RGBA): Int = (0x00ff0000 & c) >>> 16
  //  def blue(c: RGBA): Int = (0x0000ff00 & c) >>> 8
  //  def alpha(c: RGBA): Int = (0x000000ff & c) >>> 0
  //
  // Similarly, given the values of the four channels, we can obtain the pixel value like this:
  //  def rgba(r: Int, g: Int, b: Int, a: Int): RGBA =
  //    (r << 24) | (g << 16) | (b << 8) | (a << 0)
  //
  // Now that we know how to manipulate individual pixels, we can define our image type Img:
  //  class Img(val width: Int, val height: Int) {
  //   private val data = new Array[RGBA](width * height)
  //   def apply(x: Int, y: Int): RGBA = data(y * width + x)
  //   def update(x: Int, y: Int, c: RGBA): Unit = data(y * width + x) = c
  //  }
  //
  // The image is a two-dimensional entity -- to refer to a pixel in an image,
  // we need to specify an x and y component. On the other hand,
  // the underlying memory model is one-dimensional -- a single offset value specifies the position in the array.
  // When we store the image into memory, we need to map between the two-dimensional image model
  // and the one-dimensional memory model. We will do this by storing consecutive rows of the image one after another,
  // as illustrated in the following figure:
  //  mapping (picture)
  //
  // Thus, the offset of a pixel at coordinates x and y, is equal to y * width + x,
  // where width is the number of pixels in a single row. Although there are other mappings used in practice,
  // we will restrict to this simple mapping throughout the exercise.
  //
  // To ensure that the value of the x and y coordinates are confined to the dimensions of the image,
  // namely width and height, occasionally we have to call the clamp method:
  //  def clamp(v: Int, min: Int, max: Int): Int
  //
  // Finally, we will use the task construct to start parallel computations.
  // Every task construct invocation returns an object on which we can call the join method.
  // Calling the join method blocks the execution of the program until the parallel computation ends.
  // Below, we calculate the expression 1 + 1 in parallel to the main program:
  //  val computation = task {
  //   val result = 1 + 1
  //   println("Done!")
  //   result
  //  }
  //  println("About to wait for some heavy calculation...")
  //  computation.join()
  //
  // We now have everything we need to start implementing the parallel box filter.
  //
  // ScalaShop
  // Now we have everything we need to start ScalaShop:
  //  runMain scalashop.ScalaShop
  //
  // Change the blur implementation, parallelism level and blur radius,
  // and study the effect your changes have on performance.
  //
  //  * Which of the two blur implementations is faster?
  //  * For which values of the radius parameter is the difference most significant?
  //  * Why ?
  //  * What if we split the image into rectangles? Will this be faster?
  //
  // HACK git hub dot com
  // /TomLous/coursera-parallel-programming-scala
  // /blob/master/src/main/scala/scalashop/VerticalBoxBlur.scala

  var imagePath: Option[String] = None

  var image = loadScalaImage()

  override def getPreferredSize = {
    new Dimension(image.width, image.height)
  }

  private def loadScalaImage(): Img = {
    val stream = this.getClass.getResourceAsStream("/scalashop/scala.jpg")
    try {
      loadImage(stream)
    } finally {
      stream.close()
    }
  }

  private def loadFileImage(path: String): Img = {
    val stream = new FileInputStream(path)
    try {
      loadImage(stream)
    } finally {
      stream.close()
    }
  }

  private def loadImage(inputStream: InputStream): Img = {
    val bufferedImage = ImageIO.read(inputStream)
    val width = bufferedImage.getWidth
    val height = bufferedImage.getHeight
    val img = new Img(width, height)
    for (x <- 0 until width; y <- 0 until height) img(x, y) = bufferedImage.getRGB(x, y)
    img
  }

  def reload(): Unit = {
    image = imagePath match {
      case Some(path) => loadFileImage(path)
      case None => loadScalaImage()
    }
    repaint()
  }

  def loadFile(path: String): Unit = {
    imagePath = Some(path)
    reload()
  }

  def applyFilter(filterName: String, numTasks: Int, radius: Int) {
    val dst = new Img(image.width, image.height)
    filterName match {
      case "horizontal-box-blur" =>
        HorizontalBoxBlur.parBlur(image, dst, numTasks, radius)
      case "vertical-box-blur" =>
        VerticalBoxBlur.parBlur(image, dst, numTasks, radius)
      case "" =>
    }
    image = dst
    repaint()
  }

  override def paintComponent(gcan: Graphics) = {
    super.paintComponent(gcan)

    val width = image.width
    val height = image.height
    val bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    for (x <- 0 until width; y <- 0 until height) bufferedImage.setRGB(x, y, image(x, y))

    gcan.drawImage(bufferedImage, 0, 0, null)
  }

}
