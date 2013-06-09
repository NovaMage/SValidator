package integration.com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.testing._
import testUtils.Observes
import com.github.novamage.svalidator.validation.IValidate
import com.github.novamage.svalidator.validation.simple.constructs._
import com.github.novamage.svalidator.validation.simple.SimpleValidator
import com.github.novamage.svalidator.validation.IRuleBuilder

case class Person(firstName: String,
                  lastName: String,
                  age: Int,
                  married: Boolean,
                  hasJob: Boolean,
                  notes: Option[String])

class PersonValidator extends SimpleValidator[Person] {

  override def buildRules: List[IRuleBuilder[Person]] = List(

    For {_.firstName} ForField 'firstName
      mustNot be empty() withMessage "First name is required"
      must have maxLength 32 withMessage "Must have 32 characters or less",

    For {_.lastName} ForField 'lastName
      mustNot be empty() withMessage "Last name is required"
      must have maxLength 32 withMessage "Must have 32 characters or less",

    For {_.age} ForField 'age
      mustNot be negative() withMessage "Must be a positive number",

    For {_.married} ForField 'married
      must be(false) when {_.age < 18} withMessage "Must be 18 years or older to allow marking marriage",

    For {_.hasJob} ForField 'hasJob
      must be(false) when {_.age < 21} withMessage "Must be 21 years or older to allow marking a job"

  )
}

class SimpleValidatorIntegrationSpecs extends Observes {

  describe("when validating an instance of a person") {

    val sut: IValidate[Person] = new PersonValidator

    val instance = Person(
      firstName = "John",
      lastName = "Smith",
      age = 25,
      married = true,
      hasJob = true,
      notes = Some("notes"))

    describe("and all fields are filled properly") {

      lazy val result = sut.validate(instance)

      it("should be valid") {
        result.shouldBeValid()
      }
    }

    describe("and the first name is null") {

      lazy val result = sut.validate(instance.copy(firstName = null))

      it("should have a validation error for the firstName field") {
        result shouldHaveValidationErrorFor 'firstName
      }
    }

    describe("and the first name is empty") {

      lazy val result = sut.validate(instance.copy(firstName = ""))

      it("should have a validation error for the firstName field") {
        result shouldHaveValidationErrorFor 'firstName
      }
    }

    describe("and the first name has more than 32 characters") {

      lazy val result = sut.validate(instance.copy(firstName = "012345678901234567890123456789012"))

      it("should have a validation error for the firstName field") {
        result shouldHaveValidationErrorFor 'firstName
      }
    }

    describe("and the last name is null") {

      lazy val result = sut.validate(instance.copy(lastName = null))

      it("should have a validation error for the lastName field") {
        result shouldHaveValidationErrorFor 'lastName
      }
    }

    describe("and the last name is empty") {

      lazy val result = sut.validate(instance.copy(lastName = ""))

      it("should have a validation error for the lastName field") {
        result shouldHaveValidationErrorFor 'lastName
      }
    }

    describe("and the last name has more than 32 characters") {

      lazy val result = sut.validate(instance.copy(lastName = "012345678901234567890123456789012"))

      it("should have a validation error for the lastName field") {
        result shouldHaveValidationErrorFor 'lastName
      }
    }

    describe("and the age is negative") {

      lazy val result = sut.validate(instance.copy(age = -1))

      it("should have a validation error for the age field") {
        result shouldHaveValidationErrorFor 'age
      }
    }

    describe("and the notes are not present") {

      lazy val result = sut.validate(instance.copy(notes = None))

      it("should not have a validation error for the notes field") {
        result shouldNotHaveValidationErrorFor 'notes
      }
    }

    describe("and the married flag is set to true but the age is lower than the marriageable age of 18") {

      lazy val result = sut.validate(instance.copy(age = 17, married = true))

      it("should have a validation error for the married field") {
        result shouldHaveValidationErrorFor 'married
      }

    }

    describe("and the hasJob flag is set to true but the age is lower than the minimum working age of 21") {

      lazy val result = sut.validate(instance.copy(age = 20, hasJob = true))

      it("should have a validation error for the married field") {
        result shouldHaveValidationErrorFor 'hasJob
      }

    }

  }

}