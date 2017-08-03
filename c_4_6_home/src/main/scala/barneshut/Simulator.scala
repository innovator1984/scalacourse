package barneshut

import java.awt._
import java.awt.event._
import javax.swing._
import javax.swing.event._
import scala.collection.parallel.TaskSupport
import scala.collection.parallel.Combiner
import scala.collection.parallel.mutable.ParHashSet
import common._

class Simulator(val taskSupport: TaskSupport, val timeStats: TimeStatistics) {

  def updateBoundaries(boundaries: Boundaries, body: Body): Boundaries = {
    // TODO 6.D Computing the Scene Boundaries
    // So, we need the updateBoundaries method:
    //    def updateBoundaries(boundaries: Boundaries, body: Body): Boundaries
    //
    // Given an existing boundaries object and a body, the updateBoundaries updates the minX, minY, maxX and maxY values
    // so that the boundaries include the body.
    //
    // QQQ
    boundaries.minX = math.min(boundaries.minX, body.x)
    boundaries.minY = math.min(boundaries.minY, body.y)
    boundaries.maxX = math.max(boundaries.maxX, body.x)
    boundaries.maxY = math.max(boundaries.maxY, body.y)
    boundaries
  }

  def mergeBoundaries(a: Boundaries, b: Boundaries): Boundaries = {
    // TODO 6.E Computing the Scene Boundaries
    // Next, the mergeBoundaries method creates a new Boundaries object, which represents the smallest rectangle
    // that contains both the input boundaries:
    //
    //    def mergeBoundaries(a: Boundaries, b: Boundaries): Boundaries
    //
    // Question: Is mergeBoundaries associative? Is it commutative? Does it need to be commutative?
    // Implement these two methods, and test that they work correctly!
    // QQQ
    a.minX = math.min(a.minX, b.minX)
    a.minY = math.min(a.minY, b.minY)
    a.maxX = math.max(a.maxX, b.maxX)
    a.maxY = math.max(a.maxY, b.maxY)
    a
  }

  def computeBoundaries(bodies: Seq[Body]): Boundaries = timeStats.timed("boundaries") {
    // TODO 6.A Computing the Scene Boundaries
    //
    // First, we must compute the boundaries of all the bodies in the scene.
    // Since bodies move and the boundaries dynamically change, we must do this in every iteration of the algorithm.
    // The computeBoundaries method is already implemented -- it uses the aggregate combinator on the sequence
    // of bodies to compute the boundaries:
    //
    //    def computeBoundaries(bodies: Seq[Body]): Boundaries = {
    //      val parBodies = bodies.par
    //      parBodies.tasksupport = taskSupport
    //      parBodies.aggregate(new Boundaries)(updateBoundaries, mergeBoundaries)
    //    }
    //
    // How does this work? The aggregate method divides the input sequence into a number of chunks.
    // For each of the chunks, it uses the new Boundaries expression to create the accumulation value,
    // and then folds the values in that chunk calling updateBoundaries on each body, in the same way
    // a foldLeft operation would. Finally, aggregate combines the results of different chunks
    // using a reduction tree and mergeBoundaries.
    val parBodies = bodies.par
    parBodies.tasksupport = taskSupport
    parBodies.aggregate(new Boundaries)(updateBoundaries, mergeBoundaries)
  }

  def computeSectorMatrix(bodies: Seq[Body], boundaries: Boundaries): SectorMatrix = timeStats.timed("matrix") {
    // TODO 6.B Computing the Scene Boundaries
    // QQQ
    // val parBodies = bodies.par
    // parBodies.tasksupport = taskSupport
    val parBodies = bodies.par
    parBodies.tasksupport = taskSupport
    parBodies.aggregate(new SectorMatrix(boundaries, SECTOR_PRECISION))((agg, b) => agg += b, (a, b) => a.combine(b))
  }

  def computeQuad(sectorMatrix: SectorMatrix): Quad = timeStats.timed("quad") {
    sectorMatrix.toQuad(taskSupport.parallelismLevel)
  }

