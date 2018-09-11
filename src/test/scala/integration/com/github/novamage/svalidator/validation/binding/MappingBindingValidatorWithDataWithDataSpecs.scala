package integration.com.github.novamage.svalidator.validation.binding

import com.github.novamage.svalidator.binding.TypeBinderRegistry
import com.github.novamage.svalidator.testing.ShouldExtensions
import com.github.novamage.svalidator.validation.ValidationWithData
import com.github.novamage.svalidator.validation.binding.{MappingBindingValidator, MappingBindingValidatorWithData}
import com.github.novamage.svalidator.validation.simple.constructs._
import testUtils.Observes

case class ADifferentTestingClass(aString: String, anInt: Int, aFloat: Float, aDecimal: BigDecimal, anOptionalDouble: Option[Double], anOptionalString: Option[String])

case class AMappedTestingClass(aMappedString: String, aMappedInt: Int, aMappedFloat: Float, aMappedDecimal: BigDecimal, aMappedOptionalDouble: Option[Double], aMappedOptionalString: Option[String])

class AMappedTestingClassValidator extends MappingBindingValidator[AMappedTestingClass] {

  def validate(implicit instance: AMappedTestingClass): ValidationWithData[Nothing] = WithRules(
    For { _.aMappedString } ForField 'aString
      must { _.contains("K") } withMessage "A string must contain at least a 'K'",

    For { _.aMappedInt } ForField 'anInt
      must be greaterThan 8 withMessage "An int must be greater than 8"
  )
}

class MappingBindingValidatorWithDataWithDataSpecs extends Observes {

  val sut: MappingBindingValidator[AMappedTestingClass] = new AMappedTestingClassValidator

  val full_map = Map(
    "aString" -> List("someString"),
    "anInt" -> List("5"),
    "aFloat" -> List("88.5"),
    "aDecimal" -> List("900.0000009"),
    "anOptionalDouble" -> List("99.87"),
    "anOptionalString" -> List("anotherString")
  )

  TypeBinderRegistry.initializeBinders()

  val mapOp: ADifferentTestingClass => AMappedTestingClass = x => AMappedTestingClass(x.aString + "SomethingWithTheLetterK", x.anInt + 1000, x.aFloat, x.aDecimal, x.anOptionalDouble, x.anOptionalString)

  describe("when binding and validating a testing class and all values are provided and valid") {

    val result = sut.bindAndValidate(full_map, mapOp)

    it("should have returned a valid summary") {
      result.isValid should be(true)
    }

    it("should have returned the proper valueGetter") {
      result.instance.get should equal(AMappedTestingClass("someStringSomethingWithTheLetterK", 1005, 88.5F, BigDecimal("900.0000009"), Some(99.87D), Some("anotherString")))
    }
  }

  describe("when binding and validating a testing class and some invalid values are provided in the bind") {

    val result = sut.bindAndValidate(full_map.updated("anInt", List("90.9")), mapOp)

    it("should have returned an error for the anInt field") {
      result shouldHaveValidationErrorFor "anInt"
    }

  }

  describe("when binding and validating a testing class and some invalid values are provided to the validation phase") {

    val result = sut.bindAndValidate(full_map.updated("anInt", List("-2000")), mapOp)

    it("should have returned an error for the anInt field") {
      result shouldHaveValidationErrorFor "anInt"
    }

  }

}

