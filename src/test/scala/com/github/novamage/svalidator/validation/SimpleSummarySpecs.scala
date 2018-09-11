package com.github.novamage.svalidator.validation

import testUtils.Observes

class SimpleSummarySpecs extends Observes {

  describe("when checking if a validation summary is valid") {

    describe("and the validation summary contains no validation failures") {

      val sut = ValidationWithData(List(), None)

      lazy val result = sut.isValid

      it("should return true") {
        result should be(true)
      }
    }

    describe("and the validation summary contains one or more validation failures") {

      val sut = ValidationWithData(List(ValidationFailure("someFieldName", MessageParts("someErrorMessage"), Map.empty)), None)

      lazy val result = sut.isValid

      it("should return false") {
        result should be(false)
      }
    }

  }

}
