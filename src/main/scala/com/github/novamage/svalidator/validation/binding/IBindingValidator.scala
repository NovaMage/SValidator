package com.github.novamage.svalidator.validation.binding

trait IBindingValidator[A] {

  def bindAndValidate(valuesMap: Map[String, Seq[String]]): BindingAndValidationSummary[A]
}
