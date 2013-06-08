package com.github.novamage.svalidator.binding.binders.typed

import testUtils.Observes
import com.github.novamage.svalidator.binding.binders.ITypeBinder
import com.github.novamage.svalidator.binding.{BindingPass, BindingConfig}

class IntBinderSpecs extends Observes {

  val sut: ITypeBinder[Int] = new IntBinder(BindingConfig.defaultConfig)

  describe("when testing the binding of a class with a simple constructor with an int argument") {

    val fieldName = "someIntFieldName"

    describe("and the argument is not present in the values map") {

      val result = sut.bind(fieldName, Map("someOtherInt" -> List("5")))

      it("should have returned a Binding Failure with an error for the int field") {
        result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
      }
    }

    describe("and the argument is present in the values map but it is not a valid int") {

      val result = sut.bind(fieldName, Map(fieldName -> List("aStringThatCanNotBeParsedAsInt")))

      it("should have returned a Binding Failure with an error for the int field") {
        result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
      }
    }

    describe("and the argument is present in the values map") {

      val result = sut.bind(fieldName, Map(fieldName -> List("18")))

      it("should have bound the value  properly") {
        result should equal(BindingPass(18))
      }
    }
  }
}
