package scalashop

import org.scalameter._
import common._

object VerticalBoxBlurRunner {

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
      VerticalBoxBlur.blur(src, dst, 0, width, radius)
    }
    println(s"sequential blur time: $seqtime ms")

    val numTasks = 32
    val partime = standardConfig measure {
      VerticalBoxBlur.parBlur(src, dst, numTasks, radius)
    }
    println(s"fork/join blur time: $partime ms")
    println(s"speedup: ${seqtime / partime}")
  }

}

/** A simple, trivially parallelizable computation. */
object VerticalBoxBlur {

  /** Blurs the columns of the source image `src` into the destination image
   *  `dst`, starting with `from` and ending with `end` (non-inclusive).
   *
   *  Within each column, `blur` traverses the pixels by going from top to
   *  bottom.
   */
  def blur(src: Img, dst: Img, from: Int, end: Int, radius: Int): Unit = {
    // TODO implement this method using the `boxBlurKernel` method
    // TODO 3.A Vertical Stripping Box Blur
    // We can now implement the parallel box blur filter. Note that the boxBlurKernel method is relatively inexpensive.
    // Executing boxBlurKernel might be much faster than starting a parallel computation,
    // so having a separate parallel computation for the value of each pixel would be far too expensive.
    // For this reason, we want to batch together many boxBlurKernel calls,
    // and have a smaller number of parallel tasks. This is sometimes referred to as agglomeration,
    // and is present in many parallel algorithm implementations.
    //
    // There are many different ways we can do agglomeration for the parallel box blur.
    // One is to divide the image into a fixed number of equally wide vertical strips.
    // For each strip, we start a parallel task, and wait for their completion. Within each strip,
    // we traverse the pixels going from the top to the bottom of the image, as illustrated in the following figure:
    //  stripping (picture)
    //
    // We start by implementing the sequential blur method in the VerticalBoxBlur.scala source file,
    // which takes the source image src, the destination image dst,
    // the starting (included) and ending (excluded) x coordinates (i.e, column indices) of the strip,
    // called from and end, and the blur radius.
    // The blur method blurs the pixels from the src image and writes them to the dst image:
    //   def blur(src: Img, dst: Img, from: Int, end: Int, radius: Int): Unit
    //
    // The implementation of blur should rely on the previously defined boxBlurKernel.
    // QQQ
    for (
      x <- from until end;
      y <- 0 until src.height;
      if x >= 0 && x < src.width
    ) yield {
      dst.update(x, y, boxBlurKernel(src, x, y, radius))
    }
  }

  /** Blurs the columns of the source image in parallel using `numTasks` tasks.
   *
   *  Parallelization is done by stripping the source image `src` into
   *  `numTasks` separate strips, where each strip is composed of some number of
   *  columns.
   */
  def parBlur(src: Img, dst: Img, numTasks: Int, radius: Int): Unit = {
    // TODO implement using the `task` construct and the `blur` method
    // TODO 3.B Vertical Stripping Box Blur
    // Then, implement the parBlur method, which divides the image
    // into numTasks vertical strips and runs each task in parallel:
    //   def parBlur(src:Img, dst: Img, numTasks: Int, radius:Int): Unit
    // Use Scala ranges to create a list of splitting points (hint: use the by method on ranges).
    // Then use collection combinators on the list of splitting points to create a list of start and end tuples,
    // one for each strip (hint: use the zip and tail methods).
    // Finally, use the task construct to start a parallel task for each strip, and
    // then call join on each task to wait for its completion.
    // Run the VerticalBoxBlur program with the following sbt command:
    //    runMain scalashop.VerticalBoxBlurRunner
    // Change the number of tasks and the radius parameter. How does the performance change?
    // QQQ
    val colsPerTasks: Int = Math.max(src.width / numTasks, 1)
    val startPoints = Range(0, src.width) by colsPerTasks

    val tasks = startPoints.map(t => {
      task {
        blur(src, dst, t, t + colsPerTasks, radius)
      }
    })
    tasks.map(t => t.join())
  }

}
