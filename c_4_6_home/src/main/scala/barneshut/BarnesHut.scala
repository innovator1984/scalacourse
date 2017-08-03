package barneshut

import java.awt._
import java.awt.event._
import javax.swing._
import javax.swing.event._
import scala.collection.parallel._
import scala.collection.par._
import scala.collection.mutable.ArrayBuffer
import scala.reflect.ClassTag

object BarnesHut {
  // TODO 1.A Common
  //
  // Barnes-Hut Simulation
  //
  // In this assignment, you will implement the parallel Barnes-Hut algorithm for N-body simulation.
  // N-body simulation is a simulation of a system of N particles that interact with physical forces,
  // such as gravity or electrostatic force. Given initial positions and velocities of all the particles (or bodies),
  // the N-body simulation computes the new positions and velocities of the particles as the time progresses.
  // It does so by dividing time into discrete short intervals,
  // and computing the positions of the particles after each interval.
  //
  // Before we study the Barnes-Hut algorithm for the N-body simulation problem,
  // we will focus on a simpler algorithm -- the direct sum N-body algorithm.
  // The direct sum algorithm consists of multiple iterations,
  // each of which performs the following steps for each particle:
  //
  // 1. The particle position is updated according to its current velocity (delta is a short time period).
  // x' = x + v_x * delta y' = y + v_y * delta
  // 2. The net force on the particle is computed by adding the individual forces from all the other particles.
  // F_x = F_1x + F_2x + F_3x + ... + F_Nx F_y = F_1y + F_2y + F_3y + ... + F_Ny
  // 3. The particle velocity is updated according to the net force on that particle.
  // v_x' = v_x + F_x / mass * delta v_y' = v_y + F_y / mass * delta
  //
  // In this exercise, we will assume that the force between particles is the gravitational force
  // from classical mechanics. Let's recall the formula for the gravitational force between two stellar bodies:
  //
  // F = G * (m1 * m2) / distance^2
  //
  // Above, F is the absolute value of the gravitational force, m1 and m2 are the masses of the two bodies,
  // and r is the distance between them. G is the gravitational constant.
  //
  // For each particle, the net force is computed by summing the components of individual forces
  // from all other particles, as shown in the following figure:
  //
  //    net-force (picture)
  //
  // The direct sum N-body algorithm is very simple, but also inefficient.
  // Since we need to update N particles, and compute N - 1 force contributions for each of those particles,
  // the overall complexity of an iteration step of this algorithm is O(N^2).
  // As the number of particles grows larger, the direct sum N-body algorithm becomes prohibitively expensive.
  //
  // The Barnes-Hut algorithm is an optimization of the direct sum N-body algorithm,
  // and is based on the following observation:
  //
  // If a cluster of bodies is sufficiently distant from a body A, the net force on A from
  // that cluster can be approximated with one big body with the mass of all the bodies in the cluster, positioned
  // at the center of mass of the cluster.
  //
  // This is illustrated in the following figure:
  //    observation (picture)
  //
  // To take advantage of this observation, the Barnes-Hut algorithm relies on a quadtree -- a data structure
  // that divides the space into cells, and answers queries such as 'What is the total mass and the center
  // of mass of all the particles in this cell?'. The following figure shows an example of a quadtree for 6 bodies:
  //    quadtree (picture)
  //
  // Above, the total force from the bodies B, C, D and E on the body A can be approximated by a single body
  // with mass equal to the sum of masses B, C, D and E, positioned at the center of mass of bodies B, C, D and E.
  // The center of mass (massX, massY) is computed as follows:
  //
  //    mass = m_B + m_C + m_D + m_E
  //    massX = (m_B * x_B + m_C * x_C + m_D * x_D + m_E * x_E) / mass
  //    massY = (m_B * y_B + m_C * y_C + m_D * y_D + m_E * y_E) / mass
  //
  // An iteration of the Barnes-Hut algorithm is composed of the following steps:
  // 1. Construct the quadtree for the current arrangement of the bodies.
  // 2. Determine the boundaries, i.e. the square into which all bodies fit.
  // 3. Construct a quadtree that covers the boundaries and contains all the bodies.
  // 4. Update the bodies -- for each body:
  // 5. Update the body position according to its current velocity.
  // 6. Using the quadtree, compute the net force on the body by adding the individual forces from all the other bodies.
  // 7. Update the velocity according to the net force on that body.
  //
  // It turns out that, for most spatial distribution of bodies, the expected number of cells
  // that contribute to the net force on a body is log n, so the overall complexity
  // of the Barnes-Hut algorithm is O(n log n).
  //
  // Now that we covered all the necessary theory, let's finally dig into the implementation! You will implement:
  //  * a quadtree and its combiner data structure
  //  * an operation that computes the total force on a body using the quadtree
  //  * a simulation step of the Barnes-Hut algorithm
  //
  // Since this assignment consists of multiple components, we will follow the principles of test-driven
  // development and test each component separately, before moving on to the next component.
  // That way, if anything goes wrong, we will more precisely know where the error is.
  // It is always better to detect errors sooner, rather than later.
  //
  //
  // TODO 7.A Building the Quadtree
  // Next, we need to build a Quad tree from the sequence of bodies. We will first implement
  // the computeSectorMatrix method to get the SectorMatrix:
  //    def computeSectorMatrix(bodies: Seq[Body], boundaries: Boundaries): SectorMatrix
  //
  // Hint: aggregate the SectorMatrix from the sequence of bodies, the same way it was used for boundaries.
  // Use the SECTOR_PRECISION constant when creating a new SectorMatrix.
  //
  // Test that these methods work correctly before proceeding!
  //
  // Eliminating Outliers
  //
  // During the execution of the Barnes-Hut algorithm, some of the bodies tend to move far away
  // from most of the other bodies. There are many ways to deal with such outliers, but to keep things simple,
  // we will eliminate bodies that move too fast and too far away.
  //
  // We will not go into details of how this works, but if you'd like to know more, you can try to understand
  // how the eliminateOutliers method works.
  //
  // Updating Bodies
  //
  // The updateBodies method uses the quadtree to map each body
  // from the previous iteration of the algorithm to a new iteration:
  //    def updateBodies(bodies: Seq[Body], quad: Quad): Seq[Body]
  //
  // Recall that we already implemented the updated method which updates a single body.
  //
  // Running Barnes-Hut
  // At last, the parallel Barnes-Hut algorithm is implemented. Note that, despite all the work,
  // we kept our Barnes-Hut algorithm implementation simple and avoided the details that a more realistic
  // implementation must address. In particular:
  //  * we represented each body as a single point in space
  //  * we restricted the simulation to two-dimensional space
  //  * we ignored close encounter effects, such as body collision or tearing
  //  * we ignored any relativistic effects, and assume classical mechanics
  //  * we ignored errors induced by floating point computations
  // You can now run it as follows:
  //     runMain barneshut.BarnesHut
  //
  // To visualize the quadtree, press the Show quad button, and then hit the Start/Pause button.
  //
  // Play with the parallelism level and the number of bodies, and observe the average speedups
  // in the lower right corner. Then sit back, and enjoy the show!
  //
  // HACK git hub dot com
  // /hsleep/Coursera/blob/master/parprog1/barneshut/src/main/scala/barneshut/BarnesHut.scala
  //

