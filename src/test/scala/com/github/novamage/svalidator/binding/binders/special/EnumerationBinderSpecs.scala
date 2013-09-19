package com.github.novamage.svalidator.binding.binders.special

import testUtils.Observes
import scala.reflect.runtime.{universe => ru}
import com.github.novamage.svalidator.binding.{BindingConfig, BindingPass, BindingFailure}

object ATestEnumeration extends Enumeration {
  type ATestEnumeration = Value

  val anEnumValue = Value(1)
  val anotherEnumValue = Value(2)
  val yetAnotherEnumValue = Value(3)
}

class EnumerationBinderSpecs extends Observes {

  val testedTypeTag = ru.typeTag[ATestEnumeration.Value]
  val field_name = "someFieldName"

  val sut = new EnumerationBinder(testedTypeTag.tpe, testedTypeTag.mirror, BindingConfig.defaultConfig)

  describe("when binding an enumeration value using the enumeration binder") {

    describe("and the field name is not present in the map") {

      val result = sut.bind(field_name, Map())

      it("should return a bind failure as the result with no such element exception as the cause") {
        val failure = result.asInstanceOf[BindingFailure[ATestEnumeration.Value]]
        failure.cause.get.getClass should equal(classOf[NoSuchElementException])
      }
    }

    describe("and the field name is present in the map") {

      describe("and no value is passed in") {
        val result = sut.bind(field_name, Map(field_name -> List()))

        it("should return a bind failure as the result with no such element exception as the cause") {
          val failure = result.asInstanceOf[BindingFailure[ATestEnumeration.Value]]
          failure.cause.get.getClass should equal(classOf[NoSuchElementException])
        }
      }

      describe("and a value is passed in") {


        describe("and the passed in value is not a valid not an integer"){

          val result = sut.bind(field_name, Map(field_name -> List("notAnInt")))

          it("should return a bind failure as the result and the cause should not equal a no such element exception") {
            val failure = result.asInstanceOf[BindingFailure[ATestEnumeration.Value]]
            failure.cause.get.getClass should not equal classOf[NoSuchElementException]
          }
        }

        describe("and the passed in value is not a valid id of the enumeration"){

          val result = sut.bind(field_name, Map(field_name -> List("1000")))

          it("should return a bind failure as the result and the cause should not equal a no such element exception") {
            val failure = result.asInstanceOf[BindingFailure[ATestEnumeration.Value]]
            failure.cause.get.getClass should not equal classOf[NoSuchElementException]
          }

        }

        describe("and the passed in value is a valid id of the enumeration") {

          val result = sut.bind(field_name, Map(field_name -> List(ATestEnumeration.anotherEnumValue.id.toString)))

          it("should return a successful binding with the return value") {
            val boundValue = result.asInstanceOf[BindingPass[ATestEnumeration.Value]]
            boundValue.value should equal(Some(ATestEnumeration.anotherEnumValue))
          }
        }
      }
    }


  }

}
