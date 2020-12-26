package com.github.novamage.svalidator.binding.binders.typed

import com.github.novamage.svalidator.binding.binders.{JsonTypedBinder, TypedBinder}
import com.github.novamage.svalidator.binding.{BindingConfig, BindingFailure, BindingPass, BindingResult}
import io.circe.ACursor

/** Performs binding of a boolean field
  *
  * @param config The configuration to use for error messages
  */
class BooleanBinder(config: BindingConfig)
  extends TypedBinder[Boolean] with JsonTypedBinder[Boolean] {

  def bind(fieldName: String, valueMap: Map[String, Seq[String]], bindingMetadata: Map[String, Any]): BindingResult[Boolean] = {
    try {
      BindingPass(valueMap.get(fieldName).exists(_.headOption.exists(_.toBoolean)))
    } catch {
      case ex: IllegalArgumentException => BindingFailure(fieldName, config.languageConfig.invalidBooleanMessage(fieldName, valueMap.get(fieldName).flatMap(_.headOption).getOrElse("")), Some(ex))
    }
  }

  override def bindJson(currentCursor: ACursor, fieldName: Option[String], bindingMetadata: Map[String, Any]): BindingResult[Boolean] = {
    currentCursor.as[Option[Boolean]] match {
      case Left(parsingFailure) =>
        BindingFailure(fieldName, config.languageConfig.invalidBooleanMessage(fieldName.getOrElse(""), currentCursor.focus.map(_.toString()).getOrElse("")), Some(parsingFailure))
      case Right(value) =>
        try {
          BindingPass(value.getOrElse(false))
        } catch {
          case ex: IllegalArgumentException => BindingFailure(fieldName, config.languageConfig.invalidBooleanMessage(fieldName.getOrElse(""), value.map(_.toString).getOrElse("")), Some(ex))
        }
    }
  }

}
