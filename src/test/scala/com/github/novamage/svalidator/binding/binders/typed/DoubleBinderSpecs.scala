package com.github.novamage.svalidator.binding.binders.typed

import testUtils.Observes
import com.github.novamage.svalidator.binding.binders.ITypedBinder
import com.github.novamage.svalidator.binding.{BindingPass, BindingConfig}

class DoubleBinderSpecs extends Observes {

  val sut:ITypedBinder[Double] = new DoubleBinder(BindingConfig.defaultConfig)

  describe("when testing the binding of a class with a simple constructor with a double argument") {

    val fieldName = "someDoubleFieldName"

    describe("and the argument is not present in the values map") {

      val result = sut.bind(fieldName, Map("someOtherDouble" -> List("8.8")))

      it("should have returned a Binding Failure with an error for the double field") {
        result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
      }
    }

    describe("and the argument is present in the values map but it is not a valid double") {

      val result = sut.bind(fieldName, Map(fieldName -> List("aStringThatCanNotBeParsedAsDouble")))

      it("should have returned a Binding Failure with an error for the double field") {
        result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
      }
    }

    describe("and the argument is present in the values map and is a valid double") {

      val result = sut.bind(fieldName, Map(fieldName -> List("170.5")))

      it("should have bound the value  properly") {
        result should equal(BindingPass(170.5D))
      }
    }
  }
}
