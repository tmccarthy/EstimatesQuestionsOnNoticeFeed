package au.id.tmm.estimatesqon.utils

import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import collection.JavaConversions._

object ElementUtils {
  implicit class InstanceElementUtils(val element: Element) {
    lazy val descendents: Set[Element] = descendentsOf(element)
  }
  
  def descendentsOf(element: Element): Set[Element] = {
    val children: Elements = element.children()

    children.flatMap(_.descendents).toSet ++ children
  }
}
