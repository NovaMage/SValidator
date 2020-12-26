package com.github.novamage.svalidator.binding.binders.special

import com.github.novamage.svalidator.binding.binders.{JsonTypedBinder, TypedBinder}
import com.github.novamage.svalidator.binding.binders.typed.LongBinder
import com.github.novamage.svalidator.binding.{BindingConfig, BindingFailure, BindingPass, FieldError}
import io.circe.Json
import testUtils.Observes

class OptionBinderWrapperSpecs extends Observes {


  describe("when performing the binding of an option type") {

    describe("and the values map method of binding is used") {

      val wrappedBinder = mock[TypedBinder[Long]]
      val sut: TypedBinder[Option[_]] = new OptionBinder(wrappedBinder)

      describe("and the wrapped type binder returns a BindingFailure") {

        val fieldName = "fieldName"
        val valueMap = mock[Map[String, Seq[String]]]
        val errors = mock[List[FieldError]]

        describe("and the binding failure was caused by a no such element exception") {
          val binding_result = BindingFailure[Long](errors, Some(new NoSuchElementException))
          val metadata = mock[Map[String, Any]]
          when(wrappedBinder.bind(fieldName, valueMap, metadata)) thenReturn binding_result

          val result = sut.bind(fieldName, valueMap, metadata)

          it("should return a Binding Pass with a value of None") {
            result should equal(BindingPass(None))
          }

        }

        describe("and the binding failure was not caused by a no such element exception") {
          val exception = new RuntimeException
          val metadata = mock[Map[String, Any]]
          val binding_result = BindingFailure[Long](errors, Some(exception))
          when(wrappedBinder.bind(fieldName, valueMap, metadata)) thenReturn binding_result

          val result = sut.bind(fieldName, valueMap, metadata)

          it("should return a Binding failure with the error and exception provided") {
            result should equal(BindingFailure(errors, Some(exception)))
          }

        }
      }

      describe("and the wrapped type binder returns a BindingPass") {

        val fieldName = "fieldName"
        val valueMap = mock[Map[String, Seq[String]]]
        val boundValue = 8L
        val binding_result = BindingPass(boundValue)
        val metadata = mock[Map[String, Any]]
        when(wrappedBinder.bind(fieldName, valueMap, metadata)) thenReturn binding_result

        val result = sut.bind(fieldName, valueMap, metadata)

        it("should return a BindingPass with the valueGetter returned from the wrapped binder wrapped in Option") {
          result should equal(BindingPass(Option(boundValue)))
        }
      }

    }

    describe("and the json method of binding is used") {

      val wrappedBinder = new LongBinder(BindingConfig.defaultConfig)
      val sut: JsonTypedBinder[Option[_]] = new JsonOptionBinder(wrappedBinder)
      val fieldName = "someFieldName"
      val metadata = mock[Map[String, Any]]

      describe("and the wrapped type binder returns a BindingFailure") {

        describe("and the binding failure was caused by a no such element exception due to absence of value") {

          val json = Json.obj()

          val result = sut.bindJson(json.hcursor.downField(fieldName), Some(fieldName), metadata)

          it("should return a Binding Pass with a value of None") {
            result should equal(BindingPass(None))
          }

        }

        describe("and the binding failure was caused by a no such element exception from presence of a null value") {

          val json = Json.obj(fieldName -> Json.Null)

          val result = sut.bindJson(json.hcursor.downField(fieldName), Some(fieldName), metadata)

          it("should return a Binding Pass with a value of None") {
            result should equal(BindingPass(None))
          }

        }

        describe("and the binding failure was not caused by a no such element exception") {

          val json = Json.obj(fieldName -> Json.fromString("a"))

          val result = sut.bindJson(json.hcursor.downField(fieldName), Some(fieldName), metadata)

          it("should return a Binding failure with the error and exception provided") {
            result.fieldErrors.size should equal(1)
            result.asInstanceOf[BindingFailure[_]].cause.getClass shouldNot equal(classOf[NoSuchElementException])
          }

        }
      }

      describe("and the wrapped type binder returns a BindingPass") {

        val fieldValue = 8L
        val json = Json.obj(fieldName -> Json.fromLong(fieldValue))

        val result = sut.bindJson(json.hcursor.downField(fieldName), Some(fieldName), metadata)

        it("should return a BindingPass with the valueGetter returned from the wrapped binder wrapped in Option") {
          result should equal(BindingPass(Option(fieldValue)))
        }
      }

    }

  }
}
