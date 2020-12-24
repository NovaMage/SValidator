package com.github.novamage.svalidator.binding.binders.typed

import com.github.novamage.svalidator.binding.binders.{JsonTypedBinder, TypedBinder}
import com.github.novamage.svalidator.binding.{BindingConfig, BindingFailure, BindingPass, BindingResult}
import io.circe.ACursor

/** Performs binding of a string field.  Only strings that are non-whitespace and non-empty will be successfully bound.
  * However, strings are not trimmed if they aren't full whitespace.
  *
  * @param config The configuration to use for error messages
  */
class StringBinder(config: BindingConfig)
  extends TypedBinder[String] with JsonTypedBinder[String] {

  def bind(fieldName: String, valueMap: Map[String, Seq[String]], bindingMetadata: Map[String, Any]): BindingResult[String] = {

    val headOption = valueMap.get(fieldName) match {
      case None => None
      case Some(list) => list.headOption
    }
    headOption.map(_.trim).filterNot(_.isEmpty) match {
      case None => BindingFailure(fieldName, config.languageConfig.invalidNonEmptyTextMessage(fieldName), Some(new NoSuchElementException))
      case _ => BindingPass(headOption.get)
    }
  }

  override def bindJson(currentCursor: ACursor, fieldName: String, bindingMetadata: Map[String, Any]): BindingResult[String] = {

    currentCursor.as[Option[String]] match {
      case Left(parsingFailure) =>
        BindingFailure(fieldName, config.languageConfig.invalidJsonStringMessage(fieldName, currentCursor.focus.map(_.toString()).getOrElse("")), Some(parsingFailure))
      case Right(value) =>
        try {
          value.map(_.trim).filterNot(_.isEmpty) match {
            case None => BindingFailure(fieldName, config.languageConfig.invalidNonEmptyTextMessage(fieldName), Some(new NoSuchElementException))
            case _ => BindingPass(value.get)
          }
        } catch {
          case ex: NoSuchElementException => BindingFailure(fieldName, config.languageConfig.invalidNonEmptyTextMessage(fieldName), Some(ex))
        }
    }
  }

}
