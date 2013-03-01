package com.github.novamage.svalidator.samples

import com.github.novamage.svalidator.validation.simple.SimpleValidator

object ExampleValidator extends SimpleValidator[ExampleValidatedClass] {

  def buildRules = List(
    For(_.age).ForField("age")
      .Must(_ > 0).WithMessage("%s must be greater than zero")
      .Must(_ < 100).WithMessage("%s must be greater than zero")
    ,
    For(_.name).ForField("name")
      .Must(_.trim.length > 0)
  )
}
