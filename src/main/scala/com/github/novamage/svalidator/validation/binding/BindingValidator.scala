package com.github.novamage.svalidator.validation.binding

import scala.reflect.runtime.{universe => ru}

abstract class BindingValidator[A: ru.TypeTag] extends MappingBindingValidator[A] {

  def bindAndValidate[B](valuesMap: Map[String, Seq[String]])(implicit localizer: BindingLocalizer, tag: ru.TypeTag[B]): BindingAndValidationSummary[A] = {
    super.bindAndValidate(valuesMap, (x: A) => x)
  }


}
