package integration.com.github.novamage.svalidator.validation.simple

import com.github.novamage.svalidator.testing._
import com.github.novamage.svalidator.utils.TypeBasedEnumeration
import com.github.novamage.svalidator.validation.ValidationSummary
import com.github.novamage.svalidator.validation.binding.{BindingAndValidationWithData, Failure, Success}
import com.github.novamage.svalidator.validation.simple.SimpleValidator
import com.github.novamage.svalidator.validation.simple.constructs._
import testUtils.Observes

case class Address(line1: String, line2: String, city: String, state: String, zip: String)

case class PhoneNumber(areaCode: String, number: String)

sealed abstract case class Gender(id: Int, description: String, abbreviation: Char) extends Gender.Value

object Gender extends TypeBasedEnumeration[Gender] {

  object Male extends Gender(1, "Male", 'M')

  object Female extends Gender(2, "Female", 'F')

}


case class Person(firstName: String,
                  lastName: String,
                  age: Int,
                  gender: Gender,
                  married: Boolean,
                  hasJob: Boolean,
                  notes: Option[String],
                  primaryAddress: Address,
                  otherAddresses: List[Address],
                  tasksCompletedByMonth: List[Int],
                  emergencyPhoneNumber: Option[PhoneNumber])


class AddressValidator extends SimpleValidator[Address] {

  def validate(implicit instance: Address): ValidationSummary = WithRules(
    For { _.zip } ForField 'zip
      must have maxLength 10 withMessage "Must have 10 characters or less",
  )

}

class PhoneNumberValidator extends SimpleValidator[PhoneNumber] {
  def validate(implicit instance: PhoneNumber): ValidationSummary = WithRules(
    For { _.areaCode } ForField 'areaCode
      must have maxLength 4 withMessage "The area code can not exceed 4 characters"
  )
}

class PersonValidator extends SimpleValidator[Person] {

  override def validate(implicit instance: Person): ValidationSummary = WithRules(

    For { _.firstName } ForField 'firstName
      mustNot be empty() withMessage "First name is required"
      must have maxLength 32 withMessage "Must have 32 characters or less",

    For { _.lastName } ForField 'lastName
      mustNot be empty() withMessage "Last name is required"
      must have maxLength 32 withMessage "Must have 32 characters or less",

    For { _.age } ForField 'age
      mustNot be negative() withMessage "Must be a positive number",

    When { _.age < 21 }(
      For { _.hasJob } ForField 'hasJob
        must be(false) withMessage "Must be 21 years or older to allow marking a job",
      For { _.married } ForField 'married
        must { _ == false } withMessage s"Can't be married at ${ instance.age } Must be 21 years or older to allow marking marriage"
    ),


    For { _.tasksCompletedByMonth } ForField 'tasksCompletedByMonth
      must have size 12 withMessage "Must have 12 values for the tasks completed by month",

    For { _.notes } ForField 'notes
      must { _.isDefined } withMessage "Phone is required"
      map { _.get }
      must have maxLength 32 withMessage "Notes can't have more than 32 characters"
      map { _.charAt(0) }
      must { _.isLetter } withMessage "Must start with a letter",

    ForEach { _.tasksCompletedByMonth } ForField 'tasksCompletedByMonth
      must be positive() withMessage "Must be a positive number",

    ForComponent { _.primaryAddress } ForField 'primaryAddress
      validateUsing new AddressValidator,

    For { _.emergencyPhoneNumber } ForField 'emergencyPhoneNumber
      mustNot be empty() withMessage "An emergency number is needed if the person is a minor" when { _.age < 21 },

    ForOptionalComponent { _.emergencyPhoneNumber } ForField 'emergencyPhoneNumber
      validateUsing new PhoneNumberValidator,

    ForEachComponent { _.otherAddresses } ForField 'otherAddresses
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
      gender = Gender.Male,
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

      it("should have a validation error for the notes field") {
        result shouldHaveValidationErrorFor 'notes
      }
    }

    describe("and the notes are defined but have more than 32 characters") {
      val result = sut.validate(instance.copy(notes = Some("A ridiculously long string that should have more than 32 characters by all means")))

      it("should have a validation error for the notes field") {
        result shouldHaveValidationErrorFor 'notes
      }
    }

    describe("and the notes are defined and have less than 32 characters but don't start with a letter") {
      val result = sut.validate(instance.copy(notes = Some("3 This starts with a number")))

      it("should have a validation error for the notes field") {
        result shouldHaveValidationErrorFor 'notes
      }
    }

    describe("and the married flag is set to true but the age is lower than the marriageable age of 21") {

      val result = sut.validate(instance.copy(age = 20, married = true))

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

      describe("and the person is 21 or older") {
        val result = sut.validate(instance.copy(emergencyPhoneNumber = None, age = 21))

        it("should not have a validation error for the emergencyPhoneNumber field") {
          result shouldNotHaveValidationErrorFor 'emergencyPhoneNumber
        }
      }

      describe("and the person is younger than 21") {
        val result = sut.validate(instance.copy(emergencyPhoneNumber = None, age = 20))

        it("should have a validation error for the emergencyPhoneNumber field") {
          result shouldHaveValidationErrorFor 'emergencyPhoneNumber
        }
      }

    }

    describe("and the emergency phone number is provided and the area code has more than four digits") {

      val result = sut.validate(instance.copy(emergencyPhoneNumber = Some(PhoneNumber(areaCode = "99990", number = "aNumber"))))

      it("should have a validation error for the emergencyPhoneNumber.areaCode field") {
        result shouldHaveValidationErrorFor "emergencyPhoneNumber.areaCode"
      }

    }

  }

  val c: BindingAndValidationWithData[String, Nothing] = Success.apply("Hola", Map.empty, None)

  //This is just sort of a compile time test to ensure binding validation summaries can be unapplied properly.
  c match {
    case Success(instance) => instance.length
    case Failure(failures) => failures.size
  }

}