package com.github.novamage.svalidator.testing

import com.github.novamage.svalidator.testing.exceptions.ValidationTestingException
import com.github.novamage.svalidator.validation.{ValidationFailure, ValidationSummary}

class ShouldExtensions(summary: ValidationSummary) {

  def shouldBeValid(): Unit = {
    if (!summary.isValid) {
      throw new ValidationTestingException(
        "\nExpected instance to be valid, but it had the following errors:\n%s".format(summary.validationFailures.map(_.message).mkString("\n")))
    }
  }

  def shouldHaveValidationErrorFor(fieldName: Symbol): ShouldMessageKeyContinuationExtensions = {
    shouldHaveValidationErrorFor(fieldName.name)
  }

  def shouldHaveValidationErrorFor(fieldName: String): ShouldMessageKeyContinuationExtensions = {
    val errors = summary.validationFailures filter { _.fieldName == fieldName }
    if (errors.isEmpty) {
      throw new ValidationTestingException(
        s"\nExpected instance to have errors for field $fieldName, but it didn't have any.")
    }
    new ShouldMessageKeyContinuationExtensions(fieldName, errors)
  }

  def shouldNotHaveValidationErrorFor(fieldName: Symbol): ShouldMessageKeyContinuationExtensions = {
    shouldNotHaveValidationErrorFor(fieldName.name)
  }

  def shouldNotHaveValidationErrorFor(fieldName: String): ShouldMessageKeyContinuationExtensions = {
    val errors = summary.validationFailures filter { _.fieldName == fieldName }
    if (errors.nonEmpty) {
      throw new ValidationTestingException(
        s"\nExpected instance to not have errors for field $fieldName, but it had the following errors:\n%s".format(summary.validationFailures.map(_.message).mkString("\n")))
    }
    new ShouldMessageKeyContinuationExtensions(fieldName, errors)
  }
}

class ShouldMessageKeyContinuationExtensions(fieldName: String, fieldFailures: List[ValidationFailure]) {

  def withMessageKey(messageKey: String): ShouldMessageArgumentsContinuationExtensions = {
    val errorsOfKey = fieldFailures.filter { _.messageParts.messageKey == messageKey }
    if (errorsOfKey.isEmpty) {
      throw new ValidationTestingException(
        s"\nExpected instance to have errors for field $fieldName with message key $messageKey, but instead " +
          s"the following message keys were found: ${ fieldFailures.map(_.messageParts.messageKey) }")
    }
    new ShouldMessageArgumentsContinuationExtensions(fieldName, messageKey, errorsOfKey)
  }

}

class ShouldMessageArgumentsContinuationExtensions(fieldName: String,
                                                   messageKey: String,
                                                   errorsOfKey: List[ValidationFailure]) {

  def withFormatValues(formatValues: Any*): Unit = {
    val errors = errorsOfKey.filter { _.messageParts.messageFormatValues == formatValues.toList }
    if (errors.isEmpty) {
      throw new ValidationTestingException(
        s"\nExpected instance to have errors for field $fieldName with message key $messageKey and format values " +
          s"$formatValues, but instead " +
          s"the following format values were found: ${ errorsOfKey.map(_.messageParts.messageFormatValues).mkString(",") }")
    }
  }

}
