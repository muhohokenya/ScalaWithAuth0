package models
import play.api.libs.json._

case class Student(id: Long, name: String, age: Int)

object Student {
  implicit val studentFormat: OFormat[Student] = Json.format[Student]
}
