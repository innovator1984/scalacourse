package scalashop

import org.scalameter._
import common._

object HorizontalBoxBlurRunner {

  val standardConfig = config(
    Key.exec.minWarmupRuns -> 5,
    Key.exec.maxWarmupRuns -> 10,
    Key.exec.benchRuns -> 10,
    Key.verbose -> true
  ) withWarmer(new Warmer.Default)

  def main(args: Array[String]): Unit = {
    val radius = 3
    val width = 1920
    val height = 1080
    val src = new Img(width, height)
    val dst = new Img(width, height)
    val seqtime = standardConfig measure {
      HorizontalBoxBlur.blur(src, dst, 0, height, radius)
    }
    println(s"sequential blur time: $seqtime ms")

    val numTasks = 32
    val partime = standardConfig measure {
      HorizontalBoxBlur.parBlur(src, dst, numTasks, radius)
    }
    println(s"fork/join blur time: $partime ms")
    println(s"speedup: ${seqtime / partime}")
  }
}


/** A simple, trivially parallelizable computation. */
object HorizontalBoxBlur {

  /** Blurs the rows of the source image `src` into the destination image `dst`,
   *  starting with `from` and ending with `end` (non-inclusive).
   *
   *  Within each row, `blur` traverses the pixels by going from left to right.
   */
  def blur(src: Img, dst: Img, from: Int, end: Int, radius: Int): Unit = {
    // TODO implement this method using the `boxBlurKernel` method
    // TODO 4.A Horizontal Stripping Box Blur
    // In this part of the exercise we will pick an alternative agglomeration for the box blur algorithm.
    // Instead of dividing the image into vertical strips, we will divide it into horizontal strips in a similar way:
    //  stripping (picture)
    //
    // We implement the two methods, blur and parBlur in a similar way as before:
    //  def blur(src: Img, dst: Img, from: Int, end: Int, radius: Int): Unit
    //  def parBlur(src:Img, dst:Img, numTasks: Int, radius: Int): Unit
    // QQQ

    for(
      x <- 0 until src.width;
      y <- from until end;
      if y >= 0 && y < src.height
    ) yield {
      dst.update(x, y, boxBlurKernel(src, x, y, radius))
    }
  }

  /** Blurs the rows of the source image in parallel using `numTasks` tasks.
   *
   *  Parallelization is done by stripping the source image `src` into
   *  `numTasks` separate strips, where each strip is composed of some number of
   *  rows.
   */
  def parBlur(src: Img, dst: Img, numTasks: Int, radius: Int): Unit = {
    // TODO implement using the `task` construct and the `blur` method
    // TODO 4.B Horizontal Stripping Box Blur
    // We implement the two methods, blur and parBlur in a similar way as before:
    //  def blur(src: Img, dst: Img, from: Int, end: Int, radius: Int): Unit
    //  def parBlur(src:Img, dst:Img, numTasks: Int, radius: Int): Unit
    //
    // Note that the arguments from (included) and end (excluded) this time denote the the values of the y coordinate
    // (i.e, row indices), and that we traverse the pixels left-to-right within each strip.
    // You can now run the HorizontalBoxBlur program with
    //   runMain scalashop.HorizontalBoxBlurRunner
    //
    // If you implemented the two blur versions correctly, you should observe
    // that the horizontal stripping is slightly faster. This is because the pixel traversal order visits
    // the pixels which are closer together in memory (remember the mapping between the pixel coordinates
    // and the memory addresses). As a result, each core can reuse some of the pixels
    // that it fetched from the memory during the previous invocation of boxBlurKernel.
    // The processor cores spend less time fetching pixels from memory, and lower the pressure on the memory bus.
    // QQQ
    val rowsPerTasks = Math.max(src.height / numTasks, 1)
    val startPoints = Range(0, src.height) by rowsPerTasks

    val tasks = startPoints.map(t => {
      task {
        blur(src, dst, t, t + rowsPerTasks, radius)
      }
    })

    tasks.map(t => t.join())
  }

}
