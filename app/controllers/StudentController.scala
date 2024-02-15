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
      Student(1, "Jeremy", 34),
      Student(1, "Alice", 20),
      Student(2, "Bob", 22),
      Student(3, "Charlie", 23)
    )
    Ok(Json.toJson(students))
  }
}
