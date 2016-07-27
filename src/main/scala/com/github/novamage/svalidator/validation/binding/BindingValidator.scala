package com.github.novamage.svalidator.validation.binding

import scala.reflect.runtime.{universe => ru}

abstract class BindingValidator[A: ru.TypeTag] extends MappingBindingValidator[A] {

  def bindAndValidate(valuesMap: Map[String, Seq[String]], localizationFunction: String => String): BindingAndValidationSummary[A] = {
    super.bindAndValidate(valuesMap, (x: A) => x, localizationFunction)
  }

}
