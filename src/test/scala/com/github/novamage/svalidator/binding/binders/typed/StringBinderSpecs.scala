package com.github.novamage.svalidator.binding.binders.typed

import com.github.novamage.svalidator.binding.binders.{JsonTypedBinder, TypedBinder}
import com.github.novamage.svalidator.binding.{BindingConfig, BindingFailure, BindingPass}
import io.circe.Json
import testUtils.Observes

class StringBinderSpecs extends Observes {


  describe("when testing the binding of a class with a simple constructor with a string argument") {

    val fieldName = "someStringFieldName"
    val metadata = mock[Map[String, Any]]


    describe("and the values map method of binding is used") {

      val sut: TypedBinder[String] = new StringBinder(BindingConfig.defaultConfig)

      describe("and the argument is not present in the values map") {
        val result = sut.bind(fieldName, Map("aDifferentField" -> List("someValue")), metadata)

        it("should have returned a Binding Failure with a field error for the string field") {
          result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
          val error = result.fieldErrors.head
          error.fieldName should equal(fieldName)
          error.messageParts should equal(BindingConfig.defaultConfig.languageConfig.invalidNonEmptyTextMessage(fieldName))
          result.asInstanceOf[BindingFailure[_]].cause.get.isInstanceOf[NoSuchElementException] should be(true)
        }
      }

      describe("and the argument is present in the values map") {

        describe("and the argument is an empty string") {

          val result = sut.bind(fieldName, Map(fieldName -> List("")), metadata)

          it("should have returned a Binding Failure with a field error for the string field") {
            result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
            val error = result.fieldErrors.head
            error.fieldName should equal(fieldName)
            error.messageParts should equal(BindingConfig.defaultConfig.languageConfig.invalidNonEmptyTextMessage(fieldName))
            result.asInstanceOf[BindingFailure[_]].cause.get.isInstanceOf[NoSuchElementException] should be(true)
          }
        }

        describe("and the argument is a whitespace string") {

          val result = sut.bind(fieldName, Map(fieldName -> List("             ")), metadata)

          it("should have returned a Binding Failure with a field error for the string field") {
            result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
            val error = result.fieldErrors.head
            error.fieldName should equal(fieldName)
            error.messageParts should equal(BindingConfig.defaultConfig.languageConfig.invalidNonEmptyTextMessage(fieldName))
            result.asInstanceOf[BindingFailure[_]].cause.get.isInstanceOf[NoSuchElementException] should be(true)
          }
        }

        describe("and the valueGetter is a non-whitespace string with spaces on the edges") {
          val fieldValue = " someValue "

          val result = sut.bind(fieldName, Map(fieldName -> List(fieldValue)), metadata)

          it("should have bound the property including its spaces properly") {
            result should equal(BindingPass(fieldValue))
          }
        }
      }

    }

    describe("and the json method of binding is used") {

      val sut: JsonTypedBinder[String] = new StringBinder(BindingConfig.defaultConfig)

      describe("and the argument is not present in the values map") {

        val json = Json.obj("aDifferentField" -> Json.fromString("someValue"))

        val result = sut.bindJson(json.hcursor.downField(fieldName), Some(fieldName), metadata)

        it("should have returned a Binding Failure with a field error for the string field") {
          result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
          val error = result.fieldErrors.head
          error.fieldName should equal(fieldName)
          error.messageParts should equal(BindingConfig.defaultConfig.languageConfig.invalidNonEmptyTextMessage(fieldName))
          result.asInstanceOf[BindingFailure[_]].cause.get.isInstanceOf[NoSuchElementException] should be(true)
        }
      }

      describe("and the argument is present in the values map") {

        describe("and the argument is an empty string") {

          val json = Json.obj(fieldName -> Json.fromString(""))

          val result = sut.bindJson(json.hcursor.downField(fieldName), Some(fieldName), metadata)

          it("should have returned a Binding Failure with a field error for the string field") {
            result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
            val error = result.fieldErrors.head
            error.fieldName should equal(fieldName)
            error.messageParts should equal(BindingConfig.defaultConfig.languageConfig.invalidNonEmptyTextMessage(fieldName))
            result.asInstanceOf[BindingFailure[_]].cause.get.isInstanceOf[NoSuchElementException] should be(true)
          }
        }

        describe("and the argument is a whitespace string") {

          val json = Json.obj(fieldName -> Json.fromString("             "))

          val result = sut.bindJson(json.hcursor.downField(fieldName), Some(fieldName), metadata)

          it("should have returned a Binding Failure with a field error for the string field") {
            result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
            val error = result.fieldErrors.head
            error.fieldName should equal(fieldName)
            error.messageParts should equal(BindingConfig.defaultConfig.languageConfig.invalidNonEmptyTextMessage(fieldName))
            result.asInstanceOf[BindingFailure[_]].cause.get.isInstanceOf[NoSuchElementException] should be(true)
          }
        }

        describe("and the valueGetter is a non-whitespace string with spaces on the edges") {

          val fieldValue = " someValue "

          val json = Json.obj(fieldName -> Json.fromString(fieldValue))

          val result = sut.bindJson(json.hcursor.downField(fieldName), Some(fieldName), metadata)

          it("should have bound the property including its spaces properly") {
            result should equal(BindingPass(fieldValue))
          }
        }
      }

    }

  }
}
