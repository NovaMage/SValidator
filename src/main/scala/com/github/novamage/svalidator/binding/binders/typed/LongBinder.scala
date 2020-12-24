package com.github.novamage.svalidator.binding.binders.typed

import com.github.novamage.svalidator.binding.binders.{JsonTypedBinder, TypedBinder}
import com.github.novamage.svalidator.binding.{BindingConfig, BindingFailure, BindingPass, BindingResult}
import io.circe.ACursor

/** Performs binding of a long field
  *
  * @param config The configuration to use for error messages
  */
class LongBinder(config: BindingConfig)
  extends TypedBinder[Long] with JsonTypedBinder[Long] {

  def bind(fieldName: String, valueMap: Map[String, Seq[String]], bindingMetadata: Map[String, Any]): BindingResult[Long] = {
    try {
      BindingPass(valueMap(fieldName).headOption.map(_.trim).filterNot(_.isEmpty).map(_.toLong).get)
    } catch {
      case ex: NumberFormatException => BindingFailure(fieldName, config.languageConfig.invalidLongMessage(fieldName, valueMap.get(fieldName).flatMap(_.headOption).getOrElse("")), Some(ex))
      case ex: NoSuchElementException => BindingFailure(fieldName, config.languageConfig.noValueProvidedMessage(fieldName), Some(ex))
    }
  }

  override def bindJson(currentCursor: ACursor, fieldName: String, bindingMetadata: Map[String, Any]): BindingResult[Long] = {
    currentCursor.as[Option[Long]] match {
      case Left(parsingFailure) =>
        BindingFailure(fieldName, config.languageConfig.invalidLongMessage(fieldName, currentCursor.focus.map(_.toString()).getOrElse("")), Some(parsingFailure))
      case Right(value) =>
        try {
          BindingPass(value.get)
        } catch {
          case ex: NoSuchElementException => BindingFailure(fieldName, config.languageConfig.noValueProvidedMessage(fieldName), Some(ex))
        }
    }
  }

}