  val model = new SimulationModel

  var simulator: Simulator = _

  def initialize(parallelismLevel: Int, pattern: String, nbodies: Int) {
    model.initialize(parallelismLevel, pattern, nbodies)
    model.timeStats.clear()
    simulator = new Simulator(model.taskSupport, model.timeStats)
  }

  class BarnesHutFrame extends JFrame("Barnes-Hut") {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    setSize(1024, 600)
    setLayout(new BorderLayout)

    val rightpanel = new JPanel
    rightpanel.setBorder(BorderFactory.createEtchedBorder(border.EtchedBorder.LOWERED))
    rightpanel.setLayout(new BorderLayout)
    add(rightpanel, BorderLayout.EAST)
    
    val controls = new JPanel
    controls.setLayout(new GridLayout(0, 2))
    rightpanel.add(controls, BorderLayout.NORTH)
    
    val parallelismLabel = new JLabel("Parallelism")
    controls.add(parallelismLabel)
    
    val items = (1 to Runtime.getRuntime.availableProcessors).map(_.toString).toArray
    val parcombo = new JComboBox[String](items)
    parcombo.setSelectedIndex(items.length - 1)
    parcombo.addActionListener(new ActionListener {
      def actionPerformed(e: ActionEvent) = {
        initialize(getParallelism, "two-galaxies", getTotalBodies)
        canvas.repaint()
      }
    })
    controls.add(parcombo)
    
