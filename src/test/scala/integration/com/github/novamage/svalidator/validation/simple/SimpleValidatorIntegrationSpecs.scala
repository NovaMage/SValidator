package integration.com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.validation.simple.SimpleValidator
import com.github.novamage.svalidator.testing._
import testUtils.Observes
import com.github.novamage.svalidator.validation.IValidate

class SimpleValidatorIntegrationSpecs extends Observes {

  describe("when validating an instance of a person") {

    val sut: IValidate[Person] = new PersonValidator

    val instance = Person("John", "Smith", 25, true, true, Some("notes"))

    describe("and all fields are filled properly") {

      lazy val result = sut.validate(instance)

      it("should be valid") {
        result.shouldBeValid()
      }
    }

    describe("and the first name is null") {

      lazy val result = sut.validate(instance.copy(firstName = null))

      it("should have a validation error for the firstName field") {
        result shouldHaveValidationErrorFor "firstName"
      }
    }

    describe("and the first name is empty") {

      lazy val result = sut.validate(instance.copy(firstName = ""))

      it("should have a validation error for the firstName field") {
        result shouldHaveValidationErrorFor "firstName"
      }
    }
    
    
    describe("and the last name is null") {

      lazy val result = sut.validate(instance.copy(lastName = null))

      it("should have a validation error for the lastName field") {
        result shouldHaveValidationErrorFor "lastName"
      }
    }

    describe("and the last name is empty") {

      lazy val result = sut.validate(instance.copy(lastName = ""))

      it("should have a validation error for the lastName field") {
        result shouldHaveValidationErrorFor "lastName"
      }
    }
  }

}