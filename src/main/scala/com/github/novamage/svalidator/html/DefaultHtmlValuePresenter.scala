package com.github.novamage.svalidator.html

import java.sql.Timestamp
import java.text.{DecimalFormat, NumberFormat, SimpleDateFormat}
import java.util.Locale

import com.github.novamage.svalidator.utils.TypeBasedEnumeration

/** Default implementation for [[com.github.novamage.svalidator.html.HtmlValuePresenter HtmlValuePresenter]]
  */
object DefaultHtmlValuePresenter extends HtmlValuePresenter {

  def getValueToPresentFor(propertyValue: Any): Option[String] = {
    propertyValue match {
      case Some(optionalValue) => getValueToPresentFor(optionalValue)
      case None => None
      case date: Timestamp => Some(timestampFormatter.format(date))
      case decimal: BigDecimal => Some(moneyFormatter.format(decimal))
      case enumValue: Enumeration#Value => Some(enumValue.id.toString)
      case objectEnumValue: TypeBasedEnumeration[_]#Value => Some(objectEnumValue.id.toString)
      case anythingElse => Some(anythingElse.toString)
    }
  }

  private lazy val timestampFormatter = new SimpleDateFormat("dd/MM/yyyy")

  private val moneyFormatter = {
    val temp = NumberFormat.getCurrencyInstance(Locale.US).asInstanceOf[DecimalFormat]
    temp.setNegativePrefix("-")
    temp.setNegativeSuffix("")
    temp.setPositivePrefix("")
    temp.setPositiveSuffix("")
    temp.setGroupingUsed(false)
    temp
  }
}
