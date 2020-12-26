package com.github.novamage.svalidator.binding.binders.typed

import java.sql.Timestamp
import java.text.{ParseException, SimpleDateFormat}
import com.github.novamage.svalidator.binding.binders.{JsonTypedBinder, TypedBinder}
import com.github.novamage.svalidator.binding.{BindingConfig, BindingFailure, BindingPass, BindingResult}
import io.circe.ACursor

/** Performs binding of a [[java.sql.Timestamp Timestamp]] field, parsing dates according to the format specified in the
  * passed configuration
  *
  * @param config The configuration to use for error messages, and format for parsing dates
  */
class TimestampBinder(config: BindingConfig)
  extends TypedBinder[Timestamp] with JsonTypedBinder[Timestamp] {

  private val formatter = new SimpleDateFormat(config.dateFormat)

  def bind(fieldName: String, valueMap: Map[String, Seq[String]], bindingMetadata: Map[String, Any]): BindingResult[Timestamp] = {
    try {
      BindingPass(new Timestamp(formatter.parse(valueMap(fieldName).headOption.map(_.trim).filterNot(_.isEmpty).get).getTime))
    } catch {
      case ex: ParseException => BindingFailure(fieldName, config.languageConfig.invalidTimestampMessage(fieldName, valueMap.get(fieldName).flatMap(_.headOption).getOrElse("")), Some(ex))
      case ex: NoSuchElementException => BindingFailure(fieldName, config.languageConfig.noValueProvidedMessage(fieldName), Some(ex))
    }
  }

  override def bindJson(currentCursor: ACursor, fieldName: Option[String], bindingMetadata: Map[String, Any]): BindingResult[Timestamp] = {
    currentCursor.as[Option[String]] match {
      case Left(parsingFailure) =>
        BindingFailure(fieldName, config.languageConfig.invalidJsonStringMessage(fieldName.getOrElse(""), currentCursor.focus.map(_.toString()).getOrElse("")), Some(parsingFailure))
      case Right(value) =>
        try {
          value.map(_.trim).filterNot(_.isEmpty) match {
            case None => BindingFailure(fieldName, config.languageConfig.invalidNonEmptyTextMessage(fieldName.getOrElse("")), Some(new NoSuchElementException))
            case Some(trimmedString) => BindingPass(new Timestamp(formatter.parse(trimmedString).getTime))
          }
        } catch {
          case ex: ParseException => BindingFailure(fieldName, config.languageConfig.invalidTimestampMessage(fieldName.getOrElse(""), value.getOrElse("")), Some(ex))
          case ex: NoSuchElementException => BindingFailure(fieldName, config.languageConfig.invalidNonEmptyTextMessage(fieldName.getOrElse("")), Some(ex))
        }
    }
  }

}
