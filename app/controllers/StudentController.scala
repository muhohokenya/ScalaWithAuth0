package controllers
import com.typesafe.config.ConfigFactory
import models.Student
import play.api.libs.json.Json
import play.api.mvc._
import auth.AuthAction
import javax.inject.Inject

class StudentController @Inject()
(cc: ControllerComponents,
 authAction: AuthAction
)

  extends AbstractController(cc) {

  def getStudents: Action[AnyContent] = authAction {
    val config = ConfigFactory.load()
    val students = List(
      Student(1, "Jack", 32),
      Student(1, "Alice", 30),
      Student(2, "Bob", 24),
      Student(3, "Charlie", 23),
      Student(3, "Samuel", 49)
    )
    Ok(Json.toJson(students))
  }
}
