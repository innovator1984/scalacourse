import common._
import barneshut.conctrees._

package object barneshut {

  class Boundaries {

    // TODO 4.A The Sector Matrix
    //
    // The last data structure that we will implement is the sector matrix.
    // In this data structure, we will use the auxiliary class Boundaries,
    // which contains the minX, maxX, minY and maxY fields for the boundaries of the scene:
    //
    //    class Boundaries {
    //      var minX: Float
    //      var minY: Float
    //      var maxX: Float
    //      var maxY: Float
    //      def size = math.max(maxX - minX, maxY - minY)
    //    }
    //
    var minX = Float.MaxValue

    var minY = Float.MaxValue

    var maxX = Float.MinValue

    var maxY = Float.MinValue

    def width = maxX - minX

    def height = maxY - minY

    def size = math.max(width, height)

    def centerX = minX + width / 2

    def centerY = minY + height / 2

    override def toString = s"Boundaries($minX, $minY, $maxX, $maxY)"
  }

  sealed abstract class Quad {
    // TODO 2.A Data Structures
    // We will start by implementing the necessary data structures: the quadtree, the body data-type
    // and the sector matrix. You will find the stubs in the package.scala file of the barneshut package.
    //
    // Quadtree Data Structure
    // In this part of the assignment, we implement the quadtree data structure,
    // denoted with the abstract data-type Quad. Every Quad represents a square cell of space,
    // and can be one of the following node types:
    //  * an Empty node, which represents an empty quadtree
    //  * a Leaf node, which represents one or more bodies
    //  * a Fork node, which divides a spatial cell into four quadrants
    //
    // The definition of Quad is as follows:
    //    sealed abstract class Quad {
    //      def massX: Float
    //      def massY: Float
    //      def mass: Float
    //      def centerX: Float
    //      def centerY: Float
    //      def size: Float
    //      def total: Int
    //      def insert(b: Body): Quad
    //    }
    //
    // Here, massX and massY represent the center of mass of the bodies in the respective cell,
    // mass is the total mass of bodies in that cell, centerX and centerY are the coordinates of the center of the cell,
    // size is the length of the side of the cell, and total is the total number of bodies in the cell.
    //
    // Note that we consider the top left corner to be at coordinate (0, 0). We also consider the x axis to grow
    // to the right and the y axis to the bottom.
    //    cell (picture)
    //
    def massX: Float

    def massY: Float

    def mass: Float

    def centerX: Float

    def centerY: Float

    def size: Float

    def total: Int

    def insert(b: Body): Quad
  }

  case class Empty(centerX: Float, centerY: Float, size: Float) extends Quad {
    // TODO 2.C Data Structures
    // In this part of the exercise, you only need to know about body's position x and y.
    // Let's start by implementing the simplest Quad type -- the empty quadtree:
    //    case class Empty(centerX: Float, centerY: Float, size: Float) extends Quad
    // The center and the size of the Empty quadtree are specified in its constructor.
    // The Empty tree does not contain any bodies, so we specify that its center of mass is equal to its center.
    //
    // QQQ
    // def massX: Float = ???
    // def massY: Float = ???
    // def mass: Float = ???
    // def total: Int = ???
    // def insert(b: Body): Quad = ???
    def massX: Float = centerX
    def massY: Float = centerY
    def mass: Float = 0
    def total: Int = 0
    def insert(b: Body): Quad = Leaf(centerX, centerY, size, Seq(b))
  }

  case class Fork(
    nw: Quad, ne: Quad, sw: Quad, se: Quad
  ) extends Quad {
    // TODO 2.D Data Structures
    // Next, let's implement the Fork quadtree:
    //    case class Fork(nw: Quad, ne: Quad, sw: Quad, se: Quad) extends Quad
    //
    // This node is specified by four child quadtrees nw, ne, sw and se, in the northwest, northeast, southwest
    // and southeast quadrant, respectively.
    // The northwest is located on the top left, northeast on the top right, southwest on the bottom left
    // and southeast on the bottom right.
    //
    // The constructor assumes that the children nodes that represent four adjacent cells of the same size
    // and adjacent to each other, as in the earlier figure. The center of the Fork quadtree is then specified by,
    // say, the lower right corner of the quadtree nw.
    // If the Fork quadtree is empty, the center of mass coincides with the center.
    //
    // Inserting into a Fork is recursive -- it updates the respective child and creates a new Fork.
    // QQQ
    // val centerX: Float = ???
    // val centerY: Float = ???
    // val size: Float = ???
    // val mass: Float = ???
    // val massX: Float = ???
    // val massY: Float = ???
    // val total: Int = ???
    val quads = Seq(nw, ne, sw, se)
    val centerX: Float = (nw.centerX + ne.centerX) / 2
    val centerY: Float = (nw.centerY + sw.centerY) / 2
    val size: Float = nw.size + ne.size
    val mass: Float = quads.map(_.mass).sum
    val massX: Float = if (mass == 0) centerX else quads.map(q => q.mass * q.massX).sum / mass
    val massY: Float = if (mass == 0) centerY else quads.map(q => q.mass * q.massY).sum / mass
    val total: Int = quads.map(_.total).sum

    def insert(b: Body): Fork = {
      // TODO 2.E Data Structures
      // Next, let's implement the Fork quadtree:
      //    case class Fork(nw: Quad, ne: Quad, sw: Quad, se: Quad) extends Quad
      // QQQ
      val dx = b.x > centerX
      val dy = b.y > centerY
      (dx, dy) match {
        case (false, true) => Fork(nw.insert(b), ne, sw, se)
        case (true, false) => Fork(nw, ne.insert(b), sw, se)
        case (false, false) => Fork(nw, ne, sw.insert(b), se)
        case (true, true) => Fork(nw, ne, sw, se.insert(b))
      }
    }
  }

  case class Leaf(centerX: Float, centerY: Float, size: Float, bodies: Seq[Body])
  extends Quad {
    // TODO 2.F Data Structures
    // Finally, the Leaf quadtree represents one or more bodies:
    //    case class Leaf(centerX: Float, centerY: Float, size: Float, bodies: Seq[Body]) extends Quad
    //
    // If the size of a Leaf is greater than a predefined constant minimumSize, inserting an additonal body
    // into that Leaf quadtree creates a Fork quadtree with empty children, and adds all the bodies
    // into that Fork (including the new body).
    // Otherwise, inserting creates another Leaf with all the existing bodies and the new one.
    //
    // val (mass, massX, massY) =
      // TODO 2.G Data Structures
      // Finally, the Leaf quadtree represents one or more bodies:
      //    case class Leaf(centerX: Float, centerY: Float, size: Float, bodies: Seq[Body]) extends Quad
      // QQQ
      // (??? : Float, ??? : Float, ??? : Float)

      val mass =  bodies.map(_.mass).sum
      val massX =  bodies.map(b => b.mass * b.x).sum / mass
      val massY = bodies.map(b => b.mass * b.y).sum / mass

    val total: Int = {
      // TODO 2.H Data Structures
      // Finally, the Leaf quadtree represents one or more bodies:
      //    case class Leaf(centerX: Float, centerY: Float, size: Float, bodies: Seq[Body]) extends Quad
      // QQQ
      bodies.length
    }

    def insert(b: Body): Quad = {
      // TODO 2.I Data Structures
      // Finally, the Leaf quadtree represents one or more bodies:
      //    case class Leaf(centerX: Float, centerY: Float, size: Float, bodies: Seq[Body]) extends Quad
      // QQQ
      val newBodies = bodies :+ b
      if (size <= minimumSize) copy(bodies = newBodies)
      else {
        val quadSize = size / 4
        val halfSize = size / 2
        val nw = Empty(centerX - quadSize, centerY - quadSize, halfSize)
        val ne = Empty(centerX + quadSize, centerY - quadSize, halfSize)
        val sw = Empty(centerX - quadSize, centerY + quadSize, halfSize)
        val se = Empty(centerX + quadSize, centerY + quadSize, halfSize)
        newBodies.foldLeft(Fork(nw, ne, sw, se))((agg, body) => agg.insert(body))
      }
    }
  }

  def minimumSize = 0.00001f

  def gee: Float = 100.0f

  def delta: Float = 0.01f

  def theta = 0.5f

  def eliminationThreshold = 0.5f

  def force(m1: Float, m2: Float, dist: Float): Float = gee * m1 * m2 / (dist * dist)

  def distance(x0: Float, y0: Float, x1: Float, y1: Float): Float = {
    math.sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0)).toFloat
  }

  class Body(val mass: Float, val x: Float, val y: Float, val xspeed: Float, val yspeed: Float) {
    // TODO 2.B Data Structures
    // The method insert creates a new quadtree which additionally contains the body b, and covers the same area
    // in space as the original quadtree. Quadtree is an immutable data structure -- insert does not modify
    // the existing Quad object. Note that Body has the following signature:
    //    class Body(val mass: Float, val x: Float, val y: Float, val xspeed: Float, val yspeed: Float)
    //

    def updated(quad: Quad): Body = {
      // TODO 3.A The Body Data-Type
      //
      // Next, we can implement the Body data-type:
      //    class Body(val mass: Float, val x: Float, val y: Float, val xspeed: Float, val yspeed: Float) {
      //      def updated(quad: Quad): Body = ???
      //    }
      //
      // Here, xspeed and yspeed represent the velocity of the body, mass is its mass,
      // and x and y are the coordinates of the body.
      //
      // The most interesting method on the Body is updated -- it takes a quadtree
      // and returns the updated version of the Body:
      //    def updated(quad: Quad): Body
      //
      // This method is already half-completed for you -- you only need to implement its nested method traverse,
      // which goes through the quadtree and proceeds casewise:
      //  * empty quadtree does not affect the net force
      //  * each body in a leaf quadtree adds some net force
      //  * a fork quadtree that is sufficiently far away acts as a single point of mass
      //  * a fork quadtree that is not sufficiently far away must be recursively traversed
      // When are we allowed to approximate a cluster of bodies with a single point?
      // The heuristic that is used is that the size of the cell divided by the distance dist between
      // the center of mass and the particle is less than some constant theta:
      //    quad.size / dist < theta
      //
      // Hint: make sure you use the distance to compute distance between points, the theta value for the condition,
      // and addForce to add force contributions!
      //
      // Before proceeding, make sure to run tests against your Quad and Body implementations.
      //
      // QQQ
      // var netforcex = 0.0f
      // var netforcey = 0.0f

      // def addForce(thatMass: Float, thatMassX: Float, thatMassY: Float): Unit = {
      //   val dist = distance(thatMassX, thatMassY, x, y)
        /* If the distance is smaller than 1f, we enter the realm of close
         * body interactions. Since we do not model them in this simplistic
         * implementation, bodies at extreme proximities get a huge acceleration,
         * and are catapulted from each other's gravitational pull at extreme
         * velocities (something like this:
         * http://en.wikipedia.org/wiki/Interplanetary_spaceflight#Gravitational_slingshot).
         * To decrease the effect of this gravitational slingshot, as a very
         * simple approximation, we ignore gravity at extreme proximities.
         */
      //  if (dist > 1f) {
      //    val dforce = force(mass, thatMass, dist)
      //    val xn = (thatMassX - x) / dist
      //    val yn = (thatMassY - y) / dist
      //    val dforcex = dforce * xn
      //    val dforcey = dforce * yn
      //    netforcex += dforcex
      //    netforcey += dforcey
      //  }
      // }

      // def traverse(quad: Quad): Unit = (quad: Quad) match {
      //   case Empty(_, _, _) =>
      //     // no force
      //   case Leaf(_, _, _, bodies) =>
      //     // add force contribution of each body by calling addForce
      //   case Fork(nw, ne, sw, se) =>
      //     // see if node is far enough from the body,
      //     // or recursion is needed
      // }

      // traverse(quad)

      // val nx = x + xspeed * delta
      // val ny = y + yspeed * delta
      // val nxspeed = xspeed + netforcex / mass * delta
      // val nyspeed = yspeed + netforcey / mass * delta

      // new Body(mass, nx, ny, nxspeed, nyspeed)
      var netforcex = 0.0f
      var netforcey = 0.0f

      def addForce(thatMass: Float, thatMassX: Float, thatMassY: Float): Unit = {
        val dist = distance(thatMassX, thatMassY, x, y)
        if (dist > 1f) {
          val dforce = force(mass, thatMass, dist)
          val xn = (thatMassX - x) / dist
          val yn = (thatMassY - y) / dist
          val dforcex = dforce * xn
          val dforcey = dforce * yn
          netforcex += dforcex
          netforcey += dforcey
        }
      }
      def traverse(quad: Quad): Unit = {
        (quad: Quad) match {
          case Empty(_, _, _) => // println(quad)
          case Leaf(_, _, _, bodies) =>
            bodies.foreach(b => addForce(b.mass, b.x, b.y))
          case Fork(nw, ne, sw, se) =>
            val dist = distance(quad.massX, quad.massY, x, y)
            if (quad.size / dist < theta) {
              addForce(quad.mass, quad.massX, quad.massY)
            } else {
              Seq(nw, ne, sw, se).foreach {
                q => traverse(q)
              }
            }
        }
      }
      traverse(quad)

      val nx = x + xspeed * delta
      val ny = y + yspeed * delta
      val nxspeed = xspeed + netforcex / mass * delta
      val nyspeed = yspeed + netforcey / mass * delta
      new Body(mass, nx, ny, nxspeed, nyspeed)
    }


  }

  val SECTOR_PRECISION = 8

  class SectorMatrix(val boundaries: Boundaries, val sectorPrecision: Int) {
    // TODO 4.B The Sector Matrix
    // We will also rely on the ConcBuffer data structure, mentioned in the lecture:
    //
    //    class ConcBuffer[T]
    //
    // The ConcBuffer class comes with efficient +=, combine and foreach operations,
    // which add elements into the buffer, combine two buffers and traverse the buffer, respectively.
    // The sector matrix additionally has the toQuad method, which returns a quadtree
    // that contains all the elements previously added with the += method.
    // Recall from the lectures that this combination of methods make the ConcBuffer a combiner.
    //
    //    class SectorMatrix(val boundaries: Boundaries, val sectorPrecision: Int) {
    //      val sectorSize = boundaries.size / sectorPrecision
    //      val matrix = new Array[ConcBuffer[Body]](sectorPrecision * sectorPrecision)
    //      for (i <- 0 until matrix.length) matrix(i) = new ConcBuffer
    //      def apply(x: Int, y: Int) = matrix(y * sectorPrecision + x)
    //    }
    //
    // The sectorPrecision argument denotes the width and height of the matrix,
    // and each entry contains a ConcBuffer[Body] object. Effectively, the SectorMatrix
    // is a combiner -- it partitions the square region of space into sectorPrecision
    // times sectorPrecision buckets, called sectors.
    //
    //    sectormatrix (picture)
    //

    val sectorSize = boundaries.size / sectorPrecision
    val matrix = new Array[ConcBuffer[Body]](sectorPrecision * sectorPrecision)
    for (i <- 0 until matrix.length) matrix(i) = new ConcBuffer

    def +=(b: Body): SectorMatrix = {
      // TODO 4.C The Sector Matrix
      // Combiners such as the SectorMatrix are used in parallel programming to partition the results
      // into some intermediate form that is more amenable to parallelization.
      // Recall from the lecture that one of the ways to implement a combiner is by using
      // a bucket data structure -- we will do exactly that in this part of the exercise!
      // We will add three methods on the SectorMatrix that will make it a combiner. We start with the += method:
      //
      //    def +=(b: Body): SectorMatrix = {
      //      ???
      //      this
      //    }
      //
      // This method should use the body position, boundaries and sectorPrecision to determine
      // the sector into which the body should go into, and add the body into the corresponding ConcBuffer object.
      //
      // Importantly, if the Body lies outside of the Boundaries, it should be considered to be located
      // at the closest point within the Boundaries for the purpose of finding which ConcBuffer should hold the body.
      //
      // QQQ
      // this
      val x = math.min(math.max(boundaries.minX, b.x), boundaries.maxX)
      val y = math.min(math.max(boundaries.minY, b.y), boundaries.maxY)
      apply((x - boundaries.minX) / sectorSize toInt, (y - boundaries.minY) / sectorSize toInt) += b
      this
    }

    def apply(x: Int, y: Int) = matrix(y * sectorPrecision + x)

    def combine(that: SectorMatrix): SectorMatrix = {
      // TODO 4.D The Sector Matrix
      // Next, we implement the combine method, which takes another SectorMatrix, and creates a SectorMatrix
      // which contains the elements of both input SectorMatrix data structures:
      //
      //    def combine(that: SectorMatrix): SectorMatrix
      //
      // This method calls combine on the pair of ConcBuffers in this and that matrices to produce the ConcBuffer
      // for the resulting matrix. You can safely assume that combine will only be called on matrices of same dimensions,
      // boundaries and sector precision.
      //
      //    combine (picture)
      //
      // QQQ
      for (i <- 0 until matrix.length) matrix(i) = matrix(i).combine(that.matrix(i))
      this
    }

    def toQuad(parallelism: Int): Quad = {
      // TODO 4.E The Sector Matrix
      // The nice thing about the sector matrix is that a quadtree can be constructed in parallel for each sector.
      // Those little quadtrees can then be linked together. The toQuad method on the SectorMatrix does this:
      //
      //    def toQuad(parallelism: Int): Quad
      //
      // This method is already implemented -- you can examine it if you would like to know how it works.
      // Congratulations, you just implemented your first combiner! Before proceeding, make sure to run those unit tests.
      //

      def BALANCING_FACTOR = 4
      def quad(x: Int, y: Int, span: Int, achievedParallelism: Int): Quad = {
        if (span == 1) {
          val sectorSize = boundaries.size / sectorPrecision
          val centerX = boundaries.minX + x * sectorSize + sectorSize / 2
          val centerY = boundaries.minY + y * sectorSize + sectorSize / 2
          var emptyQuad: Quad = Empty(centerX, centerY, sectorSize)
          val sectorBodies = this(x, y)
          sectorBodies.foldLeft(emptyQuad)(_ insert _)
        } else {
          val nspan = span / 2
          val nAchievedParallelism = achievedParallelism * 4
          val (nw, ne, sw, se) =
            if (parallelism > 1 && achievedParallelism < parallelism * BALANCING_FACTOR) parallel(
              quad(x, y, nspan, nAchievedParallelism),
              quad(x + nspan, y, nspan, nAchievedParallelism),
              quad(x, y + nspan, nspan, nAchievedParallelism),
              quad(x + nspan, y + nspan, nspan, nAchievedParallelism)
            ) else (
              quad(x, y, nspan, nAchievedParallelism),
              quad(x + nspan, y, nspan, nAchievedParallelism),
              quad(x, y + nspan, nspan, nAchievedParallelism),
              quad(x + nspan, y + nspan, nspan, nAchievedParallelism)
            )
          Fork(nw, ne, sw, se)
        }
      }

      quad(0, 0, sectorPrecision, 1)
    }

    override def toString = s"SectorMatrix(#bodies: ${matrix.map(_.size).sum})"
  }

  class TimeStatistics {
    private val timeMap = collection.mutable.Map[String, (Double, Int)]()

    def clear() = timeMap.clear()

    def timed[T](title: String)(body: =>T): T = {
      var res: T = null.asInstanceOf[T]
      val totalTime = /*measure*/ {
        val startTime = System.currentTimeMillis()
        res = body
        (System.currentTimeMillis() - startTime)
      }

      timeMap.get(title) match {
        case Some((total, num)) => timeMap(title) = (total + totalTime, num + 1)
        case None => timeMap(title) = (0.0, 0)
      }

      println(s"$title: ${totalTime} ms; avg: ${timeMap(title)._1 / timeMap(title)._2}")
      res
    }

    override def toString = {
      timeMap map {
        case (k, (total, num)) => k + ": " + (total / num * 100).toInt / 100.0 + " ms"
      } mkString("\n")
    }
  }
}
