package com.github.novamage.svalidator.binding.binders.typed

import com.github.novamage.svalidator.binding.binders.TypedBinder
import com.github.novamage.svalidator.binding.{BindingConfig, BindingPass}
import testUtils.Observes

class BooleanBinderSpecs extends Observes {

  val sut: TypedBinder[Boolean] = new BooleanBinder(BindingConfig.defaultConfig)

  describe("when testing the binding of a class with a simple constructor with a boolean argument") {


    val fieldName = "someBooleanFieldName"

    describe("and the argument is not present in the values map") {
      val result = sut.bind(fieldName, Map("someOtherBoolean" -> List("true")), identityLocalizer)

      it("should have returned a Binding Pass with the valueGetter set to false") {
        result should equal(BindingPass(false))
      }
    }

    describe("and the argument is present in the values map with a false valueGetter") {
      val result = sut.bind(fieldName, Map(fieldName -> List("false")), identityLocalizer)

      it("should have returned a Binding Pass with the valueGetter set to false") {
        result should equal(BindingPass(false))
      }
    }

    describe("and the argument is present in the values map with a true valueGetter") {
      val result = sut.bind(fieldName, Map(fieldName -> List("true")), identityLocalizer)

      it("should have returned a Binding Pass with the valueGetter set to false") {
        result should equal(BindingPass(true))
      }
    }

    describe("and the argument is present in the values map with a valueGetter that is not a Boolean") {
      val result = sut.bind(fieldName, Map(fieldName -> List("18")), identityLocalizer)

      it("should have returned a Binding Pass with the valueGetter set to false") {
        result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
      }
    }

  }
}
