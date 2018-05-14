package com.github.novamage.svalidator.binding.binders

import com.github.novamage.svalidator.binding.BindingResult
import com.github.novamage.svalidator.validation.Localizer

trait TypedBinder[A] {

  def bind(fieldName: String, valueMap: Map[String, Seq[String]], localizer:Localizer): BindingResult[A]
}
