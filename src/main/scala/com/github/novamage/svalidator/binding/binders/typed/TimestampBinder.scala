package com.github.novamage.svalidator.binding.binders.typed

import java.sql.Timestamp
import java.text.{ParseException, SimpleDateFormat}

import com.github.novamage.svalidator.binding.binders.TypedBinder
import com.github.novamage.svalidator.binding.{BindingConfig, BindingFailure, BindingPass, BindingResult}
import com.github.novamage.svalidator.validation.Localizer

class TimestampBinder(config: BindingConfig) extends TypedBinder[Timestamp] {
  def bind(fieldName: String, valueMap: Map[String, Seq[String]], localizer: Localizer): BindingResult[Timestamp] = {
    val formatter = new SimpleDateFormat(config.dateFormat)
    try {
      BindingPass(new Timestamp(formatter.parse(valueMap(fieldName).headOption.map(_.trim).filterNot(_.isEmpty).get).getTime))
    } catch {
      case ex: ParseException => new BindingFailure(fieldName, config.languageConfig.invalidTimestampMessage(fieldName, valueMap.get(fieldName).flatMap(_.headOption).getOrElse(""), localizer), Some(ex))
      case ex: NoSuchElementException => new BindingFailure(fieldName, config.languageConfig.noValueProvidedMessage(fieldName, localizer), Some(ex))
    }
  }
}
