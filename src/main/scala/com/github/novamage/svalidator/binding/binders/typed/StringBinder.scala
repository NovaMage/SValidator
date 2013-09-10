package com.github.novamage.svalidator.binding.binders.typed

import com.github.novamage.svalidator.binding.{BindingPass, BindingFailure, BindingConfig, BindingResult}
import com.github.novamage.svalidator.binding.binders.ITypedBinder

class StringBinder(config: BindingConfig) extends ITypedBinder[String] {
  def bind(fieldName: String, valueMap: Map[String, Seq[String]]): BindingResult[String] = {

    val headOption = valueMap.get(fieldName) match {
      case None => None
      case Some(list) => list.headOption
    }
    headOption.map(_.trim).filterNot(_.isEmpty) match {
      case None => new BindingFailure(fieldName, config.languageConfig.invalidNonEmptyTextMessage(fieldName), Some(new NoSuchElementException))
      case _ => BindingPass(headOption.get)
    }
  }
}
