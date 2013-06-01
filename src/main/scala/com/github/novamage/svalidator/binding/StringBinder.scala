package com.github.novamage.svalidator.binding

class StringBinder extends ITypeBinder[String] {
  def bind(fieldName: String, valueMap: Map[String, Seq[String]]): BindingResult[String] = {
    BindingResult(List(), Some(valueMap(fieldName).headOption.map(_.trim).filterNot(_.isEmpty).getOrElse(null)))
  }
}
