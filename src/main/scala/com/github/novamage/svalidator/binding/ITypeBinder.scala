package com.github.novamage.svalidator.binding

trait ITypeBinder[A] {

  def bind(fieldName: String, valueMap: Map[String, Seq[String]]): BindingResult[A]
}
