package com.github.novamage.svalidator.binding.binders.typed

import com.github.novamage.svalidator.binding.binders.ITypeBinder
import java.sql.Timestamp
import com.github.novamage.svalidator.binding.{BindingFailure, BindingConfig, BindingPass, BindingResult}
import java.text.{ParseException, SimpleDateFormat}

class TimestampBinder(config: BindingConfig) extends ITypeBinder[Timestamp] {
  def bind(fieldName: String, valueMap: Map[String, Seq[String]]): BindingResult[Timestamp] = {
    val formatter = new SimpleDateFormat(config.dateFormat)
    try {
      BindingPass(new Timestamp(formatter.parse(valueMap(fieldName).head).getTime))
    } catch {
      case ex: ParseException => new BindingFailure(fieldName, config.languageConfig.invalidTimestampMessage(fieldName, valueMap(fieldName).head.toString))
      case ex: NoSuchElementException => new BindingFailure(fieldName, config.languageConfig.noValueProvidedMessage(fieldName))
    }
  }
}
