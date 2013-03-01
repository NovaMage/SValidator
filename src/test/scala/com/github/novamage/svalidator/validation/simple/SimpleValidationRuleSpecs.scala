package com.github.novamage.svalidator.validation.simple

import testUtils.Observes
import com.github.novamage.svalidator.validation.{ValidationFailure, IValidationRule, ValidationPass}
import org.mockito.Matchers.anyLong

class SimpleValidationRuleSpecs extends Observes {

  case class TestClass(name: String, age: Long, single: Boolean) {

  }

  describe("when using a simple validation rule to validate a specific property on an object with a given field name") {
    val field_name = "someFieldNameHere"
    val instance = TestClass("someName", 18, true)

    val property_extractor_expression = mock[TestClass => Long]
    val rule_expression = mock[Long => Boolean]
    val error_message_builder = mock[(String, Long) => String]
    val conditioned_validation = mock[TestClass => Boolean]

    describe("and the conditioned validation expression returns false") {

      when(conditioned_validation.apply(instance)) thenReturn false

      val sut: IValidationRule[TestClass] = new SimpleValidationRule(property_extractor_expression, rule_expression, field_name, error_message_builder, conditioned_validation)

      val result = sut.apply(instance)

      it("should have returned ValidationPass as the validation result") {
        result should equal(ValidationPass)
      }

      it("should have never invoked the rule expression") {
        rule_expression wasNeverToldTo (_.apply(anyLong()))
      }
    }

    describe("and the conditioned validation expression returns true") {

      val some_long_property_value = 3948L
      val some_error_message = "someGeneratedErrorMessage"

      when(conditioned_validation.apply(instance)) thenReturn true
      when(property_extractor_expression.apply(instance)) thenReturn some_long_property_value

      describe("and the rule expression returns true") {
        //For some reason the mocking here does not properly work, resorting to a manual mocking function
        //when(rule_expression.apply(some_long_property_value)) thenReturn true

        //The comparison here is only done to assert that the correct parameter was passed in
        val manual_mocking_function:(Long => Boolean) = x => x == some_long_property_value

        val sut: IValidationRule[TestClass] = new SimpleValidationRule(property_extractor_expression, manual_mocking_function, field_name, error_message_builder, conditioned_validation)

        val result = sut.apply(instance)

        it("should have returned a ValidationPass as the validation result") {
          result should equal(ValidationPass)
        }
      }

      describe("and the rule expression returns false") {

        when(rule_expression.apply(some_long_property_value)) thenReturn false
        when(error_message_builder.apply(field_name, some_long_property_value)) thenReturn some_error_message

        val sut: IValidationRule[TestClass] = new SimpleValidationRule(property_extractor_expression, rule_expression, field_name, error_message_builder, conditioned_validation)

        val result = sut.apply(instance)

        it("should have returned a validation failure"){
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
