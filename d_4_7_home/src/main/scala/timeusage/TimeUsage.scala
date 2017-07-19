package timeusage

import java.nio.file.Paths

import org.apache.spark.sql._
import org.apache.spark.sql.types._

/** Main class */
object TimeUsage {

  import org.apache.spark.sql.SparkSession
  import org.apache.spark.sql.functions._

  val spark: SparkSession =
    SparkSession
      .builder()
      .appName("Time Usage")
      .config("spark.master", "local")
      .getOrCreate()

  // For implicit conversions like converting RDDs to DataFrames
  import spark.implicits._

  /** Main function */
  def main(args: Array[String]): Unit = {
    // TODO 1.A The problem
    // Our goal is to identify three groups of activities:
    // primary needs (sleeping and eating), work, other (leisure).
    // And then to observe how do people allocate their time
    // between these three kinds of activities,
    // and if we can see differences between men and women,
    // employed and unemployed people, and young (less than 22 years old),
    // active (between 22 and 55 years old) and elder people.

    // HACK git hub dot com
    // TomLous/coursera-scala-spark-big-data/
    // blob/master/src/main/scala/timeusage/TimeUsage.scala
    timeUsageByLifePeriod()
  }

  def timeUsageByLifePeriod(): Unit = {
    // TODO 1.B The problem
    // At the end of the assignment we will be able to answer the following questions based on the dataset:
    // how much time do we spend on primary needs compared to other activities?
    // do women and men spend the same amount of time in working?
    // does the time spent on primary needs change when people get older?
    // how much time do employed people spend on leisure compared to unemployed people?
    // To achieve this, we will first read the dataset with Spark, transform it
    // into an intermediate dataset which will be easier to work with for our use case,
    // and finally compute information that will answer the above questions.

    val (columns, initDf) = read("/timeusage/atussum.csv")
    val (primaryNeedsColumns, workColumns, otherColumns) = classifiedColumns(columns)
    val summaryDf = timeUsageSummary(primaryNeedsColumns, workColumns, otherColumns, initDf)
    val finalDf = timeUsageGrouped(summaryDf)
    finalDf.show()
  }

  /** @return The read DataFrame along with its column names. */
  def read(resource: String): (List[String], DataFrame) = {
    // TODO 2.A Read-in Data
    // The simplest way to create a DataFrame consists in reading a file
    // and letting Spark-sql infer the underlying schema.
    // However this approach does not work well with CSV files,
    // because the inferred column types are always String.

    // In our case, the first column contains a String value identifying the respondent
    // but all the other columns contain numeric values.
    // Since this schema will not be correctly inferred by Spark-sql,
    // we will define it programmatically.
    // However, the number of columns is huge.
    // So, instead of manually enumerating all the columns we can rely on the fact that,
    // in the CSV file, the first line contains the name of all the columns of the dataset.

    // Our first task consists in turning this first line into a Spark-sql StructType.
    // This is the purpose of the dfSchema method.
    // This method returns a StructType describing the schema of the CSV file,
    // where the first column has type StringType and all the others have type DoubleType.
    // None of these columns are nullable.

    // The second step is to be able to effectively read the CSV file is to turn each line
    // into a Spark-sql Row containing columns that matches the schema returned by dfSchema.
    // That’s the job of the row method.

    val rdd = spark.sparkContext.textFile(fsPath(resource))

    val headerColumns = rdd.first().split(",").to[List]
    // Compute the schema based on the first line of the CSV file
    val schema = dfSchema(headerColumns)

    val data =
      rdd
        .mapPartitionsWithIndex((i, it) => if (i == 0) it.drop(1) else it) // skip the header line
        .map(_.split(",").to[List])
        .map(row)

    val dataFrame =
      spark.createDataFrame(data, schema)

    (headerColumns, dataFrame)
  }

  /** @return The filesystem path of the given resource */
  def fsPath(resource: String): String =
    Paths.get(getClass.getResource(resource).toURI).toString

  /** @return The schema of the DataFrame, assuming that the first given column has type String and all the others
    *         have type Double. None of the fields are nullable.
    * @param columnNames Column names of the DataFrame
    */
  def dfSchema(columnNames: List[String]): StructType = {
    // TODO 2.B Read-in Data
    // ???
    StructType(StructField(columnNames.head, StringType, nullable = false)
      :: columnNames.tail.map(name => StructField(name, DoubleType, nullable = false)))
  }


  /** @return An RDD Row compatible with the schema produced by `dfSchema`
    * @param line Raw fields
    */
  def row(line: List[String]): Row = {
    // TODO 2.C Read-in Data
    // ABC
    Row(line.head.toString :: line.tail.map(_.toDouble): _*)
  }

