package com.github.novamage.svalidator.binding.binders.special

import com.github.novamage.svalidator.binding.binders.TypedBinder
import com.github.novamage.svalidator.binding.{BindingFailure, BindingPass, FieldError}
import testUtils.Observes

class ListBinderWrapperSpecs extends Observes {

  val wrappedBinder = mock[TypedBinder[Long]]
  val sut: TypedBinder[List[_]] = new ListBinder(wrappedBinder)

  describe("when binding a list of values of a specific type with a single non indexed field name") {

    describe("and some of the values return binding failures") {
      val fieldName = "fieldName"
      val valueMap = Map(
        fieldName -> List("a", "2", "c", "4")
      )

      val first_failure = mock[BindingFailure[Long]]
      val second_failure = mock[BindingFailure[Long]]
      val first_failure_field_errors = List(FieldError(fieldName + ".someSubField", "anError"), FieldError(fieldName + ".yetAnotherSubField", "anotherError"))
      val second_failure_field_errors = List(FieldError(fieldName + ".someOtherSubField", "secondError"), FieldError(fieldName + ".anOldSubField", "secondOtherError"))

      when(first_failure.fieldErrors) thenReturn first_failure_field_errors
      when(second_failure.fieldErrors) thenReturn second_failure_field_errors

      when(wrappedBinder.bind(fieldName, Map(fieldName -> List("a")), identityLocalization)) thenReturn first_failure
      when(wrappedBinder.bind(fieldName, Map(fieldName -> List("c")), identityLocalization)) thenReturn second_failure

      when(wrappedBinder.bind(fieldName, Map(fieldName -> List("2")), identityLocalization)) thenReturn BindingPass(2L)
      when(wrappedBinder.bind(fieldName, Map(fieldName -> List("4")), identityLocalization)) thenReturn BindingPass(4L)

      val result = sut.bind(fieldName, valueMap, identityLocalization)

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

      when(wrappedBinder.bind(fieldName, Map(fieldName -> List("a")), identityLocalization)) thenReturn BindingPass(1L)
      when(wrappedBinder.bind(fieldName, Map(fieldName -> List("c")), identityLocalization)) thenReturn BindingPass(3L)

      when(wrappedBinder.bind(fieldName, Map(fieldName -> List("2")), identityLocalization)) thenReturn BindingPass(2L)
      when(wrappedBinder.bind(fieldName, Map(fieldName -> List("4")), identityLocalization)) thenReturn BindingPass(4L)

      val result = sut.bind(fieldName, valueMap, identityLocalization)

      it("should have returned BindingPass with a list with all BindingPass values bound to it") {
        result should equal(BindingPass(List(1L, 2L, 3L, 4L)))
      }
    }


    describe("when binding a list of values of a specific type with indexed field names") {

      describe("and some of them return binding failures") {

        val fieldName = "fieldName"

        val first_failure = mock[BindingFailure[Long]]
        val second_failure = mock[BindingFailure[Long]]
        val first_failure_field_errors = List(FieldError(fieldName + "[0].someSubFieldName", "anError"), FieldError(fieldName + "[0].otherSubFieldName", "anotherError"))
        val second_failure_field_errors = List(FieldError(fieldName + "[2].aNewSubFieldName", "secondError"), FieldError(fieldName + "[2].yetAnotherSubFieldName", "secondOtherError"))

        when(first_failure.fieldErrors) thenReturn first_failure_field_errors
        when(second_failure.fieldErrors) thenReturn second_failure_field_errors

        val valueMap = Map(
          fieldName + "[0]" -> List("a"),
          fieldName + "[1]" -> List("2"),
          fieldName + "[2]" -> List("c"),
          fieldName + "[3]" -> List("4")
        )

        when(wrappedBinder.bind(fieldName + "[0]", valueMap, identityLocalization)) thenReturn first_failure
        when(wrappedBinder.bind(fieldName + "[2]", valueMap, identityLocalization)) thenReturn second_failure

        when(wrappedBinder.bind(fieldName + "[1]", valueMap, identityLocalization)) thenReturn BindingPass(2L)
        when(wrappedBinder.bind(fieldName + "[3]", valueMap, identityLocalization)) thenReturn BindingPass(4L)

        val result = sut.bind(fieldName, valueMap, identityLocalization)

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

        when(wrappedBinder.bind(fieldName + "[0]", valueMap, identityLocalization)) thenReturn BindingPass(1L)
        when(wrappedBinder.bind(fieldName + "[2]", valueMap, identityLocalization)) thenReturn BindingPass(3L)

        when(wrappedBinder.bind(fieldName + "[1]", valueMap, identityLocalization)) thenReturn BindingPass(2L)
        when(wrappedBinder.bind(fieldName + "[3]", valueMap, identityLocalization)) thenReturn BindingPass(4L)

        val result = sut.bind(fieldName, valueMap, identityLocalization)

        it("should have returned BindingPass with a list with all BindingPass values bound to it") {
          result should equal(BindingPass(List(1L, 2L, 3L, 4L)))
        }
      }
    }
  }

}
