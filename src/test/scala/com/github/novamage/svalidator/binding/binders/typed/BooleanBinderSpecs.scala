package com.github.novamage.svalidator.binding.binders.typed

import com.github.novamage.svalidator.binding.binders.{JsonTypedBinder, TypedBinder}
import com.github.novamage.svalidator.binding.{BindingConfig, BindingPass}
import io.circe.Json
import testUtils.Observes

class BooleanBinderSpecs extends Observes {

  describe("when testing the binding of a class with a simple constructor with a boolean argument") {

    val fieldName = "someBooleanFieldName"
    val metadata = mock[Map[String, Any]]

    describe("and the values map method of binding is used") {

      val sut: TypedBinder[Boolean] = new BooleanBinder(BindingConfig.defaultConfig)

      describe("and the argument is not present in the values map") {

        val result = sut.bind(fieldName, Map("someOtherBoolean" -> List("true")), metadata)

        it("should have returned a Binding Pass with the value set to false") {
          result should equal(BindingPass(false))
        }
      }

      describe("and the argument is present in the values map with a false value") {

        val result = sut.bind(fieldName, Map(fieldName -> List("false")), metadata)

        it("should have returned a Binding Pass with the value set to false") {
          result should equal(BindingPass(false))
        }
      }

      describe("and the argument is present in the values map with a true value") {

        val result = sut.bind(fieldName, Map(fieldName -> List("true")), metadata)

        it("should have returned a Binding Pass with the value set to false") {
          result should equal(BindingPass(true))
        }
      }

      describe("and the argument is present in the values map with a value that is not a Boolean") {

        val invalidFieldValue = "18"

        val result = sut.bind(fieldName, Map(fieldName -> List(invalidFieldValue)), metadata)

        it("should have returned a Binding Failure with an invalid boolean error for the field") {
          result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
          val error = result.fieldErrors.head
          error.fieldName should equal(fieldName)
          error.messageParts should equal(BindingConfig.defaultConfig.languageConfig.invalidBooleanMessage(fieldName, invalidFieldValue))
        }
      }

    }

    describe("and the json method of binding is used") {

      val sut: JsonTypedBinder[Boolean] = new BooleanBinder(BindingConfig.defaultConfig)

      describe("and the argument is not present in the json") {

        val json = Json.obj("someOtherBoolean" -> Json.fromBoolean(true))

        val result = sut.bindJson(json.hcursor.downField(fieldName), fieldName, metadata)

        it("should have returned a Binding Pass with the value set to false") {
          result should equal(BindingPass(false))
        }
      }

      describe("and the argument is present in the json with a false value") {
        val json = Json.obj(fieldName -> Json.fromBoolean(false))

        val result = sut.bindJson(json.hcursor.downField(fieldName), fieldName, metadata)

        it("should have returned a Binding Pass with the value set to false") {
          result should equal(BindingPass(false))
        }
      }

      describe("and the argument is present in the json with a true value") {

        val json = Json.obj(fieldName -> Json.fromBoolean(true))

        val result = sut.bindJson(json.hcursor.downField(fieldName), fieldName, metadata)

        it("should have returned a Binding Pass with the value set to false") {
          result should equal(BindingPass(true))
        }
      }

      describe("and the argument is present in the json with a value that is not a Boolean") {

        val invalidFieldValue = 18
        val json = Json.obj(fieldName -> Json.fromInt(invalidFieldValue))

        val result = sut.bindJson(json.hcursor.downField(fieldName), fieldName, metadata)

        it("should have returned a Binding Pass with the value set to false") {
          result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
          val error = result.fieldErrors.head
          error.fieldName should equal(fieldName)
          error.messageParts should equal(BindingConfig.defaultConfig.languageConfig.invalidBooleanMessage(fieldName, invalidFieldValue.toString))
        }
      }

    }

  }
}