  /** @return The initial data frame columns partitioned in three groups: primary needs (sleeping, eating, etc.),
    *         work and other (leisure activities)
    *
    * @see https://www.kaggle.com/bls/american-time-use-survey
    *
    * The dataset contains the daily time (in minutes) people spent in various activities. For instance, the column
    * “t010101” contains the time spent sleeping, the column “t110101” contains the time spent eating and drinking, etc.
    *
    * This method groups related columns together:
    * 1. “primary needs” activities (sleeping, eating, etc.). These are the columns starting with “t01”, “t03”, “t11”,
    *    “t1801” and “t1803”.
    * 2. working activities. These are the columns starting with “t05” and “t1805”.
    * 3. other activities (leisure). These are the columns starting with “t02”, “t04”, “t06”, “t07”, “t08”, “t09”,
    *    “t10”, “t12”, “t13”, “t14”, “t15”, “t16” and “t18” (those which are not part of the previous groups only).
    */
  def classifiedColumns(columnNames: List[String]): (List[Column], List[Column], List[Column]) = {
    // TODO 3.A Project
    // As you probably noticed, the initial dataset contains lots of information
    // that we don’t need to answer our questions, and even the columns
    // that contain useful information are too much detailed.
    // For instance, we are not interested in the exact age of each respondent,
    // but just whether she was “young”, “active” or “elder”.
    // Also, the time spent on each activity is very detailed (there are more than 50 reported activities).
    // Again, we don’t need this level of detail, we are only interested in three activities:
    // primary needs, work and other.
    // So, with this initial dataset it would a bit hard to express the queries
    // that would give us the answers we are looking for.
    // The second part of this assignment consists in transforming the initial dataset
    // into a format that will be easier to work with.
    // A first step in this direction is to identify which columns are related to the same activity.
    // Based on the description of the activity corresponding
    // to each column (given in this document), we deduce the following rules:
    // “primary needs” activities (sleeping, eating, etc.) are reported in columns
    // starting with “t01”, “t03”, “t11”, “t1801” and “t1803” ;
    // working activities are reported in columns starting with “t05” and “t1805” ;
    // other activities (leisure) are reported in columns
    // starting with “t02”, “t04”, “t06”, “t07”, “t08”, “t09”, “t10”,
    // “t12”, “t13”, “t14”, “t15”, “t16” and “t18” (only those which are not part of the previous groups).
    // Then our work consists in implementing the classifiedColumns method,
    // which classifies the given list of column names into three groups (primary needs, work or other).
    // This method should return a triplet containing the “primary needs” columns list,
    // the “work” columns list and the “other” columns list.
    // ABC

    val myCategoryMappings = List(
      List("t01", "t03", "t11", "t1801", "t1803"),
      List("t05", "t1805"),
      List("t02", "t04", "t06", "t07", "t08", "t09", "t10", "t12", "t13", "t14", "t15", "t16", "t18")
    ).zipWithIndex

    val myGroups: Map[Int, List[Column]] = columnNames
      .foldLeft(List.empty[(Int, Column)])((acc, name) => {
        myCategoryMappings
          .flatMap {
            case (prefixes, index) if prefixes.exists(name.startsWith) => Some((index, new Column(name)))
            case _ => None
          }
          .sortBy(_._1)
          .headOption match {
          case Some(tuple) => tuple :: acc
          case None => acc
        }
      }
      )
      .groupBy(_._1)
      .mapValues(_.map(_._2))

    val myResults = (0 to 2).map(index => myGroups.getOrElse(index, List.empty[Column]))

    (myResults(0), myResults(1), myResults(2))
  }

