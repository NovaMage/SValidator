package com.github.novamage.svalidator.binding.binders.typed

import com.github.novamage.svalidator.binding.binders.{JsonTypedBinder, TypedBinder}
import com.github.novamage.svalidator.binding.{BindingConfig, BindingFailure, BindingPass}
import io.circe.Json
import testUtils.Observes

class BigDecimalBinderSpecs extends Observes {

  describe("when testing the binding of a class with a simple constructor with a decimal argument") {

    val fieldName = "someDecimalFieldName"
    val metadata = mock[Map[String, Any]]

    describe("and the string values map version of the method is used") {

      val sut: TypedBinder[BigDecimal] = new BigDecimalBinder(BindingConfig.defaultConfig)

      describe("and the argument is not present in the values map") {

        val result = sut.bind(fieldName, Map("someOtherDouble" -> List("315.00")), metadata)

        it("should have returned a Binding Failure with an error for the decimal field with a NoSuchElementException as the cause") {
          result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
          val error = result.fieldErrors.head
          error.fieldName should equal(fieldName)
          error.messageParts should equal(BindingConfig.defaultConfig.languageConfig.noValueProvidedMessage(fieldName))
          result.asInstanceOf[BindingFailure[_]].cause.get.isInstanceOf[NoSuchElementException] should be(true)
        }
      }

      describe("and the argument is present in the values map but it is not a valid decimal") {

        val invalidFieldValue = "aStringThatCanNotBeParsedAsDecimal"
        val result = sut.bind(fieldName, Map(fieldName -> List(invalidFieldValue)), metadata)

        it("should have returned a Binding Failure with an error for the decimal field") {
          result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
          val error = result.fieldErrors.head
          error.fieldName should equal(fieldName)
          error.messageParts should equal(BindingConfig.defaultConfig.languageConfig.invalidDecimalMessage(fieldName, invalidFieldValue))
        }
      }

      describe("and the argument is present in the values map and is a valid decimal") {

        val result = sut.bind(fieldName, Map(fieldName -> List("170.5000")), metadata)

        it("should have bound the valueGetter  properly") {
          result should equal(BindingPass(BigDecimal("170.5000")))
        }
      }

    }

    describe("and the json version of the method is used") {

      val sut: JsonTypedBinder[BigDecimal] = new BigDecimalBinder(BindingConfig.defaultConfig)

      describe("and the argument is not present in the values map") {

        val json = Json.obj("someOtherDouble" -> Json.fromString("315.00"))

        val result = sut.bindJson(json.hcursor.downField(fieldName), fieldName, metadata)

        it("should have returned a Binding Failure with an error for the decimal field") {
          result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
          val error = result.fieldErrors.head
          error.fieldName should equal(fieldName)
          error.messageParts should equal(BindingConfig.defaultConfig.languageConfig.noValueProvidedMessage(fieldName))
          result.asInstanceOf[BindingFailure[_]].cause.get.isInstanceOf[NoSuchElementException] should be(true)
        }
      }

      describe("and the argument is present in the json but it is not a valid decimal") {

        val invalidFieldValue = "aStringThatCanNotBeParsedAsDecimal"
        val json = Json.obj(fieldName -> Json.fromString(invalidFieldValue))

        val result = sut.bindJson(json.hcursor.downField(fieldName), fieldName, metadata)

        it("should have returned a Binding Failure with an error for the decimal field") {
          result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
          val error = result.fieldErrors.head
          error.fieldName should equal(fieldName)
          error.messageParts should equal(BindingConfig.defaultConfig.languageConfig.invalidDecimalMessage(fieldName, invalidFieldValue.quoted))
        }
      }

      describe("and the argument is present in the values map and is a valid decimal") {

        val targetValue = "170.5000"
        val json = Json.obj(fieldName -> Json.fromString(targetValue))

        val result = sut.bindJson(json.hcursor.downField(fieldName), fieldName, metadata)

        it("should have bound the valueGetter  properly") {
          result should equal(BindingPass(BigDecimal(targetValue)))
        }

      }

    }

  }
}
