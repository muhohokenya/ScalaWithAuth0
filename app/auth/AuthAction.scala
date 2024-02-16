package auth

import org.apache.commons.logging.Log

import javax.inject.Inject
import pdi.jwt._
import play.api.http.HeaderNames
import play.api.mvc._
import play.libs.Json
import play.twirl.api.TwirlHelperImports.twirlJavaCollectionToScala

import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters.IteratorHasAsScala
import scala.util.{Failure, Success}

// A custom request type to hold our JWT claims, we can pass these on to the
// handling action
case class UserRequest[A]
(jwt: JwtClaim, token: String, request: Request[A],permissions: Seq[String]) extends WrappedRequest[A](request)

class AuthAction @Inject()
(bodyParser: BodyParsers.Default, authService: AuthService)
(implicit ec: ExecutionContext)
  extends ActionBuilder[UserRequest, AnyContent] {

  override def parser: BodyParser[AnyContent] = bodyParser
  override protected def executionContext: ExecutionContext = ec

  // A regex for parsing the Authorization header value
  private val headerTokenRegex = """Bearer (.+?)""".r

  // Called when a request is invoked. We should validate the bearer token here
  // and allow the request to proceed if it is valid.
  override def invokeBlock[A](request: Request[A], block: UserRequest[A]
    => Future[Result]): Future[Result] =
    extractBearerToken(request) map { token =>
      authService.validateJwt(token) match {
        case Success(claim) =>



          // Extract permissions from the claim
          val permissions = Json.parse(claim.content).get("permissions").elements().asScala.map(_.asText()).toSeq
          // Check if the required permissions are present
          if (hasRequiredPermissions(permissions)) {
            block(UserRequest(claim, token, request, permissions))
          } else {
            Future.successful(Results.Forbidden("Insufficient permissions"))
          }
        // token was valid - proceed!

        case Failure(t) => Future.successful(Results.Unauthorized(t.getMessage))
        // token was invalid - return 401
      }
    } getOrElse Future.successful(Results.Unauthorized)     // no token was sent - return 401



  // Check if the user has the required permissions
  private def hasRequiredPermissions(permissions: Seq[String]): Boolean = {
    // Define your required permissions here
    val requiredPermissions = Seq("read:matrix", "write:matrix")
    //comment
    requiredPermissions.forall(permissions.contains)
  }


  // Helper for extracting the token value
  private def extractBearerToken[A](request: Request[A]): Option[String] =
    request.headers.get(HeaderNames.AUTHORIZATION) collect {
      case headerTokenRegex(token) => token
    }
}