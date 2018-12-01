package com.github.novamage.svalidator.binding.binders.typed

import com.github.novamage.svalidator.binding.binders.TypedBinder
import com.github.novamage.svalidator.binding.{BindingConfig, BindingPass}
import testUtils.Observes

class StringBinderSpecs extends Observes {

  val sut: TypedBinder[String] = new StringBinder(BindingConfig.defaultConfig)

  describe("when testing the binding of a class with a simple constructor with a string argument") {

    val fieldName = "someStringFieldName"
    val metadata = mock[Map[String, Any]]

    describe("and the argument is not present in the values map") {
      val result = sut.bind(fieldName, Map("aDifferentField" -> List("someValue")), metadata)

      it("should have returned a Binding Failure with a field error for the string field") {
        result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
      }
    }

    describe("and the argument is present in the values map") {

      describe("and the argument is an empty string") {
        val result = sut.bind(fieldName, Map(fieldName -> List("")), metadata)

        it("should have returned a Binding Failure with a field error for the string field") {
          result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
        }
      }

      describe("and the argument is a whitespace string") {
        val result = sut.bind(fieldName, Map(fieldName -> List("             ")), metadata)

        it("should have returned a Binding Failure with a field error for the string field") {
          result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
        }
      }

      describe("and the valueGetter is a non-whitespace string with spaces on the edges") {
        val result = sut.bind(fieldName, Map(fieldName -> List(" someValue ")), metadata)

        it("should have bound the property including its spaces properly") {
          result should equal(BindingPass(" someValue "))
        }
      }
    }
  }
}
