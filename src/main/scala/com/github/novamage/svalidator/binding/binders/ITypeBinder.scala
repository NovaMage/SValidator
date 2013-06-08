package com.github.novamage.svalidator.binding.binders

import com.github.novamage.svalidator.binding.BindingResult

trait ITypeBinder[A] {

  def bind(fieldName: String, valueMap: Map[String, Seq[String]]): BindingResult[A]
}
