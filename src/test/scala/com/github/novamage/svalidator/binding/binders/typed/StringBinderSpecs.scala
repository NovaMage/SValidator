package com.github.novamage.svalidator.binding.binders.typed

import testUtils.Observes
import com.github.novamage.svalidator.binding.binders.ITypeBinder
import com.github.novamage.svalidator.binding.{BindingPass, BindingConfig}

class StringBinderSpecs extends Observes {

  val sut: ITypeBinder[String] = new StringBinder(BindingConfig.defaultConfig)

  describe("when testing the binding of a class with a simple constructor with a string argument") {

    val fieldName = "someStringFieldName"

    describe("and the argument is not present in the values map") {
      val result = sut.bind(fieldName, Map("aDifferentField" -> List("someValue")))

      it("should have returned a Binding Failure with a field error for the string field") {
        result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
      }
    }

    describe("and the argument is present in the values map") {

      describe("and the argument is an empty string") {
        val result = sut.bind(fieldName, Map(fieldName -> List("")))

        it("should have returned a Binding Failure with a field error for the string field") {
          result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
        }
      }

      describe("and the argument is a whitespace string") {
        val result = sut.bind(fieldName, Map(fieldName -> List("             ")))

        it("should have returned a Binding Failure with a field error for the string field") {
          result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
        }
      }

      describe("and the value is a non-whitespace string with spaces on the edges") {
        val result = sut.bind(fieldName, Map(fieldName -> List(" someValue ")))

        it("should have bound the trimmed value  properly") {
          result should equal(BindingPass("someValue"))
        }
      }
    }
  }
}
