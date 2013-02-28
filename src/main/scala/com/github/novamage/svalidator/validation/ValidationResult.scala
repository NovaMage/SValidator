package com.github.novamage.svalidator.validation

sealed abstract class ValidationResult {

  def isValid: Boolean
  def message : String

}

final case class Invalid(fieldName: String, errorMessage: String) extends ValidationResult {
  def isValid = false
  def message = errorMessage
}

object Valid extends ValidationResult {
  def isValid = true
  def message = ""
}

