package com.github.novamage.svalidator.validation.simple

import testUtils.Observes

class SimpleValidationRuleBuilderSpecs extends Observes {

  case class SampleValidatedClass(a: String, b: Long) {
  }

  describe("when building rules using the Simple Validation Rule Builder") {

    describe("and the build rules is called with a current rule structure that is null") {

      val property_expression = stubUnCallableFunction[SampleValidatedClass, Long]

      val sut = new SimpleValidationRuleBuilder[SampleValidatedClass, Long](property_expression, null, List(), "fieldName")

      lazy val result = sut.buildRules

      it("should return an empty list") {
        result should be('empty)
      }

    }

    describe("and build rules is called with a current rule structure that is not null") {
      val property_expression = stubUnCallableFunction[SampleValidatedClass, Long]
      val rule_expression = stubUnCallableFunction[Long, Boolean]
      val rule_structure_container = SimpleValidationRuleStructureContainer[SampleValidatedClass, Long](rule_expression, None, None)

      val sut = new SimpleValidationRuleBuilder[SampleValidatedClass, Long](property_expression, rule_structure_container, List(), "fieldName")

      val result = sut.buildRules

      it("should return a list with as many rules as rule expressions passed in") {
        result should have size (1)
      }
    }

    /*
     * Other behavior of this class is tested on integration tests.  
     * Testing more behavior as a unit test would imply making some members public which 
     * is not desired at this time.
     */
  }

}
