package stackoverflow

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD
import annotation.tailrec
import scala.reflect.ClassTag

/** A raw stackoverflow posting, either a question or an answer */
case class Posting(
                   postingType: Int,
                   id: Int,
                   acceptedAnswer: Option[Int],
                   parentId: Option[Int],
                   score: Int,
                   tags: Option[String]
                  ) extends Serializable


/** The main class */
object StackOverflow extends StackOverflow {

  @transient lazy val conf: SparkConf = new SparkConf().setMaster("local").setAppName("StackOverflow")
  @transient lazy val sc: SparkContext = new SparkContext(conf)

  /** Main function */
  def main(args: Array[String]): Unit = {

    val lines   = sc.textFile("src/main/resources/stackoverflow/stackoverflow.csv")
    val raw     = rawPostings(lines)
    val grouped = groupedPostings(raw)
    val scored  = scoredPostings(grouped)
    val vectors = vectorPostings(scored)
//    assert(vectors.count() == 2121822, "Incorrect number of vectors: " + vectors.count())

    val means   = kmeans(sampleVectors(vectors), vectors, debug = true)
    val results = clusterResults(means, vectors)
    printResults(results)
  }
}


/** The parsing and kmeans methods */
class StackOverflow extends Serializable {

  /** Languages */
  val langs =
    List(
      "JavaScript", "Java", "PHP", "Python", "C#", "C++", "Ruby", "CSS",
      "Objective-C", "Perl", "Scala", "Haskell", "MATLAB", "Clojure", "Groovy")

  /** K-means parameter: How "far apart" languages should be for the kmeans algorithm? */
  def langSpread = 50000
  assert(langSpread > 0, "If langSpread is zero we can't recover the language from the input data!")

  /** K-means parameter: Number of clusters */
  def kmeansKernels = 45

  /** K-means parameter: Convergence criteria */
  def kmeansEta: Double = 20.0D

  /** K-means parameter: Maximum iterations */
  def kmeansMaxIterations = 120


  //
  //
  // Parsing utilities:
  //
  //

  /** Load postings from the given file */
  def rawPostings(lines: RDD[String]): RDD[Posting] = {
    // TODO 1.A The Data
    // raw: the raw Posting entries for each line
    lines.map(line => {
      val arr = line.split(",")
      Posting(postingType = arr(0).toInt,
        id = arr(1).toInt,
        acceptedAnswer = if (arr(2) == "") None else Some(arr(2).toInt),
        parentId = if (arr(3) == "") None else Some(arr(3).toInt),
        score = arr(4).toInt,
        tags = if (arr.length >= 6) Some(arr(5).intern()) else None)
    })
  }


  /** Group the questions and answers together */
  def groupedPostings(postings: RDD[Posting]): RDD[(Int, Iterable[(Posting, Posting)])] = {
    // TODO 2.A Data processing
    // The first method you will have to implement
    // In the raw variable we have simple postings, either questions or answers,
    // but in order to use the data we need to assemble them together.
    // Questions are identified using a "postTypeId" == 1.
    // Answers to a question with "id" == QID have (a) "postTypeId" == 2 and (b) "parentId" == QID.
    // Ideally, we want to obtain an RDD with the pairs of (Question, Iterable[Answer]).
    // However, grouping on the question directly is expensive (can you imagine why?),
    // so a better alternative is to match on the QID, thus producing an RDD[(QID, Iterable[(Question, Answer))].
    //
    // To obtain this, in the groupedPostings method, first filter the questions and answers
    // separately and then prepare them for a join operation
    // by extracting the QID value in the first element of a tuple.
    // Then, use one of the join operations (which one?) to obtain an RDD[(QID, (Question, Answer))].
    // Then, the last step is to obtain an RDD[(QID, Iterable[(Question, Answer)])].
    // How can you do that, what method do you use to group by the key of a pair RDD?

    // CHEAT git hub dot com
    // /kazbek-d/scala-courses/
    // blob/839c394fba7106640e43c58b92125b9204823dde/
    // coursera/scala-spark-big-data/stackoverflow/src/main/scala/stackoverflow/StackOverflow.scala
    val questions = postings.filter(p => p.postingType == 1).map(p => (p.id, p))
    val answers = postings.filter(p => p.postingType == 2).map(p => (p.parentId.get, p))
    val joined = questions.join(answers)
    val result = joined.groupBy(x => x._1).mapValues(x => x.map(y => y._2))
    result
  }


