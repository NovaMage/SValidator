package com.github.novamage.svalidator.validation.simple

import testUtils.Observes
import com.github.novamage.svalidator.validation.{ValidationFailure, IValidationRule, ValidationPass}

class SimpleValidationRuleSpecs extends Observes {

  case class TestClass(name: String, age: Long, single: Boolean) {

  }

  describe("when using a simple validation rule to validate a specific property on an object with a given field name") {
    val field_name = "someFieldNameHere"
    val instance = TestClass("someName", 18, true)

    describe("and the conditioned validation expression returns false") {

      val conditioned_validation = stubFunction(instance, false)
      val property_extractor_expression = stubUnCallableFunction[TestClass, Long]
      val rule_expression = stubUnCallableFunction[Long, Boolean]
      val error_message_builder = stubUnCallableFunction[String, Long, String]

      val sut: IValidationRule[TestClass] = new SimpleValidationRule(property_extractor_expression, rule_expression, field_name, error_message_builder, conditioned_validation)

      val result = sut.apply(instance)

      it("should have returned ValidationPass as the validation result") {
        result should equal(ValidationPass)
      }

    }

    describe("and the conditioned validation expression returns true") {

      val some_error_message = "someGeneratedErrorMessage"
      val some_property_value = 4935L

      val conditioned_validation = stubFunction(instance, true)
      val property_extractor_expression = stubFunction(instance, some_property_value)

      describe("and the rule expression returns true") {
        val rule_expression = stubFunction(some_property_value, true)
        val error_message_builder = stubUnCallableFunction[String, Long, String]

        val sut: IValidationRule[TestClass] = new SimpleValidationRule(property_extractor_expression, rule_expression, field_name, error_message_builder, conditioned_validation)

        val result = sut.apply(instance)

        it("should have returned a ValidationPass as the validation result") {
          result should equal(ValidationPass)
        }
      }

      describe("and the rule expression returns false") {

        val rule_expression = stubFunction(some_property_value, false)
        val error_message_builder = stubFunction(field_name, some_property_value, some_error_message)

        val sut: IValidationRule[TestClass] = new SimpleValidationRule(property_extractor_expression, rule_expression, field_name, error_message_builder, conditioned_validation)

        val result = sut.apply(instance)

        it("should have returned a validation failure") {
          result.asInstanceOf[ValidationFailure] should not be null
        }

        it("should have set the field name to the passed in field name value") {
          val resultFailure = result.asInstanceOf[ValidationFailure]
          resultFailure.fieldName should equal(field_name)
        }

        it("should have set the error message to the value generated using the field name and value") {
          val resultFailure = result.asInstanceOf[ValidationFailure]
          resultFailure.errorMessage should equal(some_error_message)
        }
      }

    }
  }
}
