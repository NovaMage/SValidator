package com.github.novamage.svalidator.samples

import com.github.novamage.svalidator.validation.simple.SimpleValidator

object ExampleValidator extends SimpleValidator[ExampleValidatedClass] {

  def buildRules = List(
    For(_.age).ForField("age")
      .must(_ > 0).withMessage("%s must be greater than zero")
      .must(_ < 100).withMessage("%s must be greater than zero"),
    For(_.name).ForField("name")
      .must(_.trim.length > 0))
}
