package com.github.novamage.svalidator.binding.binders.typed

import com.github.novamage.svalidator.binding.binders.TypedBinder
import com.github.novamage.svalidator.binding.{BindingConfig, BindingPass}
import testUtils.Observes

class DoubleBinderSpecs extends Observes {

  val sut: TypedBinder[Double] = new DoubleBinder(BindingConfig.defaultConfig)

  describe("when testing the binding of a class with a simple constructor with a double argument") {

    val fieldName = "someDoubleFieldName"
    val metadata = mock[Map[String, Any]]

    describe("and the argument is not present in the values map") {

      val result = sut.bind(fieldName, Map("someOtherDouble" -> List("8.8")), metadata)

      it("should have returned a Binding Failure with an error for the double field") {
        result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
      }
    }

    describe("and the argument is present in the values map but it is not a valid double") {

      val result = sut.bind(fieldName, Map(fieldName -> List("aStringThatCanNotBeParsedAsDouble")), metadata)

      it("should have returned a Binding Failure with an error for the double field") {
        result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
      }
    }

    describe("and the argument is present in the values map and is a valid double") {

      val result = sut.bind(fieldName, Map(fieldName -> List("170.5")), metadata)

      it("should have bound the valueGetter  properly") {
        result should equal(BindingPass(170.5D))
      }
    }
  }
}
