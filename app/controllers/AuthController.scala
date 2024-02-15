package controllers

import javax.inject._
import play.api.libs.json.{JsError, JsObject, JsValue, Json, OFormat}
import play.api.mvc._
import play.api.libs.ws._
import scala.concurrent.{ExecutionContext, Future}

case class LoginRequest(username: String, password: String)

object LoginRequest {
  implicit val format: OFormat[LoginRequest] = Json.format[LoginRequest]
}

@Singleton
class AuthController @Inject()
(ws: WSClient, val controllerComponents: ControllerComponents)
(implicit ec: ExecutionContext)
  extends BaseController {
  private val clientId = "7yFzRGObTDl5dOnFzaGMZfn87IVYRtF5"
  private val clientSecret = "Y4NPnuRW17Xp0CqXru-cvasBIz_mT9nTQnHlIk6MkDAa-9UuSe7H5Dt793iAjppM"
  private val audience = "http://localhost:9000/identifier"
  private val grantType = "password"
//  private val username = "muhohoweb@gmail.com"
//  private val password = "gitpass2016@2024"

  def login(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[LoginRequest].fold(
      // Validation failure
      errors => {
        Future.successful(BadRequest(Json.obj("message" -> "Invalid request", "details" -> JsError.toJson(errors))))
      },

      loginRequest => {
        val username = loginRequest.username
        val password = loginRequest.password

        if (password.isEmpty || username.isEmpty) {
          Future.successful(BadRequest(Json.obj("message" -> "All fields are required")))
        } else {
          getToken(password, username).map {
            case Right((accessToken, expiresIn, tokenType)) =>
              Ok(Json.obj(
                "access_token" -> accessToken,
                "expires_in" -> expiresIn,
                "token_type" -> tokenType
              ))
            case Left((statusCode, errorMessage)) =>
              BadRequest(Json.obj("error" -> errorMessage)).withHeaders("WWW-Authenticate" -> "Basic realm=\"Your Realm\"")
          }.recover {
            case ex: Throwable => InternalServerError(Json.obj("message" -> "Failed to obtain token"))
          }
        }
      }
    )
  }

  private def getToken(password: String, username: String): Future[Either[(Int, String), (String, Long, String)]] = {
    val url = "https://dev-lbyr1g2rm84dajwz.us.auth0.com/oauth/token"
    val payload: JsObject = Json.obj(
      "client_id" -> clientId,
      "client_secret" -> clientSecret,
      "audience" -> audience,
      "grant_type" -> grantType,
      "username" -> username,
      "password" -> password
    )

    ws.url(url)
      .post(payload)
      .map { response =>
        response.status match {
          case 200 =>
            val accessToken = (response.json \ "access_token").as[String]
            val expiresIn = (response.json \ "expires_in").as[Long]
            val tokenType = (response.json \ "token_type").as[String]
            Right((accessToken, expiresIn, tokenType))
          case _ =>
            val errorMessage = (response.json \ "error_description").asOpt[String].getOrElse("Invalid username or password")
            Left(401, errorMessage)
        }
      }
  }


}