  /** Compute the maximum score for each posting */
  def scoredPostings(grouped: RDD[(Int, Iterable[(Posting, Posting)])]): RDD[(Posting, Int)] = {

    def answerHighScore(as: Array[Posting]): Int = {
      var highScore = 0
          var i = 0
          while (i < as.length) {
            val score = as(i).score
                if (score > highScore)
                  highScore = score
                  i += 1
          }
      highScore
    }
    // TODO 3.A Computing Scores
    // Second, implement the scoredPostings method, which should return an RDD
    // containing pairs of (a) questions and (b) the score of the answer
    // with the highest score (note: this does not have to be the answer marked as "acceptedAnswer"!).
    // The type of this scored RDD is:
    //     val scored: RDD[(Posting, Int)] = ???
    grouped.map({
      case(_, xs) => (
        xs.head._1,
        answerHighScore(xs.filter(x => x._2.acceptedAnswer.isEmpty).map(x => x._2).toArray)
      )
    })
  }


  /** Compute the vectors for the kmeans */
  def vectorPostings(scored: RDD[(Posting, Int)]): RDD[(Int, Int)] = {
    /** Return optional index of first language that occurs in `tags`. */
    def firstLangInTag(tag: Option[String], ls: List[String]): Option[Int] = {
      if (tag.isEmpty) None
      else if (ls.isEmpty) None
      else if (tag.get == ls.head) Some(0) // index: 0
      else {
        val tmp = firstLangInTag(tag, ls.tail)
        tmp match {
          case None => None
          case Some(i) => Some(i + 1) // index i in ls.tail => index i+1
        }
      }
    }
    // TODO 4.A Creating vectors for clustering
    // Next, we prepare the input for the clustering algorithm.
    // For this, we transform the scored RDD into a vectors RDD containing the vectors to be clustered.
    // In our case, the vectors should be pairs with two components (in the listed order!):
    // *** Index of the language (in the langs list) multiplied by the langSpread factor.
    // *** The highest answer score (computed above).
    // The langSpread factor is provided (set to 50000).
    // Basically, it makes sure posts about different programming languages have at least
    // distance 50000 using the distance measure provided by the euclideanDist function.
    // You will learn later what this distance means and why it is set to this value.
    // The type of the vectors RDD is as follows:
    //     val vectors: RDD[(Int, Int)] = ???
    scored
      .map(scored => (firstLangInTag(scored._1.tags, langs).getOrElse(-1) * langSpread, scored._2))
      .filter(_._1 >= 0)
      .sortBy(_._1)
  }


  /** Sample the vectors */
  def sampleVectors(vectors: RDD[(Int, Int)]): Array[(Int, Int)] = {

    assert(kmeansKernels % langs.length == 0, "kmeansKernels should be a multiple of the number of languages studied.")
    val perLang = kmeansKernels / langs.length

    // http://en.wikipedia.org/wiki/Reservoir_sampling
    def reservoirSampling(lang: Int, iter: Iterator[Int], size: Int): Array[Int] = {
      val res = new Array[Int](size)
      val rnd = new util.Random(lang)

      for (i <- 0 until size) {
        assert(iter.hasNext, s"iterator must have at least $size elements")
        res(i) = iter.next
      }

      var i = size.toLong
      while (iter.hasNext) {
        val elt = iter.next
        val j = math.abs(rnd.nextLong) % i
        if (j < size)
          res(j.toInt) = elt
        i += 1
      }
      // TODO 4.B Creating vectors for clustering
      // this functionality in method.
      res
    }

    val res =
      if (langSpread < 500)
        // sample the space regardless of the language
        vectors.takeSample(false, kmeansKernels, 42)
      else
        // sample the space uniformly from each language partition
        vectors.groupByKey.flatMap({
          case (lang, vectors) => reservoirSampling(lang, vectors.toIterator, perLang).map((lang, _))
        }).collect()
    // assert(res.length == kmeansKernels, kmeansKernels) // FIXME
    assert(res.length == kmeansKernels, res.length)
    res
  }


  //
  //
  //  Kmeans method:
  //
  //

  /** Main kmeans computation */
  @tailrec final def kmeans(means: Array[(Int, Int)], vectors: RDD[(Int, Int)], iter: Int = 1, debug: Boolean = false): Array[(Int, Int)] = {
    // TODO 5.A Kmeans Clustering
    // To implement these iterative steps,
    // use the provided functions findClosest, averageVectors, and euclideanDistance.
    // Note 1:
    // In our tests, convergence is reached after 44 iterations (for langSpread=50000) and in 104 iterations
    // (for langSpread=1), and for the first iterations the distance kept growing.
    // Although it may look like something is wrong, this is the expected behavior.
    // Having many remote points forces the kernels to shift quite a bit
    // and with each shift the effects ripple to other kernels, which also move around, and so on.
    // Be patient, in 44 iterations the distance will drop from over 100000 to 13, satisfying the convergence condition.
    // Note 2:
    // The variable langSpread corresponds to how far away are languages from the clustering algorithm's point of view.
    // For a value of 50000, the languages are too far away to be clustered together at all,
    // resulting in a clustering that only takes scores into account
    // for each language (similarly to partitioning the data across languages and then clustering based on the score).
    // A more interesting (but less scientific) clustering occurs
    // when langSpread is set to 1 (we can't set it to 0, as it loses language information completely),
    // where we cluster according to the score. See which language dominates the top questions now?

    val newMeans = means.clone() // you need to compute newMeans

    // TODO: Fill in the newMeans array
    val distance = euclideanDistance(means, newMeans)

    if (debug) {
      println(s"""Iteration: $iter
                 |  * current distance: $distance
                 |  * desired distance: $kmeansEta
                 |  * means:""".stripMargin)
      for (idx <- 0 until kmeansKernels)
      println(f"   ${means(idx).toString}%20s ==> ${newMeans(idx).toString}%20s  " +
              f"  distance: ${euclideanDistance(means(idx), newMeans(idx))}%8.0f")
    }

    if (converged(distance))
      newMeans
    else if (iter < kmeansMaxIterations)
      kmeans(newMeans, vectors, iter + 1, debug)
    else {
      println("Reached max iterations!")
      newMeans
    }
  }




