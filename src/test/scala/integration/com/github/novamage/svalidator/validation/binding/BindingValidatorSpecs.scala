package integration.com.github.novamage.svalidator.validation.binding

import com.github.novamage.svalidator.binding.TypeBinderRegistry
import com.github.novamage.svalidator.testing.ShouldExtensions
import com.github.novamage.svalidator.validation.ValidationSummary
import com.github.novamage.svalidator.validation.binding.BindingValidator
import com.github.novamage.svalidator.validation.simple.constructs._
import testUtils.Observes

case class ATestingClass(aString: String, anInt: Int, aFloat: Float, aDecimal: BigDecimal, anOptionalDouble: Option[Double], anOptionalString: Option[String])

class ATestingClassValidator extends BindingValidator[ATestingClass] {

  def validate(implicit instance: ATestingClass): ValidationSummary = WithRules(
    For { _.aString } ForField 'aString
      must have minLength 6 withMessage "A string must have at least 6 characters",

    For { _.anInt } ForField 'anInt
      must be greaterThan 8 withMessage "An int must be greater than 8"

  )
}

class BindingValidatorSpecs extends Observes {

  val sut: BindingValidator[ATestingClass] = new ATestingClassValidator

  val full_map = Map(
    "aString" -> List("someString"),
    "anInt" -> List("90"),
    "aFloat" -> List("88.5"),
    "aDecimal" -> List("900.0000009"),
    "anOptionalDouble" -> List("99.87"),
    "anOptionalString" -> List("anotherString")
  )

  TypeBinderRegistry.initializeBinders()

  describe("when binding and validating a testing class and all values are provided and valid") {

    val result = sut.bindAndValidate(full_map, identityLocalization)

    it("should have returned a valid summary") {
      result.isValid should be(true)
    }

    it("should have returned the proper valueGetter") {
      result.instance.get should equal(ATestingClass("someString", 90, 88.5F, BigDecimal("900.0000009"), Some(99.87D), Some("anotherString")))
    }
  }

  describe("when binding and validating a testing class and some invalid values are provided in the bind") {

    val result = sut.bindAndValidate(full_map.updated("anInt", List("90.9")), identityLocalization)

    it("should have returned an error for the anInt field") {
      result shouldHaveValidationErrorFor "anInt"
    }

  }

  describe("when binding and validating a testing class and some invalid values are provided to the validation phase") {

    val result = sut.bindAndValidate(full_map.updated("anInt", List("5")).updated("aString", List("error")), identityLocalization)

    it("should have returned the aString field") {
      result shouldHaveValidationErrorFor "aString"
    }

    it("should have returned an error for the anInt field") {
      result shouldHaveValidationErrorFor "anInt"
    }

  }

}
