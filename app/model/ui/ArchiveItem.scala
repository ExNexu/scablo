package model.ui

import org.joda.time.{ DateTime, DateTimeFieldType }
import org.joda.time.format.DateTimeFormat

/**
  * A class representing archive items shown in the archive widget of the page
  *
  * @author Stefan Bleibinhaus
  *
  */
case class ArchiveItem(
    val timeDisplay: String,
    val link: String,
    val year: Int,
    val month: Option[Int],
    val count: Int) {

}

object ArchiveItem {
  private val monthStringformat = DateTimeFormat.forPattern("MMMMM")

  def apply(year: Int, count: Int): ArchiveItem =
    ArchiveItem(year.toString, "/" + year.toString, year, None, count)

  def apply(year: Int, month: Int, count: Int): ArchiveItem =
    ArchiveItem(
      monthToDisplayString(month),
      "/" + year.toString + "/" + monthToLinkString(month),
      year,
      Some(month),
      count)

  private def monthToDisplayString(month: Int): String = {
    val fakeDate = new DateTime().toMutableDateTime()
    fakeDate.set(DateTimeFieldType.monthOfYear, month)
    monthStringformat.print(fakeDate)
  }

  private def monthToLinkString(month: Int): String = if (month < 10) "0" + month else month.toString
}