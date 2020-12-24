package com.github.novamage.svalidator.binding.binders.typed

import com.github.novamage.svalidator.binding.binders.{JsonTypedBinder, TypedBinder}
import com.github.novamage.svalidator.binding.{BindingConfig, BindingFailure, BindingPass}
import io.circe.Json
import testUtils.Observes

class IntBinderSpecs extends Observes {


  describe("when testing the binding of a class with a simple constructor with an int argument") {

    val fieldName = "someIntFieldName"
    val metadata = mock[Map[String, Any]]

    describe("and the values map method of binding is used") {

      val sut: TypedBinder[Int] = new IntBinder(BindingConfig.defaultConfig)

      describe("and the argument is not present in the values map") {

        val result = sut.bind(fieldName, Map("someOtherInt" -> List("5")), metadata)

        it("should have returned a Binding Failure with an error for the int field with a NoSuchElementException as the cause") {
          result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
          val error = result.fieldErrors.head
          error.fieldName should equal(fieldName)
          error.messageParts should equal(BindingConfig.defaultConfig.languageConfig.noValueProvidedMessage(fieldName))
          result.asInstanceOf[BindingFailure[_]].cause.get.isInstanceOf[NoSuchElementException] should be(true)
        }
      }

      describe("and the argument is present in the values map but it is not a valid int") {

        val invalidFieldValue = "98.5"

        val result = sut.bind(fieldName, Map(fieldName -> List(invalidFieldValue)), metadata)

        it("should have returned a Binding Failure with an error for the int field") {
          result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
          val error = result.fieldErrors.head
          error.fieldName should equal(fieldName)
          error.messageParts should equal(BindingConfig.defaultConfig.languageConfig.invalidIntegerMessage(fieldName, invalidFieldValue))
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

      val sut: JsonTypedBinder[Int] = new IntBinder(BindingConfig.defaultConfig)

      describe("and the argument is not present in the json") {

        val json = Json.obj("someOtherInt" -> Json.fromInt(5))

        val result = sut.bindJson(json.hcursor.downField(fieldName), fieldName, metadata)

        it("should have returned a Binding Failure with an error for the int field with a NoSuchElementException as the cause") {
          result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
          val error = result.fieldErrors.head
          error.fieldName should equal(fieldName)
          error.messageParts should equal(BindingConfig.defaultConfig.languageConfig.noValueProvidedMessage(fieldName))
          result.asInstanceOf[BindingFailure[_]].cause.get.isInstanceOf[NoSuchElementException] should be(true)
        }
      }

      describe("and the argument is present in the json but it is not a valid int") {

        val invalidFieldValue = 98.5D

        val json = Json.obj(fieldName -> Json.fromDouble(invalidFieldValue).get)

        val result = sut.bindJson(json.hcursor.downField(fieldName), fieldName, metadata)

        it("should have returned a Binding Failure with an error for the int field") {
          result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
          val error = result.fieldErrors.head
          error.fieldName should equal(fieldName)
          error.messageParts should equal(BindingConfig.defaultConfig.languageConfig.invalidIntegerMessage(fieldName, invalidFieldValue.toString))
        }
      }

      describe("and the argument is present in the json and is a valid int") {

        val validValue = 18

        val json = Json.obj(fieldName -> Json.fromInt(validValue))

        val result = sut.bindJson(json.hcursor.downField(fieldName), fieldName, metadata)

        it("should have bound the valueGetter  properly") {
          result should equal(BindingPass(validValue))
        }
      }

    }

  }
}
