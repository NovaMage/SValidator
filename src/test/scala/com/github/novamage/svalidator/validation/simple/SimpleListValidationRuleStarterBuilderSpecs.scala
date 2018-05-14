package com.github.novamage.svalidator.validation.simple

import testUtils.Observes

class SimpleListValidationRuleStarterBuilderSpecs extends Observes {

  case class SampleValidatedClass(a: String, b: Long) {
  }

  describe("when building rules using the Simple Validation Rule Builder") {

    val instance = SampleValidatedClass("firstValue", 8L)

    describe("and build rules is called with a current rule structure that is not null") {
      val property_expression = stubUnCallableFunction[SampleValidatedClass, List[Long]]
      val rule_expression = stubUnCallableFunction[Long, SampleValidatedClass, Boolean]
      val rule_structure_container = SimpleValidationRuleStructureContainer[SampleValidatedClass, Long](rule_expression, None, None, None, Map.empty)

      val sut = new SimpleListValidationRuleContinuationBuilder[SampleValidatedClass, Long, Nothing](property_expression, Some(rule_structure_container), List(), "fieldName", false, None, None, None)

      val result = sut.buildRules(instance)

      it("should return a list with as many rules as rule expressions passed in") {
        result.chains should have size 1
        result.chains.head.mainStream should have size 1
      }
    }

    /*
     * Other behavior of this class is tested on integration tests.  
     * Testing more behavior as a unit test would imply making some members public which 
     * is not desired at this time.
     */
  }

}
