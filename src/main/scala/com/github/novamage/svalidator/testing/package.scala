package com.github.novamage.svalidator

import com.github.novamage.svalidator.validation.ValidationSummary
import com.github.novamage.svalidator.testing.exceptions.ValidationTestingException
package object testing {

  implicit class ShouldExtensions(summary: ValidationSummary) {

    def shouldBeValid() {
      if (!summary.isValid)
        throw new ValidationTestingException(
          "\nExpected instance to be valid, but it had the following errors:\n%s".format(summary.validationFailures.map(_.message).mkString("\n")))
    }

    def shouldHaveValidationErrorFor(fieldName: String) {
      val errors = summary.validationFailures filter { _.fieldName == fieldName }
      if (errors.isEmpty){
        throw new ValidationTestingException(
          s"\nExpected instance to have errors for field $fieldName, but it didn't have any.")
      }
    }
    
    def shouldNotHaveValidationErrorFor(fieldName: String) {
      val errors = summary.validationFailures filter { _.fieldName == fieldName }
      if (!errors.isEmpty){
        throw new ValidationTestingException(
          s"\nExpected instance to not have errors for field $fieldName, but it had the following errors:\n%s".format(summary.validationFailures.map(_.message).mkString("\n")))
      }
    }
  }

}