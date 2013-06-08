package com.github.novamage.svalidator.binding.binders.typed

import testUtils.Observes
import com.github.novamage.svalidator.binding.binders.ITypeBinder
import com.github.novamage.svalidator.binding.{BindingPass, BindingConfig}

class BooleanBinderSpecs extends Observes {

  val sut: ITypeBinder[Boolean] = new BooleanBinder(BindingConfig.defaultConfig)

  describe("when testing the binding of a class with a simple constructor with a boolean argument") {


    val fieldName = "someBooleanFieldName"

    describe("and the argument is not present in the values map") {
      val result = sut.bind(fieldName, Map("someOtherBoolean" -> List("true")))

      it("should have returned a Binding Pass with the value set to false") {
        result should equal(BindingPass(false))
      }
    }

    describe("and the argument is present in the values map with a false value") {
      val result = sut.bind(fieldName, Map(fieldName -> List("false")))

      it("should have returned a Binding Pass with the value set to false") {
        result should equal(BindingPass(false))
      }
    }

    describe("and the argument is present in the values map with a true value") {
      val result = sut.bind(fieldName, Map(fieldName -> List("true")))

      it("should have returned a Binding Pass with the value set to false") {
        result should equal(BindingPass(true))
      }
    }

    describe("and the argument is present in the values map with a value that is not a Boolean") {
      val result = sut.bind(fieldName, Map(fieldName -> List("18")))

      it("should have returned a Binding Pass with the value set to false") {
        result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
      }
    }

  }
}
