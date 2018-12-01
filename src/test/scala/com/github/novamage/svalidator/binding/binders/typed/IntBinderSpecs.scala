package com.github.novamage.svalidator.binding.binders.typed

import com.github.novamage.svalidator.binding.binders.TypedBinder
import com.github.novamage.svalidator.binding.{BindingConfig, BindingPass}
import testUtils.Observes

class IntBinderSpecs extends Observes {

  val sut: TypedBinder[Int] = new IntBinder(BindingConfig.defaultConfig)

  describe("when testing the binding of a class with a simple constructor with an int argument") {

    val fieldName = "someIntFieldName"
    val metadata = mock[Map[String, Any]]

    describe("and the argument is not present in the values map") {

      val result = sut.bind(fieldName, Map("someOtherInt" -> List("5")), metadata)

      it("should have returned a Binding Failure with an error for the int field") {
        result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
      }
    }

    describe("and the argument is present in the values map but it is not a valid int") {

      val result = sut.bind(fieldName, Map(fieldName -> List("aStringThatCanNotBeParsedAsInt")), metadata)

      it("should have returned a Binding Failure with an error for the int field") {
        result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
      }
    }

    describe("and the argument is present in the values map") {

      val result = sut.bind(fieldName, Map(fieldName -> List("18")), metadata)

      it("should have bound the valueGetter  properly") {
        result should equal(BindingPass(18))
      }
    }
  }
}
