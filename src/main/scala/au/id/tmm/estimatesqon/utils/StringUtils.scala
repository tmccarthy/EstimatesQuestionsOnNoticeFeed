package au.id.tmm.estimatesqon.utils

object StringUtils {
  implicit class InstanceStringUtils(val string: String) {
    def containsIgnoreCase(subString: String): Boolean = string.toLowerCase.contains(subString.toLowerCase)

    def containsAnyIgnoreCase(subStrings: String*): Boolean = {
      subStrings.collectFirst {
        case subString if string.containsIgnoreCase(subString) => true
      } getOrElse false
    }

    def containsWordIgnoreCase(word: String): Boolean = {
      string.matches("(?i).*\\b" + word + "\\b.*")
    }

    def containsAnyWordIgnoreCase(words: String*): Boolean = {
      words.collectFirst {
        case word if string.containsWordIgnoreCase(word) => true
      } getOrElse false
    }
  }
}
