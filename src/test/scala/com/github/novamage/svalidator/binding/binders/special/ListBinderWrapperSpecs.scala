package com.github.novamage.svalidator.binding.binders.special

import testUtils.Observes
import com.github.novamage.svalidator.binding.binders.ITypedBinder
import com.github.novamage.svalidator.binding.{BindingPass, BindingFailure}

class ListBinderWrapperSpecs extends Observes {

  val wrappedBinder = mock[ITypedBinder[Long]]
  val sut: ITypedBinder[List[_]] = new ListBinderWrapper(wrappedBinder)

  describe("when binding a list of values of a specific type with a single non indexed field name") {

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


    describe("when binding a list of values of a specific type with indexed field names") {

      val fieldName = "fieldName"
      val valueMap = Map(
        fieldName + "[0]" -> List("a"),
        fieldName + "[1]" -> List("2"),
        fieldName + "[2]" -> List("c"),
        fieldName + "[3]" -> List("4")
      )

      when(wrappedBinder.bind(fieldName + "[0]", valueMap)) thenReturn mock[BindingFailure[Long]]
      when(wrappedBinder.bind(fieldName + "[2]", valueMap)) thenReturn mock[BindingFailure[Long]]

      when(wrappedBinder.bind(fieldName + "[1]", valueMap)) thenReturn BindingPass(2L)
      when(wrappedBinder.bind(fieldName + "[3]", valueMap)) thenReturn BindingPass(4L)

      val result = sut.bind(fieldName, valueMap)

      it("should have returned BindingPass with a list with all BindingPass values bound to it") {
        result should equal(BindingPass(List(2, 4)))
      }
    }
  }

}
