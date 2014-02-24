package com.github.novamage.svalidator.binding.binders.typed

import testUtils.Observes
import com.github.novamage.svalidator.binding.binders.TypedBinder
import com.github.novamage.svalidator.binding.{BindingPass, BindingConfig}

class LongBinderSpecs extends Observes {

  val sut: TypedBinder[Long] = new LongBinder(BindingConfig.defaultConfig)

  describe("when testing the binding of a class with a simple constructor with a long argument") {

    val fieldName = "someLong"

    describe("and the argument is not present in the values map") {
      val result = sut.bind(fieldName, Map("someOtherLong" -> List("9")))

      it("should have returned a Binding Failure with an error for the long field") {
        result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
      }
    }

    describe("and the argument is present in the values map but it is not a valid Long") {
      val result = sut.bind(fieldName, Map(fieldName -> List("aStringThatCanNotBeParsedAsLong")))

      it("should have returned a Binding Failure with an error for the int field") {
        result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
      }
    }

    describe("and the argument is present in the values map") {
      val result = sut.bind(fieldName, Map(fieldName -> List("49")))

      it("should have bound the valueGetter properly") {
        result should equal(BindingPass(49L))
      }
    }
  }
}
