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
  
  case class Abonnement(name: String)
  val AG = new Abonnement("AG")                   //> AG  : D.Abonnement = Abonnement(AG)
  val DemiTarif = new Abonnement("DemiTarif")     //> DemiTarif  : D.Abonnement = Abonnement(DemiTarif)
  val DemiTarifVisa = new Abonnement("DemiTarifVisa")
                                                  //> DemiTarifVisa  : D.Abonnement = Abonnement(DemiTarifVisa)
  
  val as = List((101, ("Ruetli", AG)), (102, ("Brelaz", DemiTarif)),
      (103, ("Gress", DemiTarifVisa)), (104, ("Schatten", DemiTarif)))
                                                  //> as  : List[(Int, (String, D.Abonnement))] = List((101,(Ruetli,Abonnement(AG
                                                  //| ))), (102,(Brelaz,Abonnement(DemiTarif))), (103,(Gress,Abonnement(DemiTarif
                                                  //| Visa))), (104,(Schatten,Abonnement(DemiTarif))))
  val abos = sc.parallelize(as)                   //> Using Spark's default log4j profile: org/apache/spark/log4j-defaults.proper
                                                  //| ties
                                                  //| abos  : org.apache.spark.rdd.RDD[(Int, (String, D.Abonnement))] = ParallelC
                                                  //| ollectionRDD[0] at parallelize at D.scala:38

  val ls = List((101, "Bern"), (101, "Thun"), (102, "Lausanne"), (102, "Geneve"),
      (102, "Nyon"), (103, "Zurich"), (103, "St-Gallen"), (103, "Chur"))
                                                  //> ls  : List[(Int, String)] = List((101,Bern), (101,Thun), (102,Lausanne), (1
                                                  //| 02,Geneve), (102,Nyon), (103,Zurich), (103,St-Gallen), (103,Chur))
      
  val locations = sc.parallelize(ls)              //> locations  : org.apache.spark.rdd.RDD[(Int, String)] = ParallelCollectionRD
                                                  //| D[1] at parallelize at D.scala:43
                                                  
  val trackedCustomers = abos.join(locations)     //> trackedCustomers  : org.apache.spark.rdd.RDD[(Int, ((String, D.Abonnement),
                                                  //|  String))] = MapPartitionsRDD[4] at join at D.scala:45
  trackedCustomers.collect().foreach(println)     //> (101,((Ruetli,Abonnement(AG)),Bern))
                                                  //| (101,((Ruetli,Abonnement(AG)),Thun))
                                                  //| (102,((Brelaz,Abonnement(DemiTarif)),Lausanne))
                                                  //| (102,((Brelaz,Abonnement(DemiTarif)),Geneve))
                                                  //| (102,((Brelaz,Abonnement(DemiTarif)),Nyon))
                                                  //| (103,((Gress,Abonnement(DemiTarifVisa)),Zurich))
                                                  //| (103,((Gress,Abonnement(DemiTarifVisa)),St-Gallen))
                                                  //| (103,((Gress,Abonnement(DemiTarifVisa)),Chur))

  val abosWithOptionalLocations = abos.leftOuterJoin(locations)
                                                  //> abosWithOptionalLocations  : org.apache.spark.rdd.RDD[(Int, ((String, D.Abo
                                                  //| nnement), Option[String]))] = MapPartitionsRDD[7] at leftOuterJoin at D.sca
                                                  //| la:48
  abosWithOptionalLocations.collect().foreach(println)
                                                  //> (101,((Ruetli,Abonnement(AG)),Some(Bern)))
                                                  //| (101,((Ruetli,Abonnement(AG)),Some(Thun)))
                                                  //| (104,((Schatten,Abonnement(DemiTarif)),None))
                                                  //| (102,((Brelaz,Abonnement(DemiTarif)),Some(Lausanne)))
                                                  //| (102,((Brelaz,Abonnement(DemiTarif)),Some(Geneve)))
                                                  //| (102,((Brelaz,Abonnement(DemiTarif)),Some(Nyon)))
                                                  //| (103,((Gress,Abonnement(DemiTarifVisa)),Some(Zurich)))
                                                  //| (103,((Gress,Abonnement(DemiTarifVisa)),Some(St-Gallen)))
                                                  //| (103,((Gress,Abonnement(DemiTarifVisa)),Some(Chur)))
  val customersWithLocationDataAndOptionalAbos = abos.rightOuterJoin(locations)
                                                  //> customersWithLocationDataAndOptionalAbos  : org.apache.spark.rdd.RDD[(Int, 
                                                  //| (Option[(String, D.Abonnement)], String))] = MapPartitionsRDD[10] at rightO
                                                  //| uterJoin at D.scala:50
  customersWithLocationDataAndOptionalAbos.collect().foreach(println)
                                                  //> (101,(Some((Ruetli,Abonnement(AG))),Bern))
                                                  //| (101,(Some((Ruetli,Abonnement(AG))),Thun))
                                                  //| (102,(Some((Brelaz,Abonnement(DemiTarif))),Lausanne))
                                                  //| (102,(Some((Brelaz,Abonnement(DemiTarif))),Geneve))
                                                  //| (102,(Some((Brelaz,Abonnement(DemiTarif))),Nyon))
                                                  //| (103,(Some((Gress,Abonnement(DemiTarifVisa))),Zurich))
                                                  //| (103,(Some((Gress,Abonnement(DemiTarifVisa))),St-Gallen))
                                                  //| (103,(Some((Gress,Abonnement(DemiTarifVisa))),Chur))
}