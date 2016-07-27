package com.github.novamage.svalidator.binding.binders

import com.github.novamage.svalidator.binding.BindingResult

trait TypedBinder[A] {

  def bind(fieldName: String, valueMap: Map[String, Seq[String]], localizationFunction: String => String): BindingResult[A]
}
