package integration.com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.testing._
import testUtils.Observes
import com.github.novamage.svalidator.validation.simple.constructs._
import com.github.novamage.svalidator.validation.simple.SimpleValidator
import com.github.novamage.svalidator.validation.IRuleBuilder

case class Address(line1: String, line2: String, city: String, state: String, zip: String)

case class PhoneNumber(areaCode: String, number: String)

case class Person(firstName: String,
                  lastName: String,
                  age: Int,
                  married: Boolean,
                  hasJob: Boolean,
                  notes: Option[String],
                  primaryAddress: Address,
                  otherAddresses: List[Address],
                  tasksCompletedByMonth: List[Int],
                  emergencyPhoneNumber: Option[PhoneNumber])


class AddressValidator extends SimpleValidator[Address] {

  def buildRules = List(
    For {_.zip} ForField 'zip
      must have maxLength 10 withMessage "Must have 10 characters or less"
  )
}

class PhoneNumberValidator extends SimpleValidator[PhoneNumber] {
  def buildRules = List(
    For {_.areaCode} ForField 'areaCode
      must have maxLength 4 withMessage "The area code can not exceed 4 characters"
  )
}

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
      must be(false) when {_.age < 21} withMessage "Must be 21 years or older to allow marking a job",

    For {_.tasksCompletedByMonth} ForField 'tasksCompletedByMonth
      must {_.size == 12} withMessage "Must have 12 values for the tasks completed by month",

    ForOptional {_.notes} ForField 'notes
      must have maxLength 32 withMessage "Notes can't have more than 32 characters",

    ForEach {_.tasksCompletedByMonth} ForField 'tasksCompletedByMonth
      must be positive() withMessage "Must be a positive number",

    ForComponent {_.primaryAddress} ForField 'primaryAddress
      validateUsing new AddressValidator,

    ForOptionalComponent {_.emergencyPhoneNumber} ForField 'emergencyPhoneNumber
      validateUsing new PhoneNumberValidator,

    ForEachComponent {_.otherAddresses} ForField 'otherAddresses
      validateUsing new AddressValidator

  )
}

class SimpleValidatorIntegrationSpecs extends Observes {

  val sut = new PersonValidator

