import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.log4j.Logger
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import scala.io.Source
import java.net.URL

class D {
  def initLog4j(): Unit = {
    val log = Logger.getLogger(getClass.getName)
    val path = "org/apache/spark/log4j-defaults.properties"
    val logCfgProps : Config =  ConfigFactory.load(path)
    val configLogProps = Option(getClass.getClassLoader.getResource(path)).fold
    {
      log.error("log4j.properties file not retrieved. Application is shutting down")
      ConfigFactory.load()
    }
    { resource => ConfigFactory.load(logCfgProps) }
  }
}

object D {
  println("Welcome to Spark!")                    //> Welcome to Spark!
  
  lazy val conf: SparkConf = new SparkConf().setMaster("local").setAppName("D")
                                                  //> conf: => org.apache.spark.SparkConf
  lazy val sc: SparkContext = new SparkContext(conf)
                                                  //> sc: => org.apache.spark.SparkContext
  new D().initLog4j
  
  case class Event(organizer: String, name: String, budget: Int)

  val eventsRdd = sc.parallelize(Seq(new Event("a", "a name", 10),
    new Event("b", "b name", 20)))
       .map(event => (event.organizer, event.budget))
                                                  //> Using Spark's default log4j profile: org/apache/spark/log4j-defaults.proper
                                                  //| ties
                                                  //| eventsRdd  : org.apache.spark.rdd.RDD[(String, Int)] = MapPartitionsRDD[1] 
                                                  //| at map at D.scala:35

  val groupedRdd = eventsRdd.groupByKey()         //> groupedRdd  : org.apache.spark.rdd.RDD[(String, Iterable[Int])] = ShuffledR
                                                  //| DD[2] at groupByKey at D.scala:37
  
  groupedRdd.collect().foreach(println)           //> (a,CompactBuffer(10))
                                                  //| (b,CompactBuffer(20))
  
  val intermediate =
    eventsRdd.mapValues(b => (b, 1))
      .reduceByKey((v1, v2) => (v1._1 + v2._1, v1._2 + v2._2))
                                                  //> intermediate  : org.apache.spark.rdd.RDD[(String, (Int, Int))] = ShuffledRD
                                                  //| D[4] at reduceByKey at D.scala:43
  
  val avgBudgets = intermediate.mapValues {
    case (budget, numberOfEvents) => budget / numberOfEvents
  }                                               //> avgBudgets  : org.apache.spark.rdd.RDD[(String, Int)] = MapPartitionsRDD[5]
                                                  //|  at mapValues at D.scala:45
  
  avgBudgets.collect().foreach(println)           //> (a,10)
                                                  //| (b,20)
                                                  
  case class Visitor(ip: String, timestamp: String, duration: String)

  val visitors: RDD[Visitor] = sc.textFile("file://" + "/tmp/visitors.csv")
      .map(line => new Visitor((line split " ")(0), (line split " ")(1), (line split " ")(2)))
                                                  //> visitors  : org.apache.spark.rdd.RDD[D.Visitor] = MapPartitionsRDD[8] at ma
                                                  //| p at D.scala:54
  visitors.collect().foreach(println)             //> Visitor(a,,10,,100)
                                                  //| Visitor(b,,20,,200)
  
  val visits = visitors
      .map(v => (v.ip, v.duration))               //> visits  : org.apache.spark.rdd.RDD[(String, String)] = MapPartitionsRDD[9] 
                                                  //| at map at D.scala:58
  visits.collect().foreach(println)               //> (a,,100)
                                                  //| (b,,200)
  
  val numUniqueVisits = visits.keys.distinct().count()
                                                  //> numUniqueVisits  : Long = 2
  
}