package au.id.tmm.estimatesqon

import java.nio.file.Paths

class CmdLineArgsSpec extends StandardProjectSpec {

  behaviour of "the command line args parser"

  it should "return the default args when not supplied with arguments" in {
    val parsedArgs = parseArgs(Array())

    assert(parsedArgs.right.get.errorEmail === None)
    assert(parsedArgs.right.get.oAuthCredsFile === CmdLineArgs.DEFAULT_OAUTH_FILE)
  }

  it should "correctly parse the error email using the short flag" in {
    val parsedArgs = parseArgs(Array("-e", "errors@example.com"))

    assert(parsedArgs.right.get.errorEmail === Some("errors@example.com"))
  }

  it should "correctly parse the error email using the full flag" in {
    val parsedArgs = parseArgs(Array("--errorEmail", "errors@example.com"))

    assert(parsedArgs.right.get.errorEmail === Some("errors@example.com"))
  }

  it should "correctly parse the oauth file location using the short flag" in {
    val parsedArgs = parseArgs(Array("-a", "theCreds.oauth"))

    assert(parsedArgs.right.get.oAuthCredsFile === Paths.get("theCreds.oauth"))
  }

  it should "correctly parse the oauth file location using the full flag" in {
    val parsedArgs = parseArgs(Array("--oauthCredsFile", "theCreds.oauth"))

    assert(parsedArgs.right.get.oAuthCredsFile === Paths.get("theCreds.oauth"))
  }

  it should "fail if provided with any invalid flags" in {
    val parsedArgs = parseArgs(Array("blahdi", "blahdi", "blah"))

    assert(parsedArgs.left.get == FailureCondition.INVALID_FLAG_PROVIDED)
  }

  it should "fail if help is requested" in {
    val parsedArgs = parseArgs(Array("--help"))

    assert(parsedArgs.left.get == FailureCondition.HELP_REQUESTED)
  }

  it should "fail if help is requested even if other valid flags are provided" in {
    val parsedArgs = parseArgs(Array("--help", "-e", "email@example.com"))

    assert(parsedArgs.left.get == FailureCondition.HELP_REQUESTED)
  }

}
