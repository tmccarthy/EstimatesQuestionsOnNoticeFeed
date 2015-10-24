package au.id.tmm.estimatesqon.data.databasemodel

import slick.lifted.Tag
import slick.driver.SQLiteDriver.api._

case class PDFLinkBundle(pdfBundleID: Long, linkID: Long, url: String)

class PDFLinkBundles(tag: Tag) extends Table[PDFLinkBundle](tag, "PDFLinkBundles") {

  def pdfBundleID = column[Long]("pdfBundleID")
  def linkID = column[Long]("linkID")
  def url = column[String]("url")

  def * = (pdfBundleID, linkID, url) <> (PDFLinkBundle.tupled, PDFLinkBundle.unapply)

}
