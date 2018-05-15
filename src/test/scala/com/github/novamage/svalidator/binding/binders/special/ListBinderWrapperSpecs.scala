package com.github.novamage.svalidator.binding.binders.special

import com.github.novamage.svalidator.binding.binders.TypedBinder
import com.github.novamage.svalidator.binding.{BindingFailure, BindingPass, FieldError}
import com.github.novamage.svalidator.validation.MessageParts
import testUtils.Observes

class ListBinderWrapperSpecs extends Observes {

  private val wrappedBinder = mock[TypedBinder[Long]]
  val sut: TypedBinder[List[_]] = new ListBinder(wrappedBinder)

  describe("when binding a list of values of a specific type with a single non indexed field name") {

    describe("and some of the values return binding failures") {
      val fieldName = "fieldName"
      val valueMap = Map(
        fieldName -> List("a", "2", "c", "4")
      )

      val first_failure = mock[BindingFailure[Long]]
      val second_failure = mock[BindingFailure[Long]]
      val first_failure_field_errors = List(FieldError(fieldName + ".someSubField", MessageParts("anError")), FieldError(fieldName + ".yetAnotherSubField", MessageParts("anotherError")))
      val second_failure_field_errors = List(FieldError(fieldName + ".someOtherSubField", MessageParts("secondError")), FieldError(fieldName + ".anOldSubField", MessageParts("secondOtherError")))

      when(first_failure.fieldErrors) thenReturn first_failure_field_errors
      when(second_failure.fieldErrors) thenReturn second_failure_field_errors

      when(wrappedBinder.bind(fieldName, Map(fieldName -> List("a")))) thenReturn first_failure
      when(wrappedBinder.bind(fieldName, Map(fieldName -> List("c")))) thenReturn second_failure

      when(wrappedBinder.bind(fieldName, Map(fieldName -> List("2")))) thenReturn BindingPass(2L)
      when(wrappedBinder.bind(fieldName, Map(fieldName -> List("4")))) thenReturn BindingPass(4L)

      val result = sut.bind(fieldName, valueMap)

      it("should have a list of binding failures for each failure encountered, with the name of list field instead of the" +
        " name of sub-field returned by the wrappedBinder") {
        val resultFailure = result.asInstanceOf[BindingFailure[Long]]
        resultFailure.fieldErrors should have size (first_failure_field_errors.size + second_failure_field_errors.size)
        first_failure_field_errors foreach {
          fieldError =>
            resultFailure.fieldErrors should contain(fieldError.copy(fieldName = fieldName))
        }

        second_failure_field_errors foreach {
          fieldError =>
            resultFailure.fieldErrors should contain(fieldError.copy(fieldName = fieldName))
        }

      }
    }

    describe("and none of the values return binding failures") {
      val fieldName = "fieldName"
      val valueMap = Map(
        fieldName -> List("a", "2", "c", "4")
      )

      when(wrappedBinder.bind(fieldName, Map(fieldName -> List("a")))) thenReturn BindingPass(1L)
      when(wrappedBinder.bind(fieldName, Map(fieldName -> List("c")))) thenReturn BindingPass(3L)

      when(wrappedBinder.bind(fieldName, Map(fieldName -> List("2")))) thenReturn BindingPass(2L)
      when(wrappedBinder.bind(fieldName, Map(fieldName -> List("4")))) thenReturn BindingPass(4L)

      val result = sut.bind(fieldName, valueMap)

      it("should have returned BindingPass with a list with all BindingPass values bound to it") {
        result should equal(BindingPass(List(1L, 2L, 3L, 4L)))
      }
    }


    describe("when binding a list of values of a specific type with indexed field names") {

      describe("and some of them return binding failures") {

        val fieldName = "fieldName"

        val first_failure = mock[BindingFailure[Long]]
        val second_failure = mock[BindingFailure[Long]]
        val first_failure_field_errors = List(FieldError(fieldName + "[0].someSubFieldName", MessageParts("anError")), FieldError(fieldName + "[0].otherSubFieldName", MessageParts("anotherError")))
        val second_failure_field_errors = List(FieldError(fieldName + "[2].aNewSubFieldName", MessageParts("secondError")), FieldError(fieldName + "[2].yetAnotherSubFieldName", MessageParts("secondOtherError")))

        when(first_failure.fieldErrors) thenReturn first_failure_field_errors
        when(second_failure.fieldErrors) thenReturn second_failure_field_errors

        val valueMap = Map(
          fieldName + "[0]" -> List("a"),
          fieldName + "[1]" -> List("2"),
          fieldName + "[2]" -> List("c"),
          fieldName + "[3]" -> List("4")
        )

        when(wrappedBinder.bind(fieldName + "[0]", valueMap)) thenReturn first_failure
        when(wrappedBinder.bind(fieldName + "[2]", valueMap)) thenReturn second_failure

        when(wrappedBinder.bind(fieldName + "[1]", valueMap)) thenReturn BindingPass(2L)
        when(wrappedBinder.bind(fieldName + "[3]", valueMap)) thenReturn BindingPass(4L)

        val result = sut.bind(fieldName, valueMap)

        it("should have returned BindingFailure with a field error for all failing values and the field name " +
          "should be the same as returned by the wrapped binder") {
          val resultFailure = result.asInstanceOf[BindingFailure[Long]]
          resultFailure.fieldErrors should have size (first_failure_field_errors.size + second_failure_field_errors.size)
          first_failure_field_errors foreach {
            fieldError =>
              resultFailure.fieldErrors should contain(fieldError)
          }

          second_failure_field_errors foreach {
            fieldError =>
              resultFailure.fieldErrors should contain(fieldError)
          }
        }
      }

      describe("and none of them return binding failures") {
        val fieldName = "fieldName"
        val valueMap = Map(
          fieldName + "[0]" -> List("a"),
          fieldName + "[1]" -> List("2"),
          fieldName + "[2]" -> List("c"),
          fieldName + "[3]" -> List("4")
        )

        when(wrappedBinder.bind(fieldName + "[0]", valueMap)) thenReturn BindingPass(1L)
        when(wrappedBinder.bind(fieldName + "[2]", valueMap)) thenReturn BindingPass(3L)

        when(wrappedBinder.bind(fieldName + "[1]", valueMap)) thenReturn BindingPass(2L)
        when(wrappedBinder.bind(fieldName + "[3]", valueMap)) thenReturn BindingPass(4L)

        val result = sut.bind(fieldName, valueMap)

        it("should have returned BindingPass with a list with all BindingPass values bound to it") {
          result should equal(BindingPass(List(1L, 2L, 3L, 4L)))
        }
      }
    }
  }

}
