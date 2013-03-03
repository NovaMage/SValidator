package com.github.novamage.svalidator.validation

import simple.SimpleValidator

case class ValidationSummary(validationFailures: List[ValidationFailure]) {

  def isValid = validationFailures.isEmpty

  class a extends SimpleValidator[ValidationResult] {
    def buildRules = List(For(_.message).ForField("message"))
  }
}
