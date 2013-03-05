package integration

import com.github.novamage.svalidator.validation.{ ValidationFailure, IValidate, simple }
import simple.{ SimpleValidator }
import simple.constructs._
import language.postfixOps
import testUtils.Observes

case class Person(name: String, age: Long, isSingle: Boolean) {}

class PersonValidator extends SimpleValidator[Person] {

  val beEmpty: String => Boolean = _.length == 0
  val beGreaterThan: Long => Long => Boolean = parameter => y => y > parameter

  val b = 'a

  def buildRules = List(
    For { _.name } ForField "name" mustNot be empty() withMessage "i rock" //    For { _.name } ForField "name"
    //      must be empty () withMessage "someMessage"
    //      must have maxLength 10 withMessage "someMessage",
    //    For { _.age } ForField "age"
    //      must { _ > 0 } withMessage "Must be greater than zero",
    //    For { _.name }.ForField("name")
    //      must be empty() withMessage "hola"
    )

}

class ValidationTest extends Observes {

  describe("when testing the person validator") {

    val sut: IValidate[Person] = new PersonValidator

    describe("and the name is longer than 5 characters") {

      val instance = Person("Angel", 25, true)

      lazy val result = sut.validate(instance)

      it("should have returned a validation failure for the name field") {
        result.validationFailures should equal(List(ValidationFailure("name", "someMessage")))
      }
    }

  }

}

