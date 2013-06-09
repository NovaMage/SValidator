package com.github.novamage.svalidator.binding.binders.typed

import testUtils.Observes
import com.github.novamage.svalidator.binding.binders.ITypedBinder
import com.github.novamage.svalidator.binding.{BindingPass, BindingConfig}

class FloatBinderSpecs extends Observes {

  val sut: ITypedBinder[Float] = new FloatBinder(BindingConfig.defaultConfig)

  describe("when testing the binding of a class with a simple constructor with a float argument") {

    val fieldName = "someFloatFieldName"

    describe("and the argument is not present in the values map") {

      val result = sut.bind(fieldName, Map("someOtherFloat" -> List("5.6")))

      it("should have returned a Binding Failure with an error for the float field") {
        result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
      }
    }

    describe("and the argument is present in the values map but it is not a valid float") {

      val result = sut.bind(fieldName, Map(fieldName -> List("aStringThatCanNotBeParsedAsFloat")))

      it("should have returned a Binding Failure with an error for the float field") {
        result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
      }
    }

    describe("and the argument is present in the values map and is a valid float") {

      val result = sut.bind(fieldName, Map(fieldName -> List("90.8")))

      it("should have bound the value  properly") {
        result should equal(BindingPass(90.8F))
      }
    }
  }
}
