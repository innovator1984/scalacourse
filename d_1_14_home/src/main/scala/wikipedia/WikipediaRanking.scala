package wikipedia

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._

import org.apache.spark.rdd.RDD

case class WikipediaArticle(title: String, text: String) {
  /**
    * @return Whether the text of this article mentions `lang` or not
    * @param lang Language to look for (e.g. "Scala")
    */
  def mentionsLanguage(lang: String): Boolean = text.split(' ').contains(lang)
}

object WikipediaRanking {

  val langs = List(
    "JavaScript", "Java", "PHP", "Python", "C#", "C++", "Ruby", "CSS",
    "Objective-C", "Perl", "Scala", "Haskell", "MATLAB", "Clojure", "Groovy")

  // TODO 1.B Set up Spark
  val conf: SparkConf = {
    // QQQ
    new SparkConf().setAppName("wikipedia").setMaster("local[*]")
  }
  // TODO 1.C Set up Spark
  val sc: SparkContext = {
    // QQQ
    new SparkContext(conf)
  }
  // Hint: use a combination of `sc.textFile`, `WikipediaData.filePath` and `WikipediaData.parse`
  // TODO 2.A Read-in Wikipedia Data
  // There are several ways to read data into Spark.
  // The simplest way to read in data is to convert an existing collection in memory
  // to an RDD using the parallelize method of the Spark context.
  // We have already implemented a method parse in the object WikipediaData object
  // that parses a line of the dataset and turns it into a WikipediaArticle.
  // Create an RDD (by implementing val wikiRdd) which contains the WikipediaArticle objects of articles.
  val wikiRdd: RDD[WikipediaArticle] = {
    // QQQ
    sc.textFile(WikipediaData.filePath).map(WikipediaData.parse).persist()
  }

  def myContains(needle: String, haystack: String): Boolean = haystack.split(" ").contains(needle)
  def findMyLangs(langs: List[String], article: WikipediaArticle) = langs.filter(myContains(_, article.text))

  /** Returns the number of articles on which the language `lang` occurs.
   *  Hint1: consider using method `aggregate` on RDD[T].
   *  Hint2: consider using method `mentionsLanguage` on `WikipediaArticle`
   */
  def occurrencesOfLang(lang: String, rdd: RDD[WikipediaArticle]): Int = {
    // TODO 3.A Compute a ranking of programming languages
    // We will use a simple metric for determining the popularity of a programming language:
    // the number of Wikipedia articles that mention the language at least once.
    // Rank languages attempt #1: rankLangs
    // Computing occurrencesOfLang
    // Start by implementing a helper method occurrencesOfLang which computes the number
    // of articles in an RDD of type RDD[WikipediaArticles] that mention the given language at least once.
    // For the sake of simplicity we check that it least one word (delimited by spaces)
    // of the article text is equal to the given language.
    // QQQ
    rdd.filter(article => myContains(lang, article.text)).count().toInt
  }

  /* (1) Use `occurrencesOfLang` to compute the ranking of the languages
   *     (`val langs`) by determining the number of Wikipedia articles that
   *     mention each language at least once. Don't forget to sort the
   *     languages by their occurrence, in decreasing order!
   *
   *   Note: this operation is long-running. It can potentially run for
   *   several seconds.
   */
  def rankLangs(langs: List[String], rdd: RDD[WikipediaArticle]): List[(String, Int)] = {
    // TODO 3.B Compute a ranking of programming languages
    // Computing the ranking, rankLangs
    // Using occurrencesOfLang, implement a method rankLangs
    // which computes a list of pairs where the second component of the pair is the number of articles
    // that mention the language (the first component of the pair is the name of the language).
    // An example of what rankLangs might return might look like this, for example:
    //   List(("Scala",999999),("JavaScript",1278),("LOLCODE",982),("Java",42))
    // The list should be sorted in descending order. That is, according to thisranking,
    // the pair with the highest second component (the count) should be thefirst element of the list.
    // Pay attention to roughly how long it takes to run this part! (It should take tens of seconds.)
    // QQQ
    langs.map(lang => (lang, occurrencesOfLang(lang, rdd))).sortBy(-_._2)
  }

  /* Compute an inverted index of the set of articles, mapping each language
   * to the Wikipedia pages in which it occurs.
   */
  def makeIndex(langs: List[String], rdd: RDD[WikipediaArticle]): RDD[(String, Iterable[WikipediaArticle])] = {
    // TODO 3.C Compute a ranking of programming languages
    // Compute an inverted index
    // An inverted index is an index data structure storing a mapping from content,
    // such as words or numbers, to a set of documents.
    // In particular, the purpose of an inverted index is to allow fast full text searches.
    // In our use-case, an inverted index would be useful for mapping from the names of programming languages
    // to the collection of Wikipedia articles that mention the name at least once.
    // To make working with the dataset more efficient and more convenient,
    // implement a method that computes an "inverted index" which maps programming language names
    // to the Wikipedia articles on which they occur at least once.
    // Implement method makeIndex which returns an RDD of the following type:
    // RDD[(String, Iterable[WikipediaArticle])].
    // This RDD contains pairs, such that for each language in the given langs list there is at most one pair.
    // Furthermore, the second component of each pair (the Iterable) contains
    // the WikipediaArticles that mention the language at least once.
    // Hint: You might want to use methods flatMap and groupByKey on RDD for this part.
    // QQQ
    rdd.flatMap(article => { findMyLangs(langs, article).map(lang => (lang, article)) }).groupByKey
  }