  describe("when validating an instance of a person") {


    val instance = Person(
      firstName = "John",
      lastName = "Smith",
      age = 25,
      married = true,
      hasJob = true,
      notes = Some("notes"),
      primaryAddress = Address("line1", "line2", "city", "state", "someZip"),
      otherAddresses = List(Address("line1", "line2", "city", "state", "someZip"), Address("anotherLine1", "anotherLine2", "anotherCity", "anotherState", "anotherZip")),
      tasksCompletedByMonth = List(7, 4, 9, 3, 10, 6, 15, 59, 4, 2, 1, 2),
      emergencyPhoneNumber = Some(PhoneNumber(areaCode = "123", number = "4567890")))

    describe("and all fields are filled properly") {

      val result = sut.validate(instance)

      it("should be valid") {
        result.shouldBeValid()
      }
    }

    describe("and the first name is null") {

      val result = sut.validate(instance.copy(firstName = null))

      it("should have a validation error for the firstName field") {
        result shouldHaveValidationErrorFor 'firstName
      }
    }

    describe("and the first name is empty") {

      val result = sut.validate(instance.copy(firstName = ""))

      it("should have a validation error for the firstName field") {
        result shouldHaveValidationErrorFor 'firstName
      }
    }

    describe("and the first name has more than 32 characters") {

      val result = sut.validate(instance.copy(firstName = "012345678901234567890123456789012"))

      it("should have a validation error for the firstName field") {
        result shouldHaveValidationErrorFor 'firstName
      }
    }

    describe("and the last name is null") {

      val result = sut.validate(instance.copy(lastName = null))

      it("should have a validation error for the lastName field") {
        result shouldHaveValidationErrorFor 'lastName
      }
    }

    describe("and the last name is empty") {

      val result = sut.validate(instance.copy(lastName = ""))

      it("should have a validation error for the lastName field") {
        result shouldHaveValidationErrorFor 'lastName
      }
    }

    describe("and the last name has more than 32 characters") {

      val result = sut.validate(instance.copy(lastName = "012345678901234567890123456789012"))

      it("should have a validation error for the lastName field") {
        result shouldHaveValidationErrorFor 'lastName
      }
    }

    describe("and the age is negative") {

      val result = sut.validate(instance.copy(age = -1))

      it("should have a validation error for the age field") {
        result shouldHaveValidationErrorFor 'age
      }
    }

    describe("and the notes are not present") {

      val result = sut.validate(instance.copy(notes = None))

      it("should not have a validation error for the notes field") {
        result shouldNotHaveValidationErrorFor 'notes
      }
    }

    describe("and the notes are defined but have more than 32 characters") {
      val result = sut.validate(instance.copy(notes = Some("A ridiculously long string that should have more than 32 characters by all means")))

      it("should have a validation error for the notes field") {
        result shouldHaveValidationErrorFor 'notes
      }
    }

    describe("and the married flag is set to true but the age is lower than the marriageable age of 18") {

      val result = sut.validate(instance.copy(age = 17, married = true))

      it("should have a validation error for the married field") {
        result shouldHaveValidationErrorFor 'married
      }

    }

    describe("and the hasJob flag is set to true but the age is lower than the minimum working age of 21") {

      val result = sut.validate(instance.copy(age = 20, hasJob = true))

      it("should have a validation error for the married field") {
        result shouldHaveValidationErrorFor 'hasJob
      }
    }

    describe("and the primary address component zip is longer than 10 characters") {

      val result = sut.validate(instance.copy(primaryAddress = instance.primaryAddress.copy(zip = "ARidiculouslyLongZipCodeHere")))

      it("should have a validation error for the primary address zip") {
        result shouldHaveValidationErrorFor "primaryAddress.zip"
      }

    }

    describe("and one of the addresses zip is longer than 10 characters") {

      val result = sut.validate(instance.copy(otherAddresses = List(Address("line1", "line2", "city", "state", "someZip"), Address("anotherLine1", "anotherLine2", "anotherCity", "anotherState", "aVeryLongZipCodeHere"))))

      it("should have a validation error for the addresses field on the index of the invalid address followed by a dot and the name of the invalid field") {
        result shouldHaveValidationErrorFor "otherAddresses[1].zip"
      }
    }

    describe("and both of the addresses zip are longer than 10 characters") {

      val result = sut.validate(instance.copy(otherAddresses = List(Address("line1", "line2", "city", "state", "aVeryLongZipCodeHere"), Address("anotherLine1", "anotherLine2", "anotherCity", "anotherState", "anotherVeryLongZipCodeHere"))))

      it("should have a validation error for each the addresses field on the index of the invalid address followed by a dot and the name of the invalid field") {
        result shouldHaveValidationErrorFor "otherAddresses[0].zip"
        result shouldHaveValidationErrorFor "otherAddresses[1].zip"
      }
    }

    describe("and the size of number of tasks completed by month is bigger than 12") {

      val result = sut.validate(instance.copy(tasksCompletedByMonth = List(7, 4, 9, 3, 10, 6, 15, 59, 4, 2, 1, 2, 8)))

      it("should have a validation error for the tasks completed by month") {
        result shouldHaveValidationErrorFor 'tasksCompletedByMonth
      }

    }

    describe("and the size of number of tasks completed by month is less than 12") {

      val result = sut.validate(instance.copy(tasksCompletedByMonth = List(7, 4, 9, 3, 10, 6, 15, 59, 4, 2, 1)))

      it("should have a validation error for the tasks completed by month") {
        result shouldHaveValidationErrorFor 'tasksCompletedByMonth
      }

    }

    describe("and one of the number of tasks is less than zero") {

      val result = sut.validate(instance.copy(tasksCompletedByMonth = List(7, 4, -9, 3, -10, 6, 15, 59, 4, -2, 1, 19)))

      it("should have a validation error for the tasks completed by month in the invalid indexes") {
        result shouldHaveValidationErrorFor "tasksCompletedByMonth[2]"
        result shouldHaveValidationErrorFor "tasksCompletedByMonth[4]"
        result shouldHaveValidationErrorFor "tasksCompletedByMonth[9]"
      }
    }

    describe("and the emergency phone number is not provided") {

      val result = sut.validate(instance.copy(emergencyPhoneNumber = None))

      it("should not have a validation error for the emergencyPhoneNumber field") {
        result shouldNotHaveValidationErrorFor 'emergencyPhoneNumber
      }

    }

    describe("and the emergency phone number is provided and the area code has more than four digits") {

      val result = sut.validate(instance.copy(emergencyPhoneNumber = Some(PhoneNumber(areaCode = "99990", number = "aNumber"))))

      it("should have a validation error for the emergencyPhoneNumber.areaCode field") {
        result shouldHaveValidationErrorFor "emergencyPhoneNumber.areaCode"
      }

    }

  }

}