package calculator

sealed abstract class Expr
final case class Literal(v: Double) extends Expr
final case class Ref(name: String) extends Expr
final case class Plus(a: Expr, b: Expr) extends Expr
final case class Minus(a: Expr, b: Expr) extends Expr
final case class Times(a: Expr, b: Expr) extends Expr
final case class Divide(a: Expr, b: Expr) extends Expr

object Calculator {
  // TODO 1.A Common
  // In this assignment, you will use Function Reactive Programming (FRP),
  // with the Signal[A] class that you have seen in the lectures, to implement a spreadsheet-like calculator.
  // In this calculator, cells can depend on the value of other cells, and are recomputed automatically
  // when the latter change.
  // The User Interface (UI) for the programs in this assignment are Web-based,
  // i.e., they run in an HTML page in your Web browser.
  // To compile your code to runnable JavaScript, we use Scala.js, the Scala to JavaScript compiler.
  // We have all set it up for you in the assignment template, but this will change the way you run the program.
  //
  // Tweet length monitoring
  // As you probably know, Tweets (messages on Twitter) are limited to 140 characters.
  // When typing a Tweet, it is very helpful to receive instantaneous visual feedback
  // on the number of characters you have left.
  // The traditional way of implementing this visual feedback is to set up
  // an onchange event handler (a callback) on the text area.
  // In the event handler, we imperatively ask for the text, compute the length,
  // then write back the computed remaining characters.
  // In FRP, we use Signals to abstract away the mutable nature of the UI (both inputs and outputs)
  // while working on the logic that binds output to input, which is functional.
  //

  def computeValues(namedExpressions: Map[String, Signal[Expr]]): Map[String, Signal[Double]] = {
    // TODO 2.A Spreadsheet-like calculator
    //
    // Now that you are warmed up manipulating Signals, it is time to proceed with the original goal
    // of this assignment: the spreadsheet-like calculator.
    //
    // To simplify things a bit, we use a list of named variables,
    // rather than a 2-dimensional table of cells. In the Web UI, there is a fixed list of 10 variables,
    // but your code should be able to handle an arbitrary number of variables.
    //
    // The main function is the following:
    //   def computeValues(namedExpressions: Map[String, Signal[Expr]]) : Map[String, Signal[Double]]
    //
    // This function takes as input a map from variable name to expressions of their values.
    // Since the expression is derived from the text entered by the user, it is a Signal.
    // The Expr abstract data type is defined as follows:
    // sealed abstract class Expr
    // final case class Literal(v: Double) extends Expr
    // final case class Ref(name: String) extends Expr
    // final case class Plus(a: Expr, b: Expr) extends Expr
    // final case class Minus(a: Expr, b: Expr) extends Expr
    // final case class Times(a: Expr, b: Expr) extends Expr
    // final case class Divide(a: Expr, b: Expr) extends Expr
    //
    // The Ref(name) case class represents a reference to another variable in the map namedExpressions.
    // The other kinds of expressions have the obvious meaning.
    //
    // The function should return another map from the same set of variable names to their actual values,
    // computed from their expressions.

    // HACK git hub dot com
    // /izzyleung/Principles-Of-Reactive-Programming
    // /blob/master/calculator/src/main/scala/calculator/Calculator.scala

    // QQQ
    var result = Map[String, Signal[Double]]()
    namedExpressions.foreach { case(k, v) =>
      result += k -> Signal(eval(v(), namedExpressions - k)) }
    result
  }

  def eval(expr: Expr, references: Map[String, Signal[Expr]]): Double = {
    // TODO 2.B Spreadsheet-like calculator
    // Implement the function computeValues, and its helper eval, while keeping the following things in mind:
    //   * Refs to other variables could cause cyclic dependencies (e.g., a = b + 1 and b = 2 * a.
    // Such cyclic dependencies are considered as errors (failing to detect this will cause infinite loops).
    //   * Referencing a variable that is not in the map is an error.
    //
    // Such errors should be handled by returning Double.NaN, the Not-a-Number value.
    //
    // It is not asked that, when given an expression for a = b + 1,
    // you compute the resulting value signal for a in terms of the value signal for b.
    // It is OK to compute it from the expression signal for b.
    //
    // Once all of this is done, you can, once again, compile to JavaScript
    // with webUI/fastOptJS and refresh your browser. You can now play with the simplified spreadsheet.
    // Observe that, when you type in some expression, the values recomputed as a result are highlighted for 1.5s.
    // If all ten values are highlighted every time you modify something, then something is wrong with
    // the way you construct your signals.
    //
    // Notice that, as you modify dependencies between variables in the expressions,
    // the dependencies between signals adapt dynamically.

    // QQQ
    expr match {
      case Literal(v) => v
      case Ref(name) => eval(getReferenceExpr(name, references), references)
      case Plus(a, b) => eval(a, references) + eval(b, references)
      case Minus(a, b) => eval(a, references) - eval(b, references)
      case Times(a, b) => eval(a, references) * eval(b, references)
      case Divide(a, b) => eval(a, references) / eval(b, references)
    }
  }

  /** Get the Expr for a referenced variables.
   *  If the variable is not known, returns a literal NaN.
   */
  private def getReferenceExpr(name: String,
      references: Map[String, Signal[Expr]]) = {
    references.get(name).fold[Expr] {
      Literal(Double.NaN)
    } { exprSignal =>
      exprSignal()
    }
  }
}
