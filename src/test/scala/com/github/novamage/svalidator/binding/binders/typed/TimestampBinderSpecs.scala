package com.github.novamage.svalidator.binding.binders.typed

import testUtils.Observes
import java.sql.Timestamp
import com.github.novamage.svalidator.binding.{BindingConfig, BindingPass}
import java.text.SimpleDateFormat
import com.github.novamage.svalidator.binding.binders.ITypeBinder

class TimestampBinderSpecs extends Observes {

  val sut: ITypeBinder[Timestamp] = new TimestampBinder(BindingConfig.defaultConfig)

  describe("when testing the binding of a class with a simple constructor with a timestamp argument") {


    val fieldName = "someTimestamp"
    describe("and the argument is not present in the values map") {
      val result = sut.bind(fieldName, Map("someOtherTimestamp" -> List("2013-06-08")))

      it("should have returned a Binding Failure with an error for the field") {
        result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
      }
    }

    describe("and the argument is present in the values map with a valid date in the expected format (yyyy-MM-dd by default)") {

      val formatter = new SimpleDateFormat("yyyy-MM-dd")
      val dateString = "2013-02-14"

      val result = sut.bind(fieldName, Map(fieldName -> List(dateString)))

      it("should have returned a Binding Pass with the value set to the parsed date") {
        result should equal(BindingPass(new Timestamp(formatter.parse(dateString).getTime)))
      }
    }

    describe("and the argument is present in the values map with a value that is not a valid date in the expected format") {
      val result = sut.bind(fieldName, Map(fieldName -> List("aStringThatCanNotBeParsedAsTimestamp")))

      it("should have returned a Binding Pass with the value set to false") {
        result.fieldErrors.filter(_.fieldName == fieldName) should have size 1
      }
    }

  }
}
