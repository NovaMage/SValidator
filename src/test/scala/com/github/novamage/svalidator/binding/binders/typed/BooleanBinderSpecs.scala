package com.github.novamage.svalidator.binding.binders.typed

import com.github.novamage.svalidator.binding.binders.{JsonTypedBinder, TypedBinder}
import com.github.novamage.svalidator.binding.{BindingConfig, BindingPass}
import io.circe.Json
import testUtils.Observes

class BooleanBinderSpecs extends Observes {

  describe("when testing the binding of a class with a simple constructor with a boolean argument") {

    describe("and the values map method of binding is used") {

      val sut: TypedBinder[Boolean] = new BooleanBinder(BindingConfig.defaultConfig)

      val fieldName = "someBooleanFieldName"
      val metadata = mock[Map[String, Any]]

      describe("and the argument is not present in the values map") {
        val result = sut.bind(fieldName, Map("someOtherBoolean" -> List("true")), metadata)

        it("should have returned a Binding Pass with the valueGetter set to false") {
          result should equal(BindingPass(false))
        }
      }

      describe("and the argument is present in the values map with a false valueGetter") {
        val result = sut.bind(fieldName, Map(fieldName -> List("false")), metadata)

        it("should have returned a Binding Pass with the valueGetter set to false") {
          result should equal(BindingPass(false))
        }
      }

      describe("and the argument is present in the values map with a true valueGetter") {
        val result = sut.bind(fieldName, Map(fieldName -> List("true")), metadata)

        it("should have returned a Binding Pass with the valueGetter set to false") {
          result should equal(BindingPass(true))
        }
      }

      describe("and the argument is present in the values map with a valueGetter that is not a Boolean") {
        val result = sut.bind(fieldName, Map(fieldName -> List("18")), metadata)

        it("should have returned a Binding Pass with the valueGetter set to false") {
          result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
        }
      }

    }

    describe("and the json method of binding is used") {

      val sut: JsonTypedBinder[Boolean] = new BooleanBinder(BindingConfig.defaultConfig)

      val fieldName = "someBooleanFieldName"
      val metadata = mock[Map[String, Any]]

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

        it("should have returned a Binding Pass with the valueGetter set to false") {
          result should equal(BindingPass(false))
        }
      }

      describe("and the argument is present in the json with a true value") {

        val json = Json.obj(fieldName -> Json.fromBoolean(true))

        val result = sut.bindJson(json.hcursor.downField(fieldName), fieldName, metadata)

        it("should have returned a Binding Pass with the valueGetter set to false") {
          result should equal(BindingPass(true))
        }
      }

      describe("and the argument is present in the json with a value that is not a Boolean") {

        val json = Json.obj(fieldName -> Json.fromInt(18))

        val result = sut.bindJson(json.hcursor.downField(fieldName), fieldName, metadata)

        it("should have returned a Binding Pass with the valueGetter set to false") {
          result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
        }
      }

    }

  }
}
