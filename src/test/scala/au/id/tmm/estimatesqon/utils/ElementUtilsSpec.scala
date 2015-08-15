package au.id.tmm.estimatesqon.utils

import au.id.tmm.estimatesqon.utils.ElementUtils.InstanceElementUtils

import org.jsoup.nodes.Element
import org.jsoup.parser.Tag
import org.scalatest.FreeSpec

class ElementUtilsSpec extends FreeSpec {
  "an element with one child that also has a child" - {
    val topElement = new Element(Tag.valueOf("div"), "")
    val middleElement = new Element(Tag.valueOf("div"), "")
    val bottomElement = new Element(Tag.valueOf("div"), "")

    topElement.appendChild(middleElement)
    middleElement.appendChild(bottomElement)

    "should have both of those elements in its descendents" in {
      assert(topElement.descendents.contains(middleElement))
      assert(topElement.descendents.contains(bottomElement))
    }
  }
}
