package au.id.tmm.estimatesqon.model

case class Portfolio protected (val name: String) {
}

object Portfolio {
  def withName(name: String): Portfolio = new Portfolio(name)
}