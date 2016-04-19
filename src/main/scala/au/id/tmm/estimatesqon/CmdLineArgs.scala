package au.id.tmm.estimatesqon

import java.io.File
import java.nio.file.{Path, Paths}

import au.id.tmm.estimatesqon.FailureCondition.FailureCondition
import scopt.OptionParser

case class CmdLineArgs (errorEmail: Option[String] = None,
                        oAuthCredsFile: Path = CmdLineArgs.DEFAULT_OAUTH_FILE)

object CmdLineArgs {
  val DEFAULT_OAUTH_FILE = Paths.get("twitterBot.oauth")
}

object parseArgs {
  def apply(args: Array[String]): Either[FailureCondition, CmdLineArgs] = parser
    .parse(args, Right(CmdLineArgs()))
    .getOrElse(Left(FailureCondition.INVALID_FLAG_PROVIDED))

  private val parser = new OptionParser[Either[FailureCondition, CmdLineArgs]]("QonFeed") {
    head("QonFeed")

    note("Options:")

    help("help")
      .text("Prints this usage text.")
      .action {
        case (unit, accumulatedArgs) => Left(FailureCondition.HELP_REQUESTED)
      }

    opt[String]('e', "errorEmail")
      .valueName("ERROR EMAIL")
      .text("an email address to which Exceptions during execution are sent")
      .action {
        case (errorEmail, accumulatedArgs) => accumulatedArgs.right.map(_.copy(errorEmail = Some(errorEmail)))
      }

    opt[File]('a', "oauthCredsFile")
      .valueName("TWITTER CREDENTIALS FILE")
      .text("a file containing OAuth credentials for the twitterbot")
      .action {
        case (oauthCredsFile, accumulatedArgs) => accumulatedArgs.right.map(_.copy(oAuthCredsFile = oauthCredsFile.toPath))
      }

    override def terminate(exitState: Either[String, Unit]): Unit = {}
  }
}

object FailureCondition extends Enumeration {

  type FailureCondition = Value

  val INVALID_FLAG_PROVIDED, HELP_REQUESTED = Value
}
