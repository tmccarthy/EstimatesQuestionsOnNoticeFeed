package au.id.tmm.estimatesqon.model

class Portfolio protected (val name: String) {
}

object Portfolio {
  def withName(name: String): Portfolio = new Portfolio(name)
}