  /* (2) Compute the language ranking again, but now using the inverted index. Can you notice
   *     a performance improvement?
   *
   *   Note: this operation is long-running. It can potentially run for
   *   several seconds.
   */
  def rankLangsUsingIndex(index: RDD[(String, Iterable[WikipediaArticle])]): List[(String, Int)] = {
    // TODO 3.D Compute a ranking of programming languages
    // Computing the ranking, rankLangsUsingIndex
    // Use the makeIndex method implemented in the previous part
    // to implement a faster method for computing the language ranking.
    // Like in part 1, rankLangsUsingIndex should compute a list of pairs
    // where the second component of the pair is the number of articles
    // that mention the language (the first component of the pair is the name of the language).
    // Again, the list should be sorted in descending order. That is, according to this ranking,
    // the pair with the highest second component (the count) should be the first element of the list.
    // Hint: method mapValues on PairRDD could be useful for this part.
    // Can you notice a performance improvement over attempt #2? Why?
    // QQQ
    index.map({
        case (lang, articles) => (lang, articles.size)
      }).sortBy(-_._2).collect().toList
  }

  /* (3) Use `reduceByKey` so that the computation of the index and the ranking are combined.
   *     Can you notice an improvement in performance compared to measuring *both* the computation of the index
   *     and the computation of the ranking? If so, can you think of a reason?
   *
   *   Note: this operation is long-running. It can potentially run for
   *   several seconds.
   */
  def rankLangsReduceByKey(langs: List[String], rdd: RDD[WikipediaArticle]): List[(String, Int)] = {
    // TODO 3.E Compute a ranking of programming languages
    // Rank languages attempt #3: rankLangsReduceByKey
    // In the case where the inverted index from above is only used
    // for computing the ranking and for no other task (full-text search, say),
    // it is more efficient to use the reduceByKey method to compute the ranking directly,
    // without first computing an inverted index. Note that the reduceByKey method is only
    // defined for RDDs containing pairs (each pair is interpreted as a key-value pair).
    // Implement the rankLangsReduceByKey method, this time computing the ranking
    // without the inverted index, using reduceByKey.
    // Like in part 1 and 2, rankLangsReduceByKey should compute a list of pairs
    // where the second component of the pair is the number of articles that mention
    // the language (the first component of the pair is the name of the language).
    // Again, the list should be sorted in descending order. That is, according to this ranking,
    // the pair with the highest second component (the count) should be the first element of the list.
    // Can you notice an improvement in performance compared to measuring both the computation
    // of the index and the computation of the ranking as we did in attempt #2? If so, can you think of a reason?
    // QQQ
    rdd.flatMap(article => {
      findMyLangs(langs, article)
        .map(lang => (lang, 1))
    }).reduceByKey(_ + _).sortBy(-_._2).collect().toList
  }

  def main(args: Array[String]) {
    // TODO 1.A Set up Spark
    // For the sake of simplified logistics, we'll be running Spark in "local" mode.
    // This means that your full Spark application will be run on one node, locally, on your laptop.
    // To start, we need a SparkContext. A SparkContext is the "handle" to your cluster.
    // Once you have a SparkContext, you can use it to create and populate RDDs with data.
    // To create a SparkContext, you need to first create a SparkConfig instance.
    // A SparkConfig represents the configuration of your Spark application.
    // It's here that you must specify that you intend to run your application in "local" mode.
    // You must also name your Spark application at this point. For help, see the Spark API Docs.
    // Configure your cluster to run in local mode by implementing val conf and val sc.

    /* Languages ranked according to (1) */
    val langsRanked: List[(String, Int)] = timed("Part 1: naive ranking", rankLangs(langs, wikiRdd))

    /* An inverted index mapping languages to wikipedia pages on which they appear */
    def index: RDD[(String, Iterable[WikipediaArticle])] = makeIndex(langs, wikiRdd)

    /* Languages ranked according to (2), using the inverted index */
    val langsRanked2: List[(String, Int)] = timed("Part 2: ranking using inverted index", rankLangsUsingIndex(index))

    /* Languages ranked according to (3) */
    val langsRanked3: List[(String, Int)] = timed("Part 3: ranking using reduceByKey", rankLangsReduceByKey(langs, wikiRdd))

    /* Output the speed of each ranking */
    println(timing)
    sc.stop()
  }

  val timing = new StringBuffer
  def timed[T](label: String, code: => T): T = {
    val start = System.currentTimeMillis()
    val result = code
    val stop = System.currentTimeMillis()
    timing.append(s"Processing $label took ${stop - start} ms.\n")
    result
  }
}
