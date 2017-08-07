package observatory

import com.sksamuel.scrimage.RGBColor
import java.time.LocalDate

import scala.math.{toRadians}

case class Location(lat: Double, lon: Double) {
  // TODO 0.A Common
  lazy val point:MyPoint = MyPoint(toRadians(lat), toRadians(lon))
}

case class Color(red: Int, green: Int, blue: Int) {
  // TODO 0.B Common
  def pixel(alpha: Int = 255) = RGBColor(red, green, blue, alpha).toPixel
}

import scala.math.{toDegrees, atan, sinh, Pi, abs, pow, sin, cos, atan2, sqrt}

case class MyTile(x: Double, y: Double, zoom: Int) {
  // TODO 5.A MyTile
  // QQQ
  lazy val location: Location = Location(
    lat = toDegrees(atan(sinh(Pi * (1.0 - 2.0 * y / (1 << zoom))))),
    lon = x / (1 << zoom) * 360.0 - 180.0)

  def toURI = new java.net.URI("http://tile.openstreetmap.org/" + zoom + "/" + x + "/" + y + ".png")
}

case class MyPoint(phi: Double, lam: Double) {
  // TODO 5.B MyPoint
  // QQQ
  lazy val location:Location = Location(toDegrees(phi), toDegrees(lam))

  /**
    * Added for special case: https://www.coursera.org/learn/scala-capstone/discussions/weeks/2/threads/YY0u6Ax8EeeqzRJFs29uDA
    *
    * @param other Point for distance calculatuion
    * @return distance on earth in meters
    */
  def haversineEarthDistance(other: MyPoint): Double = {
    var r = 6372.8 // mean radius Earth in KM
    r * greatCircleDistance(other) * 1000
  }

  /**
    * https://en.wikipedia.org/wiki/Great-circle_distance#Computational_formulas
    *
    * @param other Point for distance calculatuion
    * @return distance in radians
    */
  def greatCircleDistance(other: MyPoint): Double = {
    val deltaPhi = abs(other.phi - phi)
    val deltaLam = abs(other.lam - lam)

    val a =  pow(sin(deltaPhi / 2), 2) + cos(phi) * cos(other.phi) * pow(sin(deltaLam / 2), 2)
    2 * atan2(sqrt(a), sqrt(1 - a))
  }

}

case class MyColor(red: Int, green: Int, blue: Int) {
  // TODO 5.C MyColor
  // QQQ
  def pixel(alpha: Int = 255) = RGBColor(red, green, blue, alpha).toPixel
}

case class MyJoined(id: String, latitude:Double, longitude: Double,
                    day: Int, month: Int, year: Int, temperature: Double) {
  // TODO 5.D MyJoined
  // QQQ
}

case class MyStationDate(day: Int, month: Int, year: Int){
  // TODO 5.E MyStationDate
  // QQQ
  def toLocalDate = LocalDate.of(year, month, day)
}

case class MyJoinedFormat(date: MyStationDate, location: Location, temperature: Double) {
  // TODO 5.F MyJoinedFormat
  // QQQ
}

case class MyStation(id: String, latitude: Double, longitude: Double) {
  // TODO 5.G MyStation
  // QQQ
}

case class MyTemperatureRecord(id: String, day: Int, month: Int, year: Int, temperature: Double) {
  // TODO 5.H MyTemperatureRecord
  // QQQ
}
