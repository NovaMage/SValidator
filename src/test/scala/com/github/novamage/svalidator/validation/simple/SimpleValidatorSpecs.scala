package com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation.simple.internals.{ChainedValidationStream, IValidationRule, RuleBuilder, RuleStreamCollection}
import com.github.novamage.svalidator.validation.{ValidationFailure, _}
import org.mockito.Matchers.any
import testUtils.Observes

class SimpleValidatorSpecs extends Observes {

  private val rule_builder_1 = mock[RuleBuilder[SampleValidatedClass]]
  private val rule_builder_2 = mock[RuleBuilder[SampleValidatedClass]]
  private val rule_builder_3 = mock[RuleBuilder[SampleValidatedClass]]
  private val rule_builder_4 = mock[RuleBuilder[SampleValidatedClass]]

  case class SampleValidatedClass(a: String, b: Long) {
  }

  class SampleSimpleValidator extends SimpleValidator[SampleValidatedClass] {
    def validate(implicit instance: SampleValidatedClass):ValidationSummary = WithRules(rule_builder_1, rule_builder_2, rule_builder_3, rule_builder_4)
  }

  describe("when performing validation assisted by an instance of a child class of simple validator") {

    val sut: Validator[SampleValidatedClass, Nothing] = new SampleSimpleValidator

    val instance = mock[SampleValidatedClass]

    val rule_1 = mock[IValidationRule[SampleValidatedClass]]
    val rule_2 = mock[IValidationRule[SampleValidatedClass]]
    val rule_3 = mock[IValidationRule[SampleValidatedClass]]
    val rule_list_1 = RuleStreamCollection(List(ChainedValidationStream(List(Stream(rule_1, rule_2, rule_3)), None)))

    val rule_4 = mock[IValidationRule[SampleValidatedClass]]
    val rule_5 = mock[IValidationRule[SampleValidatedClass]]
    val rule_6 = mock[IValidationRule[SampleValidatedClass]]
    val rule_list_2 = RuleStreamCollection(List(ChainedValidationStream(List(Stream(rule_4, rule_5, rule_6)), None)))

    val rule_7 = mock[IValidationRule[SampleValidatedClass]]
    val rule_8 = mock[IValidationRule[SampleValidatedClass]]
    val rule_9 = mock[IValidationRule[SampleValidatedClass]]
    val rule_list_3 = RuleStreamCollection(List(ChainedValidationStream(List(Stream(rule_7, rule_8, rule_9)), None)))

    val rule_10 = mock[IValidationRule[SampleValidatedClass]]
    val rule_11 = mock[IValidationRule[SampleValidatedClass]]
    val rule_12 = mock[IValidationRule[SampleValidatedClass]]
    val rule_list_4 = RuleStreamCollection(List(ChainedValidationStream(List(Stream(rule_10, rule_11, rule_12)), None)))

    when(rule_builder_1.buildRules(instance)) thenReturn rule_list_1
    when(rule_1.apply(instance)) thenReturn Nil
    when(rule_2.apply(instance)) thenReturn Nil
    when(rule_3.apply(instance)) thenReturn Nil

    when(rule_builder_2.buildRules(instance)) thenReturn rule_list_2
    when(rule_builder_3.buildRules(instance)) thenReturn rule_list_3
    when(rule_builder_4.buildRules(instance)) thenReturn rule_list_4

    describe("and some of the rule sets return validation failures") {

      val failure_1 = ValidationFailure("fieldNameInSet2", MessageParts("errorMessageInRule4"), Map.empty)
      val failure_2 = ValidationFailure("fieldNameInSet3", MessageParts("errorMessageInRule8"), Map.empty)
      val failure_3 = ValidationFailure("fieldNameInSet4", MessageParts("errorMessageInRule12"), Map.empty)
      val failure_4 = ValidationFailure("fieldNameInSet5", MessageParts("errorMessageInRule8 second time"), Map.empty)

      when(rule_4.apply(instance)) thenReturn List(failure_1)

      when(rule_7.apply(instance)) thenReturn Nil
      when(rule_8.apply(instance)) thenReturn List(failure_2, failure_4)

      when(rule_10.apply(instance)) thenReturn Nil
      when(rule_11.apply(instance)) thenReturn Nil
      when(rule_12.apply(instance)) thenReturn List(failure_3)

      lazy val result = sut.validate(instance)

      it("return the first validation failure in each rule list") {
        result.validationFailures should equal(List(failure_1, failure_2, failure_4, failure_3))
      }

      it("should have applied any rules in the lists after the first validation failure") {
        rule_5 wasNeverToldTo {
          _.apply(any[SampleValidatedClass])
        }
        rule_6 wasNeverToldTo {
          _.apply(any[SampleValidatedClass])
        }
        rule_9 wasNeverToldTo {
          _.apply(any[SampleValidatedClass])
        }
      }
    }

    describe("and some of the rule sets return validation failures") {

      when(rule_4.apply(instance)) thenReturn Nil
      when(rule_5.apply(instance)) thenReturn Nil
      when(rule_6.apply(instance)) thenReturn Nil

      when(rule_7.apply(instance)) thenReturn Nil
      when(rule_8.apply(instance)) thenReturn Nil
      when(rule_9.apply(instance)) thenReturn Nil

      when(rule_10.apply(instance)) thenReturn Nil
      when(rule_11.apply(instance)) thenReturn Nil
      when(rule_12.apply(instance)) thenReturn Nil

      lazy val result = sut.validate(instance)

      it("return no validation failures") {
        result.validationFailures should be('empty)
      }

    }
  }

  //  describe("when using the For helper method to generate a rule builder") {
  //
  //    val sut = new SampleSimpleValidator
  //
  //    val property_expression = stubUnCallableFunction[SampleValidatedClass, Long]
  //
  //    lazy val result = sut.For(property_expression)
  //
  //    //Don't know how to test this yet, i don't want to make the properties public
  //    it("should return a field requiring rule builder with the passed in property expression, using empty lists " +
  //      "for validation expressions and error messages")(pending)
  //  }
}
