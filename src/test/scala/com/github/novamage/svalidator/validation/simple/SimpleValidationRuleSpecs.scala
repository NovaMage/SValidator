package com.github.novamage.svalidator.validation.simple

import testUtils.Observes
import com.github.novamage.svalidator.validation.{ValidationFailure, IValidationRule}

class SimpleValidationRuleSpecs extends Observes {


  describe("when using a simple validation rule to validate a specific property on an object with a given field name") {
    val field_name = "someFieldNameHere"
    val instance = TestClass("someName", 18, true)

    describe("and the conditioned validation expression returns false") {

      val conditioned_validation = stubFunction(instance, false)
      //this value is received by the method as lazy, will not be invoked unless the rule expression applies
      lazy val property_extractor_expression = if (false) List(18L) else throw new Exception
      val rule_expression = stubUnCallableFunction[Long, TestClass, Boolean]
      val error_message_builder = stubUnCallableFunction[TestClass, Long, String]

      val sut: IValidationRule[TestClass] = new SimpleListValidationRule(property_extractor_expression, rule_expression, field_name, error_message_builder, conditioned_validation, false)

      val result = sut.apply(instance)

      it("should have returned an empty list as the validation result") {
        result should equal(Nil)
      }

    }


    describe("and the conditioned validation expression returns true") {

      val some_error_message = "someGeneratedErrorMessage"
      val some_property_value = 4935L

      val conditioned_validation = stubFunction(instance, true)
      //this value is lazy, will not be invoked unless the rule expression applies
      lazy val property_value_expression = List(some_property_value)

      describe("and the rule expression returns true") {
        val rule_expression = stubFunction(some_property_value, instance, true)
        val error_message_builder = stubUnCallableFunction[TestClass, Long, String]

        val sut: IValidationRule[TestClass] = new SimpleListValidationRule(property_value_expression, rule_expression, field_name, error_message_builder, conditioned_validation, false)

        val result = sut.apply(instance)

        it("should have returned an empty list as the validation result") {
          result should equal(Nil)
        }
      }

      describe("and the rule expression returns false") {

        val rule_expression = stubFunction(some_property_value, instance, false)
        val error_message_builder = stubFunction(instance, some_property_value, some_error_message)

        val sut: IValidationRule[TestClass] = new SimpleListValidationRule(property_value_expression, rule_expression, field_name, error_message_builder, conditioned_validation, false)

        val result = sut.apply(instance)

        it("should have returned a non empty list containing a single validation failure") {
          result.asInstanceOf[List[ValidationFailure]] should have size 1
        }

        it("should have set the field name to the passed in field name valueGetter") {
          val resultFailure = result.asInstanceOf[List[ValidationFailure]].head
          resultFailure.fieldName should equal(field_name)
        }

        it("should have set the error message to the valueGetter generated using the field name and valueGetter") {
          val resultFailure = result.asInstanceOf[List[ValidationFailure]].head
          resultFailure.message should equal(some_error_message)
        }
      }

    }
  }

  case class TestClass(name: String, age: Long, single: Boolean) {

  }

}