    val bodiesLabel = new JLabel("Total bodies")
    controls.add(bodiesLabel)
    
    val bodiesSpinner = new JSpinner(new SpinnerNumberModel(25000, 32, 1000000, 1000))
    bodiesSpinner.addChangeListener(new ChangeListener {
      def stateChanged(e: ChangeEvent) = {
        if (frame != null) {
          initialize(getParallelism, "two-galaxies", getTotalBodies)
          canvas.repaint()
        }
      }
    })
    controls.add(bodiesSpinner)
    
    val stepbutton = new JButton("Step")
    stepbutton.addActionListener(new ActionListener {
      def actionPerformed(e: ActionEvent) {
        stepThroughSimulation()
      }
    })
    controls.add(stepbutton)
    
    val startButton = new JToggleButton("Start/Pause")
    val startTimer = new javax.swing.Timer(0, new ActionListener {
      def actionPerformed(e: ActionEvent) {
        stepThroughSimulation()
      }
    })
    startButton.addActionListener(new ActionListener {
      def actionPerformed(e: ActionEvent) {
        if (startButton.isSelected) startTimer.start()
        else startTimer.stop()
      }
    })
    controls.add(startButton)
    
    val quadcheckbox = new JToggleButton("Show quad")
    quadcheckbox.addActionListener(new ActionListener {
      def actionPerformed(e: ActionEvent) {
        model.shouldRenderQuad = quadcheckbox.isSelected
        repaint()
      }
    })
    controls.add(quadcheckbox)

    val clearButton = new JButton("Restart")
    clearButton.addActionListener(new ActionListener {
      def actionPerformed(e: ActionEvent) {
        initialize(getParallelism, "two-galaxies", getTotalBodies)
      }
    })
    controls.add(clearButton)

    val info = new JTextArea("   ")
    info.setBorder(BorderFactory.createLoweredBevelBorder)
    rightpanel.add(info, BorderLayout.SOUTH)

    val canvas = new SimulationCanvas(model)
    add(canvas, BorderLayout.CENTER)
    setVisible(true)

    def updateInformationBox() {
      val text = model.timeStats.toString
      frame.info.setText("--- Statistics: ---\n" + text)
    }

    def stepThroughSimulation() {
      SwingUtilities.invokeLater(new Runnable {
        def run() = {
          val (bodies, quad) = simulator.step(model.bodies)
          model.bodies = bodies
          model.quad = quad
          updateInformationBox()
          repaint()
        }
      })
    }

    def getParallelism = {
      val selidx = parcombo.getSelectedIndex
      parcombo.getItemAt(selidx).toInt
    }

    def getTotalBodies = bodiesSpinner.getValue.asInstanceOf[Int]

    initialize(getParallelism, "two-galaxies", getTotalBodies)
  }

  try {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
  } catch {
    case _: Exception => println("Cannot set look and feel, using the default one.")
  }

  val frame = new BarnesHutFrame

  def main(args: Array[String]) {
    frame.repaint()
  }

}
