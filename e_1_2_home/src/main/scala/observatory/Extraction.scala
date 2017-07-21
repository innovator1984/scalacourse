package observatory

import java.nio.file.Paths
import java.time.LocalDate

import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.functions
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{DoubleType, IntegerType}

trait SparkJob1 {
  Logger.getLogger("org.apache.spark").setLevel(Level.WARN)

  implicit val spark:SparkSession = SparkSession
    .builder()
    .master("local[6]")
    .appName(this.getClass.getSimpleName)
    .getOrCreate()
}

object Resources1 {
  def resourcePath(resource: String): String =
    Paths.get(getClass.getResource(resource).toURI).toString
}

object Models1 {
  case class Joined(id: String, latitude:Double, longitude: Double, day: Int, month: Int, year: Int, temperature: Double)

  case class StationDate(day: Int, month: Int, year: Int){
    def toLocalDate = LocalDate.of(year, month, day)
  }

  case class JoinedFormat(date: StationDate, location: Location, temperature: Double)


  case class Station(id: String, latitude: Double, longitude: Double)

  case class TemperatureRecord(id: String, day: Int, month: Int, year: Int, temperature: Double)
}

/**
  * 1st milestone: data extraction
  */
object Extraction extends SparkJob1 {

  import spark.implicits._

  def stations(stationsFile: String): Dataset[Models1.Station] = {
    spark
      .read
      .csv(Resources1.resourcePath(stationsFile))
      .select(
        functions.concat_ws("~", functions.coalesce('_c0, functions.lit("")), '_c1).alias("id"),
        '_c2.alias("latitude").cast(DoubleType),
        '_c3.alias("longitude").cast(DoubleType)
      )
      .where('_c2.isNotNull && '_c3.isNotNull && '_c2 =!= 0.0 && '_c3 =!= 0.0)
      .as[Models1.Station]
  }

  def temperatures(year: Int,temperaturesFile: String): Dataset[Models1.TemperatureRecord] = {
    spark
      .read
      .csv(Resources1.resourcePath(temperaturesFile) )
      .select(
        functions.concat_ws("~", functions.coalesce('_c0, functions.lit("")), '_c1).alias("id"),
        '_c3.alias("day").cast(IntegerType),
        '_c2.alias("month").cast(IntegerType),
        functions.lit(year).as("year"),
        (('_c4 - 32) / 9 * 5).alias("temperature").cast(DoubleType)
      )
      .where('_c4.between(-200, 200))
      .as[Models1.TemperatureRecord]
  }

  def joined(stations: Dataset[Models1.Station], temperatures: Dataset[Models1.TemperatureRecord]):
      Dataset[Models1.JoinedFormat] = {
    stations
      .join(temperatures, usingColumn = "id")
      .as[Models1.Joined]
      .map(j => (Models1.StationDate(j.day, j.month, j.year), Location(j.latitude, j.longitude), j.temperature))
      .toDF("date", "location", "temperature")
      .as[Models1.JoinedFormat]
  }

  /**
    * @param year             Year number
    * @param stationsFile     Path of the stations resource file to use (e.g. "/stations.csv")
    * @param temperaturesFile Path of the temperatures resource file to use (e.g. "/1975.csv")
    * @return A sequence containing triplets (date, location, temperature)
    */
  def locateTemperatures(year: Int, stationsFile: String, temperaturesFile: String):
      Iterable[(LocalDate, Location, Double)] = {
    // TODO 1.A. Data Extraction
    // You will first have to implement a method “locateTemperatures”
    // This method should return the list of all the temperature records converted in degrees Celsius
    // along with their date and location (ignore data coming from stations that have no GPS coordinates).
    // You should not round the temperature values.
    // The file paths are resource paths, so they must be absolute locations in your classpath
    // (so that you can read them with getResourceAsStream).
    // For instance, the path for the resource file 1975.csv is "/1975.csv".

    // HACK git hub dot com
    // /TomLous/coursera-scala-capstone
    // /blob/master/src/main/scala/observatory/Extraction.scala
    // QQQ
    val j = joined(stations(stationsFile), temperatures(year, temperaturesFile))

    //    // It'stations a shame we have to use the LocalDate because Spark cannot encode that. hence this ugly bit
    j.collect()
      .par
      .map(
        jf => (jf.date.toLocalDate, jf.location, jf.temperature)
      ).seq
    

  }

  /**
    * @param records A sequence containing triplets (date, location, temperature)
    * @return A sequence containing, for each location, the average temperature over the year.
    */
  def locationYearlyAverageRecords(records: Iterable[(LocalDate, Location, Double)]): Iterable[(Location, Double)] = {
    // TODO 1.B. Data Extraction
    // you will have to implement the following method
    // This method should return the average temperature on each location, over a year.
    // For instance, with the data given in the examples, this method would return the following sequence:
    // Note that the method signatures use the collection type Iterable, so, at the end,
    // you will have to produce such values, but your internal implementation might use some other data type,
    // if you think that it would have better performance.
    records
      .par
      .groupBy(_._2)
      .mapValues(
        l => l.foldLeft(0.0)(
          (t,r) => t + r._3) / l.size
      )
      .seq
  }

}