  /** @return a projection of the initial DataFrame such that all columns containing hours spent on primary needs
    *         are summed together in a single column (and same for work and leisure). The “teage” column is also
    *         projected to three values: "young", "active", "elder".
    *
    * @param primaryNeedsColumns List of columns containing time spent on “primary needs”
    * @param workColumns List of columns containing time spent working
    * @param otherColumns List of columns containing time spent doing other activities
    * @param df DataFrame whose schema matches the given column lists
    *
    * This methods builds an intermediate DataFrame that sums up all the columns of each group of activity into
    * a single column.
    *
    * The resulting DataFrame should have the following columns:
    * - working: value computed from the “telfs” column of the given DataFrame:
    *   - "working" if 1 <= telfs < 3
    *   - "not working" otherwise
    * - sex: value computed from the “tesex” column of the given DataFrame:
    *   - "male" if tesex = 1, "female" otherwise
    * - age: value computed from the “teage” column of the given DataFrame:
    *   - "young" if 15 <= teage <= 22,
    *   - "active" if 23 <= teage <= 55,
    *   - "elder" otherwise
    * - primaryNeeds: sum of all the `primaryNeedsColumns`, in hours
    * - work: sum of all the `workColumns`, in hours
    * - other: sum of all the `otherColumns`, in hours
    *
    * Finally, the resulting DataFrame should exclude people that are not employable (ie telfs = 5).
    *
    * Note that the initial DataFrame contains time in ''minutes''. You have to convert it into ''hours''.
    */
  def timeUsageSummary(
    primaryNeedsColumns: List[Column],
    workColumns: List[Column],
    otherColumns: List[Column],
    df: DataFrame
  ): DataFrame = {
    // TODO 3.B Project
    // The second step is to implement the timeUsageSummary method,
    // which projects the detailed dataset into a summarized dataset.
    // This summary will contain only 6 columns: the working status of the respondent,
    // his sex, his age, the amount of daily hours spent on primary needs activities,
    // the amount of daily hours spent on working and the amount of daily hours spent on other activities.
    // Each “activity column” will contain the sum of the columns related to the same activity of the initial dataset.
    // Note that time amounts are given in minutes in the initial dataset,
    // whereas in our resulting dataset we want them to be in hours.
    // The columns describing the work status, the sex and the age,
    // will contain simplified information compared to the initial dataset.
    // Last, people that are not employable will be filtered out of the resulting dataset.
    // The comment on top of the timeUsageSummary method will give you more specific
    // information about what is expected in each column.

    // val workingStatusProjection: Column = ABC
    val workingStatusProjection: Column = when('telfs >= 1.0 && 'telfs < 3.0, "working")
      .otherwise("not working")
      .as("working")
    // val sexProjection: Column = ABC
    val sexProjection: Column = when('tesex === 1.0, "male")
      .otherwise("female")
      .as("sex")
    // val ageProjection: Column = ABC
    val ageProjection: Column = when('teage.between(15.0, 22.0), "young")
      .when('teage.between(23.0, 55.0), "active")
      .otherwise("elder")
      .as("age")

    // val primaryNeedsProjection: Column = ABC
    val primaryNeedsProjection: Column = primaryNeedsColumns
      .reduce(_ + _)
      .divide(60)
      .as("primaryNeeds")
    // val workProjection: Column = ABC
    val workProjection: Column = workColumns
      .reduce(_ + _)
      .divide(60)
      .as("work")
    // val otherProjection: Column = ABC
    val otherProjection: Column = otherColumns
      .reduce(_ + _)
      .divide(60)
      .as("other")

    df
      .select(workingStatusProjection, sexProjection, ageProjection, primaryNeedsProjection, workProjection, otherProjection)
      .where($"telfs" <= 4) // Discard people who are not in labor force
  }

  /** @return the average daily time (in hours) spent in primary needs, working or leisure, grouped by the different
    *         ages of life (young, active or elder), sex and working status.
    * @param summed DataFrame returned by `timeUsageSumByClass`
    *
    * The resulting DataFrame should have the following columns:
    * - working: the “working” column of the `summed` DataFrame,
    * - sex: the “sex” column of the `summed` DataFrame,
    * - age: the “age” column of the `summed` DataFrame,
    * - primaryNeeds: the average value of the “primaryNeeds” columns of all the people that have the same working
    *   status, sex and age, rounded with a scale of 1 (using the `round` function),
    * - work: the average value of the “work” columns of all the people that have the same working status, sex
    *   and age, rounded with a scale of 1 (using the `round` function),
    * - other: the average value of the “other” columns all the people that have the same working status, sex and
    *   age, rounded with a scale of 1 (using the `round` function).
    *
    * Finally, the resulting DataFrame should be sorted by working status, sex and age.
    */
  def timeUsageGrouped(summed: DataFrame): DataFrame = {
    // TODO 4.A Aggregate
    // Finally, we want to compare the average time spent on each activity,
    // for all the combinations of working status, sex and age.
    // We will implement the timeUsageGrouped method which computes
    // the average number of hours spent on each activity, grouped by working status (employed or unemployed),
    // sex and age (young, active or elder), and also ordered by working status, sex and age.
    // The values will be rounded with a scale of 1.
    // Now you can run the project and see what the final DataFrame contains.
    // What do you see when you compare elderly men versus elderly women's time usage?
    // How much time elder people allocate to leisure compared to active people?
    // How much time do active employed people spend to work?

    // ABC
    summed
      .groupBy('working, 'sex, 'age)
      .agg(
        round(avg('primaryNeeds),1).as("primaryNeeds"),
        round(avg('work),1).as("work"),
        round(avg('other),1).as("other")
      )
      .orderBy('working, 'sex, 'age)
  }

  def altTimeUsageGrouped(summed: DataFrame): DataFrame = {
    // TODO 5.A Alternative ways to manipulate data
    // We can also implement the timeUsageGrouped method by using a plain SQL query instead of the DataFrame API.
    // Note that sometimes using the programmatic API to build queries is a lot easier than writing a plain SQL query.
    // Can you think of a previous query that would have been a nightmare to write in plain SQL?
    // Finally, in the last part of this assignment we will explore yet another alternative way to express queries:
    // using typed Datasets instead of untyped DataFrames.
    // Implement the timeUsageSummaryTyped method to convert a DataFrame returned by timeUsageSummary
    // into a DataSet[TimeUsageRow]. The TimeUsageRow is a data type that models the content
    // of a row of a summarized dataset. To achieve the conversion you might want to use the getAs method of Row.
    // This method retrieves a named column of the row and attempts to cast its value to a given type.
    // Then, implement the timeUsageGroupedTyped method that performs the same query as timeUsageGrouped
    // but uses typed APIs as much as possible. Note that not all the operations have a typed equivalent.
    // round is an example of operation that has no typed equivalent: it will return a Column
    // that you will have to turn into a TypedColumn by calling .as[Double].
    // Another example is orderBy, which has no typed equivalent.
    // So, for this operation to work be sure that your Dataset has a schema (because it relies on named columns,
    // and column names are generally lost when using typed transformations).
    // ???
    timeUsageGrouped(summed)
  }

  /**
    * @return Same as `timeUsageGrouped`, but using a plain SQL query instead
    * @param summed DataFrame returned by `timeUsageSumByClass`
    */
  def timeUsageGroupedSql(summed: DataFrame): DataFrame = {
    val viewName = s"summed"
    summed.createOrReplaceTempView(viewName)
    spark.sql(timeUsageGroupedSqlQuery(viewName))
  }

  /** @return SQL query equivalent to the transformation implemented in `timeUsageGrouped`
    * @param viewName Name of the SQL view to use
    */
  def timeUsageGroupedSqlQuery(viewName: String): String = {
    // TODO 4.B Aggregate
    // ABC
    s"SELECT working, sex, age, ROUND(AVG(primaryNeeds),1) as primaryNeeds, ROUND(AVG(work),1) as work," +
      s"  ROUND(AVG(other),1) as other FROM $viewName GROUP BY working, sex, age ORDER BY working, sex, age"
  }

  /**
    * @return A `Dataset[TimeUsageRow]` from the “untyped” `DataFrame`
    * @param timeUsageSummaryDf `DataFrame` returned by the `timeUsageSummary` method
    *
    * Hint: you should use the `getAs` method of `Row` to look up columns and
    * cast them at the same time.
    */
  def timeUsageSummaryTyped(timeUsageSummaryDf: DataFrame): Dataset[TimeUsageRow] = {
    // TODO 4.C Aggregate
    // ABC
    timeUsageSummaryDf.as[TimeUsageRow]
  }

  /**
    * @return Same as `timeUsageGrouped`, but using the typed API when possible
    * @param summed Dataset returned by the `timeUsageSummaryTyped` method
    *
    * Note that, though they have the same type (`Dataset[TimeUsageRow]`), the input
    * dataset contains one element per respondent, whereas the resulting dataset
    * contains one element per group (whose time spent on each activity kind has
    * been aggregated).
    *
    * Hint: you should use the `groupByKey` and `typed.avg` methods.
    */
  def timeUsageGroupedTyped(summed: Dataset[TimeUsageRow]): Dataset[TimeUsageRow] = {
    // TODO 4.D Aggregate
    import org.apache.spark.sql.expressions.scalalang.typed.avg
    // ABC
    def round1(d:Double) = (d * 10).round / 10d

    summed
      .groupByKey(row => (row.working, row.sex, row.age))
      .agg(
        avg(_.primaryNeeds),
        avg(_.work),
        avg(_.other)
      ).map {
      case ((working, sex, age), primaryNeeds, work, other) => TimeUsageRow(working, sex, age,  round1(primaryNeeds), round1(work), round1(other))
    }.orderBy('working, 'sex, 'age)
  }

}

/**
  * Models a row of the summarized data set
  * @param working Working status (either "working" or "not working")
  * @param sex Sex (either "male" or "female")
  * @param age Age (either "young", "active" or "elder")
  * @param primaryNeeds Number of daily hours spent on primary needs
  * @param work Number of daily hours spent on work
  * @param other Number of daily hours spent on other activities
  */
case class TimeUsageRow(
  working: String,
  sex: String,
  age: String,
  primaryNeeds: Double,
  work: Double,
  other: Double
)