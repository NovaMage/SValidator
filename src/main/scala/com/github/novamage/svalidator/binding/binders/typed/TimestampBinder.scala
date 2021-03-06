package com.github.novamage.svalidator.binding.binders.typed

import java.sql.Timestamp
import java.text.{ParseException, SimpleDateFormat}

import com.github.novamage.svalidator.binding.binders.TypedBinder
import com.github.novamage.svalidator.binding.{BindingConfig, BindingFailure, BindingPass, BindingResult}

/** Performs binding of a [[java.sql.Timestamp Timestamp]] field, parsing dates according to the format specified in the
  * passed configuration
  *
  * @param config The configuration to use for error messages, and format for parsing dates
  */
class TimestampBinder(config: BindingConfig) extends TypedBinder[Timestamp] {
  def bind(fieldName: String, valueMap: Map[String, Seq[String]], bindingMetadata: Map[String, Any]): BindingResult[Timestamp] = {
    val formatter = new SimpleDateFormat(config.dateFormat)
    try {
      BindingPass(new Timestamp(formatter.parse(valueMap(fieldName).headOption.map(_.trim).filterNot(_.isEmpty).get).getTime))
    } catch {
      case ex: ParseException => BindingFailure(fieldName, config.languageConfig.invalidTimestampMessage(fieldName, valueMap.get(fieldName).flatMap(_.headOption).getOrElse("")), Some(ex))
      case ex: NoSuchElementException => BindingFailure(fieldName, config.languageConfig.noValueProvidedMessage(fieldName), Some(ex))
    }
  }
}
