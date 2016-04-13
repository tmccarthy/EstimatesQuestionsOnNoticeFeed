package au.id.tmm.estimatesqon.controller.scraping

import au.id.tmm.estimatesqon.model.{Answer, Estimates}

trait EstimatesScraper {

  def scrapeFrom(estimates: Estimates): List[Answer]

}

object EstimatesScraper {
  def apply(): EstimatesScraper = EstimatesScraperImpl
}