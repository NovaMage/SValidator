package com.github.novamage.svalidator.binding.binders.typed

import com.github.novamage.svalidator.binding.binders.{JsonTypedBinder, TypedBinder}
import com.github.novamage.svalidator.binding.{BindingConfig, BindingFailure, BindingPass}
import io.circe.Json
import testUtils.Observes

class DoubleBinderSpecs extends Observes {

  describe("when testing the binding of a class with a simple constructor with a double argument") {

    val fieldName = "someDoubleFieldName"
    val metadata = mock[Map[String, Any]]

    describe("and the values map method of binding is used") {

      val sut: TypedBinder[Double] = new DoubleBinder(BindingConfig.defaultConfig)

      describe("and the argument is not present in the values map") {

        val result = sut.bind(fieldName, Map("someOtherDouble" -> List("8.8")), metadata)

        it("should have returned a Binding Failure with an error for the double field with NoSuchElementException as the cause") {
          result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
          val error = result.fieldErrors.head
          error.fieldName should equal(fieldName)
          error.messageParts should equal(BindingConfig.defaultConfig.languageConfig.noValueProvidedMessage(fieldName))
          result.asInstanceOf[BindingFailure[_]].cause.get.isInstanceOf[NoSuchElementException] should be(true)
        }
      }

      describe("and the argument is present in the values map but it is not a valid double") {

        val invalidFieldValue = "aStringThatCanNotBeParsedAsDouble"

        val result = sut.bind(fieldName, Map(fieldName -> List(invalidFieldValue)), metadata)

        it("should have returned a Binding Failure with an error for the double field") {
          result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
          val error = result.fieldErrors.head
          error.fieldName should equal(fieldName)
          error.messageParts should equal(BindingConfig.defaultConfig.languageConfig.invalidDoubleMessage(fieldName, invalidFieldValue))
        }
      }

      describe("and the argument is present in the values map and is a valid double") {

        val result = sut.bind(fieldName, Map(fieldName -> List("170.5")), metadata)

        it("should have bound the valueGetter  properly") {
          result should equal(BindingPass(170.5D))
        }
      }

    }

    describe("and the json method of binding is used") {

      val sut: JsonTypedBinder[Double] = new DoubleBinder(BindingConfig.defaultConfig)

      describe("and the argument is not present in the values map") {

        val json = Json.obj("someOtherDouble" -> Json.fromDouble(8.8D).get)

        val result = sut.bindJson(json.hcursor.downField(fieldName), Some(fieldName), metadata)

        it("should have returned a Binding Failure with an error for the double field") {
          result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
          val error = result.fieldErrors.head
          error.fieldName should equal(fieldName)
          error.messageParts should equal(BindingConfig.defaultConfig.languageConfig.noValueProvidedMessage(fieldName))
          result.asInstanceOf[BindingFailure[_]].cause.get.isInstanceOf[NoSuchElementException] should be(true)
        }
      }

      describe("and the argument is present in the values map but it is not a valid double") {

        val invalidFieldValue = "aStringThatCanNotBeParsedAsDouble"
        val json = Json.obj(fieldName -> Json.fromString(invalidFieldValue))

        val result = sut.bindJson(json.hcursor.downField(fieldName), Some(fieldName), metadata)

        it("should have returned a Binding Failure with an error for the double field") {
          result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
          val error = result.fieldErrors.head
          error.fieldName should equal(fieldName)
          error.messageParts should equal(BindingConfig.defaultConfig.languageConfig.invalidDoubleMessage(fieldName, invalidFieldValue.quoted))
        }
      }

      describe("and the argument is present in the values map and is a valid double") {


        val json = Json.obj(fieldName -> Json.fromDouble(170.5D).get)

        val result = sut.bindJson(json.hcursor.downField(fieldName), Some(fieldName), metadata)

        it("should have bound the valueGetter  properly") {
          result should equal(BindingPass(170.5D))
        }
      }

    }

  }
}
