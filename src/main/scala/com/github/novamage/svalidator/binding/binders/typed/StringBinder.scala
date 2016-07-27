package com.github.novamage.svalidator.binding.binders.typed

import com.github.novamage.svalidator.binding.binders.TypedBinder
import com.github.novamage.svalidator.binding.{BindingConfig, BindingFailure, BindingPass, BindingResult}

class StringBinder(config: BindingConfig) extends TypedBinder[String] {
  def bind(fieldName: String, valueMap: Map[String, Seq[String]], localizationFunction: String => String): BindingResult[String] = {

    val headOption = valueMap.get(fieldName) match {
      case None => None
      case Some(list) => list.headOption
    }
    headOption.map(_.trim).filterNot(_.isEmpty) match {
      case None => new BindingFailure(fieldName, config.languageConfig.invalidNonEmptyTextMessage(fieldName, localizationFunction), Some(new NoSuchElementException))
      case _ => BindingPass(headOption.get)
    }
  }
}
