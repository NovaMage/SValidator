package com.github.novamage.svalidator.validation

sealed abstract class ValidationResult {

  def isValid: Boolean
  def message : String

}

case class ValidationFailure(fieldName: String, errorMessage: String) extends ValidationResult {
  def isValid = false
  def message = errorMessage
}

object ValidationPass extends ValidationResult {
  def isValid = true
  def message = ""
}
