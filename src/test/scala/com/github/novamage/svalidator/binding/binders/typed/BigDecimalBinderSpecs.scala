package com.github.novamage.svalidator.binding.binders.typed

import testUtils.Observes
import com.github.novamage.svalidator.binding.binders.TypedBinder
import com.github.novamage.svalidator.binding.{BindingPass, BindingConfig}

class BigDecimalBinderSpecs extends Observes {

  val sut:TypedBinder[BigDecimal] = new BigDecimalBinder(BindingConfig.defaultConfig)

  describe("when testing the binding of a class with a simple constructor with a decimal argument") {

    val fieldName = "someDecimalFieldName"

    describe("and the argument is not present in the values map") {

      val result = sut.bind(fieldName, Map("someOtherDouble" -> List("315.00")))

      it("should have returned a Binding Failure with an error for the decimal field") {
        result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
      }
    }

    describe("and the argument is present in the values map but it is not a valid decimal") {

      val result = sut.bind(fieldName, Map(fieldName -> List("aStringThatCanNotBeParsedAsDecimal")))

      it("should have returned a Binding Failure with an error for the decimal field") {
        result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
      }
    }

    describe("and the argument is present in the values map and is a valid decimal") {

      val result = sut.bind(fieldName, Map(fieldName -> List("170.5000")))

      it("should have bound the valueGetter  properly") {
        result should equal(BindingPass(BigDecimal("170.5000")))
      }
    }
  }
}
