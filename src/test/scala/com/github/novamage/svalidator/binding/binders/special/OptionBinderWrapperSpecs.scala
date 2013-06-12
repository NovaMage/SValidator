package com.github.novamage.svalidator.binding.binders.special

import testUtils.Observes
import com.github.novamage.svalidator.binding.binders.ITypedBinder
import com.github.novamage.svalidator.binding.{BindingPass, BindingFailure}

class OptionBinderWrapperSpecs extends Observes {

  val wrappedBinder = mock[ITypedBinder[Long]]
  val sut: ITypedBinder[Option[_]] = new OptionBinderWrapper(wrappedBinder)

  describe("when performing the binding of an option type") {

    describe("and the wrapped type binder returns a BindingFailure") {

      val fieldName = "fieldName"
      val valueMap = mock[Map[String, Seq[String]]]
      val binding_result = mock[BindingFailure[Long]]
      when(wrappedBinder.bind(fieldName, valueMap)) thenReturn binding_result

      val result = sut.bind(fieldName, valueMap)

      it("should return a BindingPass with a valueGetter of None") {
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

      it("should return a BindingPass with the valueGetter returned from the wrapped binder wrapped in Option") {
        result should equal(BindingPass(Option(boundValue)))
      }
    }
  }
}
