package stackoverflow

import org.scalatest.{FunSuite, BeforeAndAfterAll}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD
import java.io.File

@RunWith(classOf[JUnitRunner])
class StackOverflowSuite extends FunSuite with BeforeAndAfterAll {

  lazy val conf = new SparkConf().setMaster("local").setAppName("D")
  lazy val sc = new SparkContext(conf)

  lazy val testObject = new StackOverflow {
    override val langs =
      List(
        "JavaScript", "Java", "PHP", "Python", "C#", "C++", "Ruby", "CSS",
        "Objective-C", "Perl", "Scala", "Haskell", "MATLAB", "Clojure", "Groovy")
    override def langSpread = 50000
    override def kmeansKernels = 45
    override def kmeansEta: Double = 20.0D
    override def kmeansMaxIterations = 120
  }

  test("testObject can be instantiated") {
    val instantiatable = try {
      testObject
      true
    } catch {
      case _: Throwable => false
    }
    assert(instantiatable, "Can't instantiate a StackOverflow object")
  }

  // ===== MY TESTS =====

  test("rawPostings pos 01") {
    // TODO 1.A The Data
    val lines = sc.parallelize(List("1,9002525,,,2,C++", "2,9003401,,9002525,4,",
      "2,9003942,,9002525,1,", "2,9005311,,9002525,0,"))
    val obj = new StackOverflow()
    val raw = obj.rawPostings(lines)
    val res = raw.collect()
    assert(res.length == 4)
  }

  test("rawPostings neg 01") {
    // TODO 1.A The Data
    val lines = sc.parallelize(List("1,9002525,,,2,C++", "2,9003401,,9002525,4,",
      "2,9003942,,9002525,1,", "2,9005311,,9002525,0,"))
    val obj = new StackOverflow()
    val raw = obj.rawPostings(lines)
    val res = raw.collect()
    assert(res.length != 0)
  }

  test("groupedPostings pos 01") {
    // TODO 2.A Data processing
    val lines = sc.parallelize(List("1,9002525,,,2,C++", "2,9003401,,9002525,4,",
      "2,9003942,,9002525,1,", "2,9005311,,9002525,0,"))
    val obj = new StackOverflow()
    val raw = obj.rawPostings(lines)
    val res = obj.groupedPostings(raw).collect()
    assert(res.length == 1)
    assert(res.head._1 == 9002525)
  }

  test("groupedPostings neg 01") {
    // TODO 2.A Data processing
    val lines = sc.parallelize(List("1,9002525,,,2,C++", "2,9003401,,9002525,4,",
      "2,9003942,,9002525,1,", "2,9005311,,9002525,0,",
      "1,9419744,,,2,Objective-C"))
    val obj = new StackOverflow()
    val raw = obj.rawPostings(lines)
    val res = obj.groupedPostings(raw).collect()
    assert(res.length != 0)
    assert(res.head._1 != 9419744)
  }

  test("scoredPostings pos 01") {
    // TODO 3.A Computing Scores
    val lines = sc.parallelize(List("1,9002525,,,2,C++", "2,9003401,,9002525,4,",
      "2,9003942,,9002525,1,", "2,9005311,,9002525,0,"))
    val obj = new StackOverflow()
    val raw = obj.rawPostings(lines)
    val res = obj.groupedPostings(raw)
    val sco = obj.scoredPostings(res).collect()
    assert(sco.length == 1)
    assert(sco.head._1.id == 9002525)
    assert(sco.head._2 == 4)
  }

  test("scoredPostings neg 01") {
    // TODO 3.A Computing Scores
    val lines = sc.parallelize(List("1,9002525,,,2,C++", "2,9003401,,9002525,4,",
      "2,9003942,,9002525,1,", "2,9005311,,9002525,0,",
      "1,9419744,,,2,Objective-C"))
    val obj = new StackOverflow()
    val raw = obj.rawPostings(lines)
    val res = obj.groupedPostings(raw)
    val sco = obj.scoredPostings(res).collect()
    assert(sco.length != 0)
    assert(sco.head._1.id != 9419744)
    assert(sco.head._2 != 1)
  }

  test("vectorPostings pos 01") {
    // TODO 4.A Creating vectors for clustering
    // The type of the vectors RDD is as follows:
    //     val vectors: RDD[(Int, Int)] = ???
    // *** Index of the language (in the langs list) multiplied by the langSpread factor.
    // *** The highest answer score (computed above).
    val lines = sc.parallelize(List("1,9002525,,,2,C++", "2,9003401,,9002525,4,",
      "2,9003942,,9002525,1,", "2,9005311,,9002525,0,"))
    val obj = new StackOverflow()
    val raw = obj.rawPostings(lines)
    val res = obj.groupedPostings(raw)
    val sco = obj.scoredPostings(res)
    val vec = obj.vectorPostings(sco).collect()
    assert(vec.length == 1)
    assert(vec.head._1 == 5 * obj.langSpread)
    assert(vec.head._2 == 4)
  }

