package com.github.novamage.svalidator.binding.binders.special

import com.github.novamage.svalidator.binding.binders.TypedBinder
import com.github.novamage.svalidator.binding.{BindingFailure, BindingPass, FieldError}
import testUtils.Observes

class OptionBinderWrapperSpecs extends Observes {

  val wrappedBinder = mock[TypedBinder[Long]]
  val sut: TypedBinder[Option[_]] = new OptionBinder(wrappedBinder)

  describe("when performing the binding of an option type") {

    describe("and the wrapped type binder returns a BindingFailure") {

      describe("and the binding failure was caused by a no such element exception") {
        val fieldName = "fieldName"
        val valueMap = mock[Map[String, Seq[String]]]
        val errors = mock[List[FieldError]]
        val binding_result = BindingFailure[Long](errors, Some(new NoSuchElementException))
        when(wrappedBinder.bind(fieldName, valueMap, identityLocalization)) thenReturn binding_result

        val result = sut.bind(fieldName, valueMap, identityLocalization)

        it("should return a Binding Pass with a value of None") {
          result should equal(BindingPass(None))
        }

      }

      describe("and the binding failure was not caused by a no such element exception") {
        val fieldName = "fieldName"
        val valueMap = mock[Map[String, Seq[String]]]
        val errors = mock[List[FieldError]]
        val exception = new RuntimeException
        val binding_result = BindingFailure[Long](errors, Some(exception))
        when(wrappedBinder.bind(fieldName, valueMap, identityLocalization)) thenReturn binding_result

        val result = sut.bind(fieldName, valueMap, identityLocalization)

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
      when(wrappedBinder.bind(fieldName, valueMap, identityLocalization)) thenReturn binding_result

      val result = sut.bind(fieldName, valueMap, identityLocalization)

      it("should return a BindingPass with the valueGetter returned from the wrapped binder wrapped in Option") {
        result should equal(BindingPass(Option(boundValue)))
      }
    }
  }
}
