package com.github.novamage.svalidator.validation.simple

import testUtils.Observes

class FieldRequiringSimpleValidationRuleBuilderSpecs extends Observes {

  case class SampleValidatedClass(a: String, b: Long) {

  }

  //Don't know how to unit test this part without allowing visibility of fields in the SimpleValidationBuilder
  describe("when using the ForField helper of a field requiring simple validation rule builder") {

    val property_expression = stubUnCallableFunction[SampleValidatedClass, Long]
    val field_name = "someFieldName"

    val sut = new FieldRequiringSimpleValidationRuleBuilder(property_expression, Nil, Nil)

    lazy val result = sut.ForField(field_name)

    it("should return a simple validation rule builder with the property expression used constructing the object and " +
      "the field name passed in to the ForField method")(pending)
  }
}
