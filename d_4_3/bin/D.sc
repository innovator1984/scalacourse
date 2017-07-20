import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.DataFrame
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
  println("Welcome to Spark!")
  
  lazy val conf: SparkConf = new SparkConf().setMaster("local").setAppName("D")
  lazy val sc: SparkContext = new SparkContext(conf)
  lazy val sq: SQLContext = new SQLContext(sc)
  new D().initLog4j
  import sq.implicits._
  import org.apache.spark.sql.functions._
  
  case class Post(authorID: Int, subforum: String, likes: Int, date: String)

  val postsRdd: RDD[Post] = sc.parallelize(("a aaa 10", 10) :: ("b bbb 20", 20) :: Nil).map {case (text, v) => Post(v, text, 0, "") }
  
  val postsDf: DataFrame = postsRdd.toDF("authorID", "subforum", "likes", "date")

  postsDf.show
  
  val rankedDf = postsDf.groupBy($"authorID", $"subforum")
    .agg(count($"authorID"))
    .orderBy($"subforum",$"count(authorID)".desc)
    
  rankedDf.show

}