package au.id.tmm.estimatesqon.utils

import au.id.tmm.estimatesqon.utils.StringUtils.InstanceStringUtils
import org.scalatest.FreeSpec

class StringUtilsSpec extends FreeSpec {
  "the string 'The quick brown fox'" - {
    val testString: String = "The quick brown fox"

    "contains (ignoring case) 'QUICK BROWN'" in {
      assert(testString.containsIgnoreCase("QUICK BROWN"))
    }

    "does not contain (ignoring case) 'lazy dogs'" in {
      assert(!testString.containsIgnoreCase("lazy dogs"))
    }

    "contains one of (ignoring case) 'NOT THIS ONE', 'NEITHER THIS ONE', 'QUICK BROWN'" in {
      assert(testString.containsAnyIgnoreCase("NOT THIS ONE", "NEITHER THIS ONE", "QUICK BROWN"))
    }

    "contains none of (ignoring case) 'NOT THIS ONE', 'NEITHER THIS ONE', 'NOR THIS ONE'" in {
      assert(!testString.containsAnyIgnoreCase("NOT THIS ONE", "NEITHER THIS ONE", "NOR THIS ONE"))
    }

    "contains the word (ignoring case) 'BROWN'" in {
      assert(testString.containsWordIgnoreCase("BROWN"))
    }

    "does not contain the word (ignoring case) 'ROW'" in {
      assert(!testString.containsWordIgnoreCase("ROW"))
    }

    "contains the \"word\" (ignoring case) 'QUICK BROWN'" in {
      assert(testString.containsWordIgnoreCase("QUICK BROWN"))
    }

    "contains one of the words (ignoring case) 'NOT', 'THIS', 'ONE', 'BROWN'" in {
      assert(testString.containsAnyWordIgnoreCase("NOT", "THIS", "ONE", "BROWN"))
    }

    "does not contain any of the words (ignoring case) 'NOT', 'THIS', 'ONE', 'ROW'" in {
      assert(!testString.containsAnyWordIgnoreCase("NOT", "THIS", "ONE", "ROW"))
    }
  }
}
