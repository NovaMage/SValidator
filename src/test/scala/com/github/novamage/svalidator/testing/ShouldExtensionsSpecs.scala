package com.github.novamage.svalidator.testing

import com.github.novamage.svalidator.testing.exceptions.ValidationTestingException
import com.github.novamage.svalidator.validation.{ValidationFailure, ValidationSummary}
import testUtils.Observes

class ShouldExtensionsSpecs extends Observes {

  val summary = mock[ValidationSummary]

  describe("when using the shouldBeValid view on a validation summary") {

    describe("and the summary has no validation errors in it") {

      when(summary.isValid) thenReturn true

      lazy val result = try {
        val unitResult = summary.shouldBeValid()
        Left(unitResult)
      } catch {
        case e: ValidationTestingException => Right(e)
      }

      it("should not throw an exception when invoking the method upon the summary") {
        result should be('left)
      }
    }

    describe("and the summary has validation errors in it") {

      val failures = List()
      when(summary.isValid) thenReturn false
      when(summary.validationFailures) thenReturn failures

      lazy val result = try {
        val unitResult = summary.shouldBeValid()
        Left(unitResult)
      } catch {
        case e: ValidationTestingException => Right(e)
      }

      it("should throw an exception when invoking the method upon the summary") {
        result should be('right)
      }
    }
  }

  describe("when using the shouldHaveValidationErrorFor view on a validation summary") {

    describe("and there's no error for specified field") {

      val failures = List(ValidationFailure("aField", "aMessage"), ValidationFailure("anotherField", "anotherMessage"))
      when(summary.validationFailures) thenReturn failures

      lazy val result = try {
        val result = summary shouldHaveValidationErrorFor "aDifferentField"
        Left(result)
      } catch {
        case e: ValidationTestingException => Right(e)
      }

      it("should have thrown a validation testing exception") {
        result should be('right)
      }

    }

    describe("and there's an error for the specified field") {

      val failures = List(ValidationFailure("aField", "aMessage"), ValidationFailure("aDifferentField", "anotherMessage"))
      when(summary.validationFailures) thenReturn failures

      lazy val unitResult = try {
        val result = summary shouldHaveValidationErrorFor "aDifferentField"
        Left(result)
      } catch {
        case e: ValidationTestingException => Right(e)
      }

      it("should not have thrown a validation testing exception") {
        unitResult should be('left)
      }

    }
  }

  describe("when using the shouldNotHaveValidationErrorFor view on a validation summary") {

    describe("and there's no error for specified field") {

      val failures = List(ValidationFailure("aField", "aMessage"), ValidationFailure("anotherField", "anotherMessage"))
      when(summary.validationFailures) thenReturn failures

      lazy val unitResult = try {
        val result = summary shouldNotHaveValidationErrorFor "aDifferentField"
        Left(result)
      } catch {
        case e: ValidationTestingException => Right(e)
      }

      it("should not have thrown a validation testing exception") {
        unitResult should be('left)
      }

    }

    describe("and there's an error for the specified field") {

      val failures = List(ValidationFailure("aField", "aMessage"), ValidationFailure("aDifferentField", "anotherMessage"))
      when(summary.validationFailures) thenReturn failures

      lazy val result = try {
        val result = summary shouldNotHaveValidationErrorFor "aDifferentField"
        Left(result)
      } catch {
        case e: ValidationTestingException => Right(e)
      }

      it("should have thrown a validation testing exception") {
        result should be('right)
      }

    }
  }
}