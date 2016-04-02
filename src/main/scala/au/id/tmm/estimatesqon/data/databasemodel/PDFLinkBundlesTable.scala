package au.id.tmm.estimatesqon.data.databasemodel

import slick.driver.SQLiteDriver.api._
import slick.lifted.Tag

private[data] case class PDFLinkBundleRow(pdfBundleID: Long, linkID: Long, url: String)

class PDFLinkBundlesTable(tag: Tag) extends Table[PDFLinkBundleRow](tag, "PDFLinkBundles") {

  def pdfBundleID = column[Long]("pdfBundleID")
  def linkID = column[Long]("linkID")
  def url = column[String]("url")

  def pk = primaryKey("PK_PDFs", (pdfBundleID, linkID))

  def * = (pdfBundleID, linkID, url) <> (PDFLinkBundleRow.tupled, PDFLinkBundleRow.unapply)

}