  //
  //
  //  Kmeans utilities:
  //
  //

  /** Decide whether the kmeans clustering converged */
  def converged(distance: Double) =
    distance < kmeansEta


  /** Return the euclidean distance between two points */
  def euclideanDistance(v1: (Int, Int), v2: (Int, Int)): Double = {
    val part1 = (v1._1 - v2._1).toDouble * (v1._1 - v2._1)
    val part2 = (v1._2 - v2._2).toDouble * (v1._2 - v2._2)
    part1 + part2
  }

  /** Return the euclidean distance between two points */
  def euclideanDistance(a1: Array[(Int, Int)], a2: Array[(Int, Int)]): Double = {
    assert(a1.length == a2.length)
    var sum = 0d
    var idx = 0
    while(idx < a1.length) {
      sum += euclideanDistance(a1(idx), a2(idx))
      idx += 1
    }
    sum
  }


  /** Return the closest point */
  def findClosest(p: (Int, Int), centers: Array[(Int, Int)]): Int = {
    var bestIndex = 0
    var closest = Double.PositiveInfinity
    for (i <- 0 until centers.length) {
      val tempDist = euclideanDistance(p, centers(i))
      if (tempDist < closest) {
        closest = tempDist
        bestIndex = i
      }
    }
    bestIndex
  }


  /** Average the vectors */
  def averageVectors(ps: Iterable[(Int, Int)]): (Int, Int) = {
    val iter = ps.iterator
    var count = 0
    var comp1: Long = 0
    var comp2: Long = 0
    while (iter.hasNext) {
      val item = iter.next
      comp1 += item._1
      comp2 += item._2
      count += 1
    }
    ((comp1 / count).toInt, (comp2 / count).toInt)
  }


  //
  //
  //  Displaying results:
  //
  //
  def clusterResults(means: Array[(Int, Int)], vectors: RDD[(Int, Int)]): Array[(String, Double, Int, Int)] = {

    val closest = vectors.map(p => (findClosest(p, means), p))
    val closestGrouped = closest.groupByKey()

    val median = closestGrouped.mapValues { vs =>
      // TODO 6 Computing Cluster Details
      // Implement the clusterResults method, which, for each cluster, computes:
      // (a) the dominant programming language in the cluster;
      // (b) the percent of answers that belong to the dominant language;
      // (c) the size of the cluster (the number of questions it contains);
      // (d) the median of the highest answer scores.
      val dominant = vs.groupBy(_._1).map(g=>(g._1, g._2.map(_._2).sum)).toList.sortBy(_._2).head
      // TODO 6.A Computing Cluster Details
      val langLabel: String   = langs(dominant._1 / langSpread)  // most common language in the cluster
    // TODO 6.B Computing Cluster Details
      val langPercent: Double = vs.count(_._1 == dominant._1).toDouble / vs.size // percent of the questions in the most common language
    // TODO 6.C Computing Cluster Details
      val clusterSize: Int    = vs.size // the size of the cluster (the number of questions it contains)
      // TODO 6.D Computing Cluster Details
      val medianScore: Int    = averageVectors(vs)._2 // the median of the highest answer scores

      (langLabel, langPercent, clusterSize, medianScore)
    }

    median.collect().map(_._2).sortBy(_._4)
  }

  def printResults(results: Array[(String, Double, Int, Int)]): Unit = {
    // TODO 7.A Questions
    // Do you think that partitioning your data would help?
    // Have you thought about persisting some of your data? Can you think of why persisting your data in memory may be helpful for this algorithm?
    // Of the non-empty clusters, how many clusters have "Java" as their label (based on the majority of questions, see above)? Why?
    // Only considering the "Java clusters", which clusters stand out and why?
    // How are the "C# clusters" different compared to the "Java clusters"?

    println("Resulting clusters:")
    println("  Score  Dominant language (%percent)  Questions")
    println("================================================")
    for ((lang, percent, size, score) <- results)
      println(f"${score}%7d  ${lang}%-17s (${percent}%-5.1f%%)      ${size}%7d")
  }
}
