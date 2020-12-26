package com.github.novamage.svalidator.binding.binders.typed

import com.github.novamage.svalidator.binding.binders.{JsonTypedBinder, TypedBinder}
import com.github.novamage.svalidator.binding.{BindingConfig, BindingFailure, BindingPass, BindingResult}
import io.circe.ACursor

/** Performs binding of a [[scala.BigDecimal BigDecimal]] field
  *
  * @param config The configuration to use for error messages
  */
class BigDecimalBinder(config: BindingConfig)
  extends TypedBinder[BigDecimal] with JsonTypedBinder[BigDecimal] {

  def bind(fieldName: String, valueMap: Map[String, Seq[String]], bindingMetadata: Map[String, Any]): BindingResult[BigDecimal] = {

    try {
      BindingPass(BigDecimal(valueMap(fieldName).headOption.map(_.trim).filterNot(_.isEmpty).get))
    } catch {
      case ex: NumberFormatException => BindingFailure(fieldName, config.languageConfig.invalidDecimalMessage(fieldName, valueMap.get(fieldName).flatMap(_.headOption).getOrElse("")), Some(ex))
      case ex: NoSuchElementException => BindingFailure(fieldName, config.languageConfig.noValueProvidedMessage(fieldName), Some(ex))
    }
  }

  override def bindJson(currentCursor: ACursor, fieldName: Option[String], bindingMetadata: Map[String, Any]): BindingResult[BigDecimal] = {
    currentCursor.as[Option[BigDecimal]] match {
      case Left(parsingFailure) =>
        BindingFailure(fieldName, config.languageConfig.invalidDecimalMessage(fieldName.getOrElse(""), currentCursor.focus.map(_.toString()).getOrElse("")), Some(parsingFailure))
      case Right(value) =>
        try {
          BindingPass(value.get)
        } catch {
          case ex: NoSuchElementException => BindingFailure(fieldName, config.languageConfig.noValueProvidedMessage(fieldName.getOrElse("")), Some(ex))
        }
    }
  }

}
