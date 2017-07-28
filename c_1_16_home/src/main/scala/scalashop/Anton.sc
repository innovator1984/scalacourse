object MyHelper {
  implicit class MyString(val str: String) {
    def say(): Unit = println(str)
  }
  implicit class MyString2(val str: String) {
    def say2(): Unit = println(str)
  }
}

import MyHelper._
// import scala.Predef._
val str = "abracadabra"
str.say
str.say2
