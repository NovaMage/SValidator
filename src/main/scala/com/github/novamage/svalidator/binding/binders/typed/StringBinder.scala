package com.github.novamage.svalidator.binding.binders.typed

import com.github.novamage.svalidator.binding.binders.TypedBinder
import com.github.novamage.svalidator.binding.{BindingConfig, BindingFailure, BindingPass, BindingResult}

/** Performs binding of a string field.  Only strings that are non-whitespace and non-empty will be successfully bound.
  * However, strings are not trimmed if they aren't full whitespace.
  *
  * @param config The configuration to use for error messages
  */
class StringBinder(config: BindingConfig) extends TypedBinder[String] {
  def bind(fieldName: String, valueMap: Map[String, Seq[String]]): BindingResult[String] = {

    val headOption = valueMap.get(fieldName) match {
      case None => None
      case Some(list) => list.headOption
    }
    headOption.map(_.trim).filterNot(_.isEmpty) match {
      case None => BindingFailure(fieldName, config.languageConfig.invalidNonEmptyTextMessage(fieldName), Some(new NoSuchElementException))
      case _ => BindingPass(headOption.get)
    }
  }
}
