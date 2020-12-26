package com.github.novamage.svalidator.binding.binders.typed

import java.sql.Timestamp
import java.text.SimpleDateFormat
import com.github.novamage.svalidator.binding.binders.{JsonTypedBinder, TypedBinder}
import com.github.novamage.svalidator.binding.{BindingConfig, BindingFailure, BindingPass}
import io.circe.Json
import testUtils.Observes

class TimestampBinderSpecs extends Observes {

  describe("when testing the binding of a class with a simple constructor with a timestamp argument") {

    val fieldName = "someTimestamp"
    val metadata = mock[Map[String, Any]]

    describe("and the values map method of binding is used") {

      val sut: TypedBinder[Timestamp] = new TimestampBinder(BindingConfig.defaultConfig)

      describe("and the argument is not present in the values map") {
        val result = sut.bind(fieldName, Map("someOtherTimestamp" -> List("2013-06-08")), metadata)

        it("should have returned a Binding Failure with an error for the field") {
          result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
          val error = result.fieldErrors.head
          error.fieldName should equal(fieldName)
          error.messageParts should equal(BindingConfig.defaultConfig.languageConfig.noValueProvidedMessage(fieldName))
          result.asInstanceOf[BindingFailure[_]].cause.get.isInstanceOf[NoSuchElementException] should be(true)
        }
      }


      describe("and the argument is present in the values map with a value that is not a valid date in the expected format") {
        val invalidFieldValue = "aStringThatCanNotBeParsedAsTimestamp"

        val result = sut.bind(fieldName, Map(fieldName -> List(invalidFieldValue)), metadata)

        it("should have returned a Binding Pass with the value set to false") {
          result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
          val error = result.fieldErrors.head
          error.fieldName should equal(fieldName)
          error.messageParts should equal(BindingConfig.defaultConfig.languageConfig.invalidTimestampMessage(fieldName, invalidFieldValue))
        }
      }

      describe("and the argument is present in the values map with a valid date in the expected format (yyyy-MM-dd by default)") {

        val formatter = new SimpleDateFormat(BindingConfig.defaultConfig.dateFormat)
        val dateString = "2013-02-14"

        val result = sut.bind(fieldName, Map(fieldName -> List(dateString)), metadata)

        it("should have returned a Binding Pass with the value set to the parsed date") {
          result should equal(BindingPass(new Timestamp(formatter.parse(dateString).getTime)))
        }
      }

    }

    describe("and the json method of binding is used") {

      val sut: JsonTypedBinder[Timestamp] = new TimestampBinder(BindingConfig.defaultConfig)

      describe("and the argument is not present in the values map") {

        val json = Json.obj("someOtherTimestamp" -> Json.fromString("2013-06-08"))

        val result = sut.bindJson(json.hcursor.downField(fieldName), Some(fieldName), metadata)

        it("should have returned a Binding Failure with an error for the field") {
          result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
          val error = result.fieldErrors.head
          error.fieldName should equal(fieldName)
          error.messageParts should equal(BindingConfig.defaultConfig.languageConfig.noValueProvidedMessage(fieldName))
          result.asInstanceOf[BindingFailure[_]].cause.get.isInstanceOf[NoSuchElementException] should be(true)
        }
      }


      describe("and the argument is present in the values map with a value that is not a valid date in the expected format") {
        val invalidFieldValue = "aStringThatCanNotBeParsedAsTimestamp"

        val json = Json.obj(fieldName -> Json.fromString(invalidFieldValue))

        val result = sut.bindJson(json.hcursor.downField(fieldName), Some(fieldName), metadata)

        it("should have returned a Binding Pass with the value set to false") {
          result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
          val error = result.fieldErrors.head
          error.fieldName should equal(fieldName)
          error.messageParts should equal(BindingConfig.defaultConfig.languageConfig.invalidTimestampMessage(fieldName, invalidFieldValue))
        }
      }

      describe("and the argument is present in the values map with a valid date in the expected format (yyyy-MM-dd by default)") {

        val formatter = new SimpleDateFormat(BindingConfig.defaultConfig.dateFormat)
        val dateString = "2013-02-14"

        val json = Json.obj(fieldName -> Json.fromString(dateString))

        val result = sut.bindJson(json.hcursor.downField(fieldName), Some(fieldName), metadata)

        it("should have returned a Binding Pass with the value set to the parsed date") {
          result should equal(BindingPass(new Timestamp(formatter.parse(dateString).getTime)))
        }
      }

    }

  }
}
