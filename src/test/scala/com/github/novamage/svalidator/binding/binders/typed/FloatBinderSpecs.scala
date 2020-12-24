package com.github.novamage.svalidator.binding.binders.typed

import com.github.novamage.svalidator.binding.binders.{JsonTypedBinder, TypedBinder}
import com.github.novamage.svalidator.binding.{BindingConfig, BindingFailure, BindingPass}
import io.circe.Json
import testUtils.Observes

class FloatBinderSpecs extends Observes {

  describe("when testing the binding of a class with a simple constructor with a float argument") {

    val fieldName = "someFloatFieldName"
    val metadata = mock[Map[String, Any]]

    describe("and the values map method of binding is used") {

      val sut: TypedBinder[Float] = new FloatBinder(BindingConfig.defaultConfig)

      describe("and the argument is not present in the values map") {

        val result = sut.bind(fieldName, Map("someOtherFloat" -> List("5.6")), metadata)

        it("should have returned a Binding Failure with an error for the float field with NoSuchElementException as the cause") {
          result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
          val error = result.fieldErrors.head
          error.fieldName should equal(fieldName)
          error.messageParts should equal(BindingConfig.defaultConfig.languageConfig.noValueProvidedMessage(fieldName))
          result.asInstanceOf[BindingFailure[_]].cause.get.isInstanceOf[NoSuchElementException] should be(true)
        }
      }

      describe("and the argument is present in the values map but it is not a valid float") {

        val invalidFieldValue = "aStringThatCanNotBeParsedAsFloat"
        val result = sut.bind(fieldName, Map(fieldName -> List(invalidFieldValue)), metadata)

        it("should have returned a Binding Failure with an error for the float field") {
          result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
          val error = result.fieldErrors.head
          error.fieldName should equal(fieldName)
          error.messageParts should equal(BindingConfig.defaultConfig.languageConfig.invalidFloatMessage(fieldName, invalidFieldValue))
        }
      }

      describe("and the argument is present in the values map and is a valid float") {

        val result = sut.bind(fieldName, Map(fieldName -> List("90.8")), metadata)

        it("should have bound the valueGetter  properly") {
          result should equal(BindingPass(90.8F))
        }
      }

    }

    describe("and the json method of binding is used") {

      val sut: JsonTypedBinder[Float] = new FloatBinder(BindingConfig.defaultConfig)

      describe("and the argument is not present in the json") {

        val json = Json.obj("someOtherFloat" -> Json.fromFloat(5.6F).get)

        val result = sut.bindJson(json.hcursor.downField(fieldName), fieldName, metadata)

        it("should have returned a Binding Failure with an error for the float field with NoSuchElementException as the cause") {
          result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
          val error = result.fieldErrors.head
          error.fieldName should equal(fieldName)
          error.messageParts should equal(BindingConfig.defaultConfig.languageConfig.noValueProvidedMessage(fieldName))
          result.asInstanceOf[BindingFailure[_]].cause.get.isInstanceOf[NoSuchElementException] should be(true)
        }
      }

      describe("and the argument is present in the values map but it is not a valid float") {

        val invalidFieldValue = "aStringThatCanNotBeParsedAsFloat"
        val json = Json.obj(fieldName -> Json.fromString(invalidFieldValue))

        val result = sut.bindJson(json.hcursor.downField(fieldName), fieldName, metadata)

        it("should have returned a Binding Failure with an error for the float field") {
          result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
          val error = result.fieldErrors.head
          error.fieldName should equal(fieldName)
          error.messageParts should equal(BindingConfig.defaultConfig.languageConfig.invalidFloatMessage(fieldName, invalidFieldValue.quoted))
        }
      }

      describe("and the argument is present in the values map and is a valid float") {

        val json = Json.obj(fieldName -> Json.fromFloat(90.8F).get)

        val result = sut.bindJson(json.hcursor.downField(fieldName), fieldName, metadata)

        it("should have bound the valueGetter  properly") {
          result should equal(BindingPass(90.8F))
        }
      }

    }

  }
}
