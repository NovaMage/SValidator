package com.github.novamage.svalidator.validation

/** Base trait for validation of objects of a given type
  *
  * @tparam A Type of objects to be validated
  */
trait Validator[-A, +B] {

  /** Returns a [[com.github.novamage.svalidator.validation.ValidationWithData ValidationSummary]] with error information
    * from validating the instance
    *
    * @param instance Instance to validate
    */
  def validate(implicit instance: A): ValidationResult[B]

}

