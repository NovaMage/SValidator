package com.github.novamage.svalidator.validation

/** Base trait for validation of objects of a given type
  *
  * @tparam A Type of objects to be validated
  */
trait Validator[-A] {

  /** Returns a [[com.github.novamage.svalidator.validation.ValidationSummary ValidationSummary]] with error information
    * from validating the instance
    *
    * @param instance Object to validate
    */
  def validate(implicit instance: A): ValidationSummary

}

