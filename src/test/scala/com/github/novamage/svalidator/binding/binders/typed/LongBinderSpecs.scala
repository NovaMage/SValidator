package com.github.novamage.svalidator.binding.binders.typed

import com.github.novamage.svalidator.binding.binders.{JsonTypedBinder, TypedBinder}
import com.github.novamage.svalidator.binding.{BindingConfig, BindingFailure, BindingPass}
import io.circe.Json
import testUtils.Observes

class LongBinderSpecs extends Observes {

  describe("when testing the binding of a class with a simple constructor with a long argument") {

    val fieldName = "someLongFieldName"
    val metadata = mock[Map[String, Any]]

    describe("and the values map method of binding is used") {

      val sut: TypedBinder[Long] = new LongBinder(BindingConfig.defaultConfig)

      describe("and the argument is not present in the values map") {

        val result = sut.bind(fieldName, Map("someOtherLong" -> List("5")), metadata)

        it("should have returned a Binding Failure with an error for the long field with a NoSuchElementException as the cause") {
          result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
          val error = result.fieldErrors.head
          error.fieldName should equal(fieldName)
          error.messageParts should equal(BindingConfig.defaultConfig.languageConfig.noValueProvidedMessage(fieldName))
          result.asInstanceOf[BindingFailure[_]].cause.get.isInstanceOf[NoSuchElementException] should be(true)
        }
      }

      describe("and the argument is present in the values map but it is not a valid long") {

        val invalidFieldValue = "98.5"

        val result = sut.bind(fieldName, Map(fieldName -> List(invalidFieldValue)), metadata)

        it("should have returned a Binding Failure with an error for the long field") {
          result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
          val error = result.fieldErrors.head
          error.fieldName should equal(fieldName)
          error.messageParts should equal(BindingConfig.defaultConfig.languageConfig.invalidLongMessage(fieldName, invalidFieldValue))
        }
      }

      describe("and the argument is present in the values map") {

        val result = sut.bind(fieldName, Map(fieldName -> List("18")), metadata)

        it("should have bound the valueGetter  properly") {
          result should equal(BindingPass(18))
        }
      }

    }

    describe("and the json method of binding is used") {

      val sut: JsonTypedBinder[Long] = new LongBinder(BindingConfig.defaultConfig)

      describe("and the argument is not present in the json") {

        val json = Json.obj("someOtherLong" -> Json.fromLong(5))

        val result = sut.bindJson(json.hcursor.downField(fieldName), fieldName, metadata)

        it("should have returned a Binding Failure with an error for the long field with a NoSuchElementException as the cause") {
          result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
          val error = result.fieldErrors.head
          error.fieldName should equal(fieldName)
          error.messageParts should equal(BindingConfig.defaultConfig.languageConfig.noValueProvidedMessage(fieldName))
          result.asInstanceOf[BindingFailure[_]].cause.get.isInstanceOf[NoSuchElementException] should be(true)
        }
      }

      describe("and the argument is present in the json but it is not a valid long") {

        val invalidFieldValue = 98.5D

        val json = Json.obj(fieldName -> Json.fromDouble(invalidFieldValue).get)

        val result = sut.bindJson(json.hcursor.downField(fieldName), fieldName, metadata)

        it("should have returned a Binding Failure with an error for the long field") {
          result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
          val error = result.fieldErrors.head
          error.fieldName should equal(fieldName)
          error.messageParts should equal(BindingConfig.defaultConfig.languageConfig.invalidLongMessage(fieldName, invalidFieldValue.toString))
        }
      }

      describe("and the argument is present in the json and is a valid long") {

        val validValue = 18L

        val json = Json.obj(fieldName -> Json.fromLong(validValue))

        val result = sut.bindJson(json.hcursor.downField(fieldName), fieldName, metadata)

        it("should have bound the valueGetter  properly") {
          result should equal(BindingPass(validValue))
        }
      }

    }

  }
}