  def updateBodies(bodies: Seq[Body], quad: Quad): Seq[Body] = timeStats.timed("update") {
    // TODO 6.C Computing the Scene Boundaries
    // val parBodies = bodies.par
    // parBodies.tasksupport = taskSupport
    // QQQ
    val parBodies = bodies.par
    parBodies.tasksupport = taskSupport
    parBodies.map(b => b.updated(quad)).seq
  }

  def eliminateOutliers(bodies: Seq[Body], sectorMatrix: SectorMatrix, quad: Quad): Seq[Body] = timeStats.timed("eliminate") {
    def isOutlier(b: Body): Boolean = {
      val dx = quad.massX - b.x
      val dy = quad.massY - b.y
      val d = math.sqrt(dx * dx + dy * dy)
      // object is far away from the center of the mass
      if (d > eliminationThreshold * sectorMatrix.boundaries.size) {
        val nx = dx / d
        val ny = dy / d
        val relativeSpeed = b.xspeed * nx + b.yspeed * ny
        // object is moving away from the center of the mass
        if (relativeSpeed < 0) {
          val escapeSpeed = math.sqrt(2 * gee * quad.mass / d)
          // object has the espace velocity
          -relativeSpeed > 2 * escapeSpeed
        } else false
      } else false
    }

    def outliersInSector(x: Int, y: Int): Combiner[Body, ParHashSet[Body]] = {
      val combiner = ParHashSet.newCombiner[Body]
      combiner ++= sectorMatrix(x, y).filter(isOutlier)
      combiner
    }

    val sectorPrecision = sectorMatrix.sectorPrecision
    val horizontalBorder = for (x <- 0 until sectorPrecision; y <- Seq(0, sectorPrecision - 1)) yield (x, y)
    val verticalBorder = for (y <- 1 until sectorPrecision - 1; x <- Seq(0, sectorPrecision - 1)) yield (x, y)
    val borderSectors = horizontalBorder ++ verticalBorder

    // compute the set of outliers
    val parBorderSectors = borderSectors.par
    parBorderSectors.tasksupport = taskSupport
    val outliers = parBorderSectors.map({ case (x, y) => outliersInSector(x, y) }).reduce(_ combine _).result

    // filter the bodies that are outliers
    val parBodies = bodies.par
    parBodies.filter(!outliers(_)).seq
  }

  def step(bodies: Seq[Body]): (Seq[Body], Quad) = {
    // TODO 5.A Implementing Barnes-Hut
    //
    // Now that we have all the right data structures ready and polished, implementing Barnes-Hut becomes a piece of cake.
    // Take a look at the file Simulator.scala, which contains the implementation of the Barnes-Hut simulator,
    // and in particular the step method. The step method represents one step in the simulation:
    //
    //     def step(bodies: Seq[Body]): (Seq[Body], Quad) = {
    //       // 1. compute boundaries
    //       val boundaries = computeBoundaries(bodies)
    //       // 2. compute sector matrix
    //       val sectorMatrix = computeSectorMatrix(bodies, boundaries)
    //       // 3. compute quadtree
    //       val quad = computeQuad(sectorMatrix)
    //       // 4. eliminate outliers
    //       val filteredBodies = eliminateOutliers(bodies, sectorMatrix, quad)
    //       // 5. update body velocities and positions
    //       val newBodies = updateBodies(filteredBodies, quad)
    //       (newBodies, quad)
    //     }
    // The pre-existing code in step nicely summarizes what this method does.
    //

    // 1. compute boundaries
    val boundaries = computeBoundaries(bodies)
    
    // 2. compute sector matrix
    val sectorMatrix = computeSectorMatrix(bodies, boundaries)

    // 3. compute quad tree
    val quad = computeQuad(sectorMatrix)
    
    // 4. eliminate outliers
    val filteredBodies = eliminateOutliers(bodies, sectorMatrix, quad)

    // 5. update body velocities and positions
    val newBodies = updateBodies(filteredBodies, quad)

    (newBodies, quad)
  }

}
