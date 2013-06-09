package com.github.novamage.svalidator.validation

import testUtils.Observes

class ValidationSummarySpecs extends Observes {

  describe("when checking if a validation summary is valid") {

    describe("and the validation summary contains no validation failures") {

      val sut = new ValidationSummary(List())

      lazy val result = sut.isValid

      it("should return true") {
        result should be(true)
      }
    }

    describe("and the validation summary contains one or more validation failures") {

      val sut =  new ValidationSummary(List(new ValidationFailure("someFieldName", "someErrorMessage")))

      lazy val result = sut.isValid

      it("should return false") {
        result should be(false)
      }
    }

  }

}
