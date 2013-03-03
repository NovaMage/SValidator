package com.github.novamage.svalidator.validation.simple

import testUtils.Observes

class SimpleValidationRuleBuilderSpecs extends Observes {

  case class SampleValidatedClass(a: String, b: Long) {
  }

  describe("when building rules using the Simple Validation Rule Builder") {

    describe("and the build rules is called without adding any rules") {

      val property_expression = stubUnCallableFunction[SampleValidatedClass, Long]

      val sut = new SimpleValidationRuleBuilder[SampleValidatedClass, Long](property_expression, Nil, "", Nil, x => true)

      lazy val result = sut.buildRules

      it("should return an empty list") {
        result should be('empty)
      }

    }

  }

}
