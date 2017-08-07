package observatory

import java.time.LocalDate

import observatory.utils.MySparkJob
import observatory.utils.MyResources.resourcePath
import org.apache.spark.sql.{DataFrame, Dataset}
import org.apache.spark.sql.types.{DoubleType, IntegerType}
import org.apache.spark.sql.functions._

/**
  * 1st milestone: data extraction
  */
object Extraction extends MySparkJob {


  // HACK git hub dot com
  // /TomLous/coursera-scala-capstone
  // /blob/master/src/main/scala/observatory/Extraction.scala
  // AND
  // /stephenpascoe/scala-capstone
  // /blob/master/observatory/src/main/scala/observatory/Extraction.scala

  import spark.implicits._

  def myJoined(stations: Dataset[MyStation], temperatures: Dataset[MyTemperatureRecord]):Dataset[MyJoinedFormat] = {
    // TODO 4.B locateTemperatures.myJoined
    // QQQ
    stations
      .join(temperatures, usingColumn = "id")
      .as[MyJoined]
      .map(j => (MyStationDate(j.day, j.month, j.year), Location(j.latitude, j.longitude), j.temperature))
      .toDF("date", "location", "temperature")
      .as[MyJoinedFormat]
  }


  def myStations(stationsFile: String): Dataset[MyStation] = {
    // TODO 4.C locateTemperatures.myStations
    // QQQ
    spark
      .read
      .csv(resourcePath(stationsFile)).select(
        concat_ws("~", coalesce('_c0, lit("")), '_c1).alias("id"),
        '_c2.alias("latitude").cast(DoubleType),
        '_c3.alias("longitude").cast(DoubleType)
      )
      .where('_c2.isNotNull && '_c3.isNotNull && '_c2 =!= 0.0 && '_c3 =!= 0.0)
      .as[MyStation]
  }

  def myTemperatures(year: Int,temperaturesFile: String): Dataset[MyTemperatureRecord] = {
    // TODO 4.D locateTemperatures.myTemperatures
    // QQQ
    spark
      .read
      .csv(resourcePath(temperaturesFile) )
      .select(
        concat_ws("~", coalesce('_c0, lit("")), '_c1).alias("id"),
        '_c3.alias("day").cast(IntegerType),
        '_c2.alias("month").cast(IntegerType),
        lit(year).as("year"),
        (('_c4 - 32) / 9 * 5).alias("temperature").cast(DoubleType)
      )
      .where('_c4.between(-200, 200))
      .as[MyTemperatureRecord]
  }

  /**
    * @param year             Year number
    * @param stationsFile     Path of the stations resource file to use (e.g. "/stations.csv")
    * @param temperaturesFile Path of the temperatures resource file to use (e.g. "/1975.csv")
    * @return A sequence containing triplets (date, location, temperature)
    */
  def locateTemperatures(year: Int, stationsFile: String, temperaturesFile: String): Iterable[(LocalDate, Location, Double)] = {
    // TODO 4.A locateTemperatures
    // QQQ
    val j = myJoined(myStations(stationsFile), myTemperatures(year, temperaturesFile))
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
    // TODO 4.E locationYearlyAverageRecords
    // QQQ
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
