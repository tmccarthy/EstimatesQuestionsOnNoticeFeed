package au.id.tmm.estimatesqon.data.databasemodel

import java.io.PrintStream

import slick.lifted.Tag
import slick.driver.SQLiteDriver.api._

private[data] case class PDFLinkBundleRow(pdfBundleID: Long, linkID: Long, url: String)

class PDFLinkBundlesTable(tag: Tag) extends Table[PDFLinkBundleRow](tag, "PDFLinkBundles") {

  val steam: PrintStream = System.out;

  def pdfBundleID = column[Long]("pdfBundleID")
  def linkID = column[Long]("linkID")
  def url = column[String]("url")

  def * = (pdfBundleID, linkID, url) <> (PDFLinkBundleRow.tupled, PDFLinkBundleRow.unapply)

}
