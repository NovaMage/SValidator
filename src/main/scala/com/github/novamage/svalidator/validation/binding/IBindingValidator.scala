package com.github.novamage.svalidator.validation.binding

import com.github.novamage.svalidator.validation.IValidate

trait IBindingValidator[A] extends IValidate[A] {

  def bindAndValidate(valuesMap: Map[String, Seq[String]]): BindingAndValidationSummary[A]
}
