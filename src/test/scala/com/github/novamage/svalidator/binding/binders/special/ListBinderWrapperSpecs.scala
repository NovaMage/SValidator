package com.github.novamage.svalidator.binding.binders.special

import testUtils.Observes
import com.github.novamage.svalidator.binding.binders.ITypeBinder
import com.github.novamage.svalidator.binding.{BindingPass, BindingFailure}

class ListBinderWrapperSpecs extends Observes {

  val wrappedBinder = mock[ITypeBinder[Long]]
  val sut: ITypeBinder[List[_]] = new ListBinderWrapper(wrappedBinder)

  describe("when binding a list of values of a specific type") {

    val fieldName = "fieldName"
    val valueMap = Map(
      fieldName -> List("a", "2", "c", "4")
    )

    when(wrappedBinder.bind(fieldName, Map(fieldName -> List("a")))) thenReturn mock[BindingFailure[Long]]
    when(wrappedBinder.bind(fieldName, Map(fieldName -> List("c")))) thenReturn mock[BindingFailure[Long]]

    when(wrappedBinder.bind(fieldName, Map(fieldName -> List("2")))) thenReturn BindingPass(2L)
    when(wrappedBinder.bind(fieldName, Map(fieldName -> List("4")))) thenReturn BindingPass(4L)

    val result = sut.bind(fieldName, valueMap)

    it("should have returned BindingPass with a list with all BindingPass values bound to it") {
      result should equal(BindingPass(List(2, 4)))
    }
  }

}