  test("vectorPostings neg 01") {
    // TODO 4.A Creating vectors for clustering
    val lines = sc.parallelize(List("1,9002525,,,2,C++", "2,9003401,,9002525,4,",
      "2,9003942,,9002525,1,", "2,9005311,,9002525,0,",
      "1,9419744,,,2,Objective-C"))
    val obj = new StackOverflow()
    val raw = obj.rawPostings(lines)
    val res = obj.groupedPostings(raw)
    val sco = obj.scoredPostings(res)
    val vec = obj.vectorPostings(sco).collect()
    assert(vec.length != 0)
    assert(vec.head._1 != 0)
    assert(vec.head._2 != 1)
  }

  test("sampleVectors pos 01") {
    // TODO 4.B Creating vectors for clustering
    // The type of the vectors RDD is as follows:
    //     val vectors: RDD[(Int, Int)] = ???
    // *** Index of the language (in the langs list) multiplied by the langSpread factor.
    // *** The highest answer score (computed above).
    val lines = sc.parallelize(List("1,9002525,,,2,C++", "2,9003401,,9002525,1,",
      "2,9003942,,9002525,1,", "2,9005311,,9002525,0,"))
    val obj = new StackOverflow()
    val raw = obj.rawPostings(lines)
    val res = obj.groupedPostings(raw)
    val sco = obj.scoredPostings(res)
    val vec = obj.vectorPostings(sco)
    val xxx = sc.parallelize(List.range(0, 680)).map(x => (5 * obj.langSpread, x))
    val yyy = xxx.join(vec).groupBy(_._1).map({case(k,v) => (1,v.map(y =>(5 * obj.langSpread
      + (math.abs(y._2._1) % 15) * 1000000, math.abs(y._2._1) % 15)))})
      .values.flatMap(_.toStream)
    val zzz = yyy.collect()
    assert(zzz.length == 680)
    assert(zzz(0)._1 == 5 * obj.langSpread)
    assert(zzz(0)._2 == 0)
    assert(zzz(1)._1 == 5 * obj.langSpread + 1000000)
    assert(zzz(1)._2 == 1)
    val sam = obj.sampleVectors(yyy)
    assert(sam.length == 45)
    // TODO: READ https://ru.wikipedia.org/wiki/Reservoir_sampling
    assert(sam(0)._1 == 5 * obj.langSpread + 4 * 1000000)
    assert(sam(0)._2 == 4)
  }

  test("sampleVectors neg 01") {
    // TODO 4.B Creating vectors for clustering
    // The type of the vectors RDD is as follows:
    //     val vectors: RDD[(Int, Int)] = ???
    // *** Index of the language (in the langs list) multiplied by the langSpread factor.
    // *** The highest answer score (computed above).
    val lines = sc.parallelize(List("1,9002525,,,2,C++", "2,9003401,,9002525,1,",
      "2,9003942,,9002525,1,", "2,9005311,,9002525,0,"))
    val obj = new StackOverflow()
    val raw = obj.rawPostings(lines)
    val res = obj.groupedPostings(raw)
    val sco = obj.scoredPostings(res)
    val vec = obj.vectorPostings(sco)
    val xxx = sc.parallelize(List.range(0, 680)).map(x => (5 * obj.langSpread, x))
    val yyy = xxx.join(vec).groupBy(_._1).map({case(k,v) => (1,v.map(y =>(5 * obj.langSpread
      + (math.abs(y._2._1) % 15) * 1000000, math.abs(y._2._1) % 15)))})
      .values.flatMap(_.toStream)
    val zzz = yyy.collect()
    assert(zzz.length != 650)
    assert(zzz(0)._1 != -1)
    assert(zzz(0)._2 != -1)
    val sam = obj.sampleVectors(yyy)
    assert(sam.length != 30)
    assert(sam(0)._1 != 0)
    assert(sam(0)._2 != 0)
  }

  test("kmeans pos 01") {
    // TODO 5.A Kmeans Clustering
    assert(true)
  }

  test("kmeans neg 01") {
    // TODO 5.A Kmeans Clustering
    assert(true)
  }

  test("clusterResults pos 01") {
    // TODO 6 Kmeans Clustering
    assert(true)
  }

  test("clusterResults neg 01") {
    // TODO 6 Kmeans Clustering
    assert(true)
  }

  test("langLabel pos 01") {
    // TODO 6.A Kmeans Clustering
    assert(true)
  }

  test("langLabel neg 01") {
    // TODO 6.A Kmeans Clustering
    assert(true)
  }

  test("langPercent pos 01") {
    // TODO 6.B Kmeans Clustering
    assert(true)
  }

  test("langPercent neg 01") {
    // TODO 6.B Kmeans Clustering
    assert(true)
  }

  test("clusterSize pos 01") {
    // TODO 6.C Kmeans Clustering
    assert(true)
  }

  test("clusterSize neg 01") {
    // TODO 6.C Kmeans Clustering
    assert(true)
  }

  test("medianScore pos 01") {
    // TODO 6.D Kmeans Clustering
    assert(true)
  }

  test("medianScore neg 01") {
    // TODO 6.D Kmeans Clustering
    assert(true)
  }

  test("printResults pos 01") {
    // TODO 7.A Kmeans Clustering
    assert(true)
  }

  test("printResults neg 01") {
    // TODO 7.A Kmeans Clustering
    assert(true)
  }
}
