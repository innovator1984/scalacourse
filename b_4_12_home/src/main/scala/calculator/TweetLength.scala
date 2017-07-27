package calculator

object TweetLength {
  final val MaxTweetLength = 140

  def tweetRemainingCharsCount(tweetText: Signal[String]): Signal[Int] = {
    // TODO 1.B Common
    // From Tweet text to remaining char count
    // Your first task is to implement the function tweetRemainingCharsCount in TweetLength.scala.
    // This function takes a Signal[String] of the text of a Tweet being typed,
    // and returns a Signal[Int] with the corresponding number of characters that are left.
    //
    // Note that the Tweet length is not text.length,
    // as this would not properly handle supplementary characters.
    // Use the provided function tweetLength to correctly compute the length of a given text.
    //
    // (Actually, even this is a simplification from reality.
    // The complete specification, which we do not implement in this assignment, can be found here.)
    //
    // Note that the remaining number of characters could very well be negative,
    // if the Tweet text currently contains more than MaxTweetLength characters.
    //
    // Running the application so far
    //
    // Now that you have implemented the first function, you can start running the Web UI. To do so, you need to compile the UI to JavaScript from sbt (you cannot do this from your IDE).
    //
    // In sbt, run:
    //   webUI/fastOptJS
    // If your code compiles, this will produce the JavaScript necessary to run the HTML page in the browser.
    // You can now open the file web-ui/index.html in your favorite browser, and enter text in the first text area.
    // If you implemented tweetRemainingCharsCount correctly, the remaining number of characters
    // should automatically update.
    //
    // From remaining char count to "warning" color
    //
    // For better visual feedback, we also want to display the remaining character count in colors indicating how "safe" we are:
    //
    // If there are 15 or more characters left, the color "green"
    // If there are between 0 and 14 characters left, included, the color "orange"
    // Otherwise (if the remaining count is negative), the color "red"
    //
    // (these are HTML colors).
    // QQQ
    Signal(MaxTweetLength - tweetLength(tweetText()))
  }

  def colorForRemainingCharsCount(remainingCharsCount: Signal[Int]): Signal[String] = {
    // TODO 1.C Common
    // Implement the function colorForRemainingCharsCount, which uses the signal
    // of remaining char count to compute the signal of color.
    //
    // You can now re-run webUI/fastOptJS and refresh your browser.
    // You should see the number of remaining characters changing color accordingly.
    //
    // Root solver for 2nd degree polynomial
    //
    // The second part of the assignment is similar, for a different application:
    // find the real root(s) of a 2nd degree polynomial of the form ax² + bx + c, where a is a non-zero value.
    //
    // We explicitly ask for the intermediary discriminant, which we call Δ:
    //   Δ = b² - 4ac
    // which you should compute in the function computeDelta, from the signals for the coefficients a, b and c.
    // Note that in this case, your output signal depends on several input signals.
    //
    // Then, use Δ to compute the set of roots of the polynomial in computeSolutions.
    // Recall that there can be 0 (when Δ is negative), 1 or 2 roots to such a polynomial,
    // and that can be computed with the formula:
    //   (-b ± √Δ) / 2a
    // After compiling with webUI/fastOptJS and refreshing your brower, you can play with the root solver.
    // QQQ
    Signal {
      if (remainingCharsCount() >= 15) {
        "green"
      } else if (0 <= remainingCharsCount() && remainingCharsCount() <= 14) {
        "orange"
      } else {
        "red"
      }
    }
  }

  /** Computes the length of a tweet, given its text string.
   *  This is not equivalent to text.length, as tweet lengths count the number
   *  of Unicode *code points* in the string.
   *  Note that this is still a simplified view of the reality. Full details
   *  can be found at
   *  https://dev.twitter.com/overview/api/counting-characters
   */
  private def tweetLength(text: String): Int = {
    /* This should be simply text.codePointCount(0, text.length), but it
     * is not implemented in Scala.js 0.6.2.
     */
    if (text.isEmpty) 0
    else {
      text.length - text.init.zip(text.tail).count(
          (Character.isSurrogatePair _).tupled)
    }
  }
}
