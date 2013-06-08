package com.github.novamage.svalidator.binding.binders.typed

import com.github.novamage.svalidator.binding.{BindingPass, BindingResult}
import com.github.novamage.svalidator.binding.binders.ITypeBinder

class StringBinder extends ITypeBinder[String] {
  def bind(fieldName: String, valueMap: Map[String, Seq[String]]): BindingResult[String] = {
    BindingPass(valueMap.get(fieldName).map(x => x.headOption.map(_.trim).filterNot(_.isEmpty).getOrElse(null)).getOrElse(null))
  }
}
