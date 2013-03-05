package integration

import com.github.novamage.svalidator.validation.{ ValidationFailure, IValidate, simple }
import simple.{ SimpleValidator }
import simple.constructs._
import language.postfixOps
import testUtils.Observes
import com.github.novamage.svalidator.validation.ValidationFailure

case class Person(name: String, age: Long, isSingle: Boolean) {}

class PersonValidator extends SimpleValidator[Person] {

  def buildRules = List(
    For { _.name } ForField "name" mustNot be empty() withMessage "someMessage"     )

}

class ValidationTest extends Observes {

  describe("when testing the person validator") {

    val sut: IValidate[Person] = new PersonValidator

    describe("and the name is not empty") {

      val instance = Person("Angel", 25, true)

      lazy val result = sut.validate(instance)

      it("should have passed the validation") {
        result.validationFailures should equal(List())
      }
    }
    
    describe("and the name is empty") {

      val instance = Person("", 25, true)

      lazy val result = sut.validate(instance)

      it("should have passed the validation") {
        result.validationFailures should equal(List(ValidationFailure("name","someMessage")))
      }
    }

  }

}

