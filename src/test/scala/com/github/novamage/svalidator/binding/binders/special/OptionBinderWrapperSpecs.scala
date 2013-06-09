package com.github.novamage.svalidator.binding.binders.special

import testUtils.Observes
import com.github.novamage.svalidator.binding.binders.ITypeBinder
import com.github.novamage.svalidator.binding.{BindingPass, BindingFailure}

class OptionBinderWrapperSpecs extends Observes {

  val wrappedBinder = mock[ITypeBinder[Long]]
  val sut: ITypeBinder[Option[_]] = new OptionBinderWrapper(wrappedBinder)

  describe("when performing the binding of an option type") {

    describe("and the wrapped type binder returns a BindingFailure") {

      val fieldName = "fieldName"
      val valueMap = mock[Map[String, Seq[String]]]
      val binding_result = mock[BindingFailure[Long]]
      when(wrappedBinder.bind(fieldName, valueMap)) thenReturn binding_result

      val result = sut.bind(fieldName, valueMap)

      it("should return a BindingPass with a value of None") {
        result should equal(BindingPass(None))
      }
    }

    describe("and the wrapped type binder returns a BindingPass") {

      val fieldName = "fieldName"
      val valueMap = mock[Map[String, Seq[String]]]
      val boundValue = 8L
      val binding_result = BindingPass(boundValue)
      when(wrappedBinder.bind(fieldName, valueMap)) thenReturn binding_result

      val result = sut.bind(fieldName, valueMap)

      it("should return a BindingPass with the value returned from the wrapped binder wrapped in Option") {
        result should equal(BindingPass(Option(boundValue)))
      }
    }
  }
}
