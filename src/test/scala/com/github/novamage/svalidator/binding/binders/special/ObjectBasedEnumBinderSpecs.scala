package com.github.novamage.svalidator.binding.binders.special

import testUtils.Observes
import scala.reflect.runtime.{universe => ru}
import com.github.novamage.svalidator.binding.{BindingPass, BindingFailure, BindingConfig}

sealed class ATestCaseObjectEnum(val identifier: Int, description: String) {

  //These methods are only to catch corner cases in the test of classes with other methods with similar names or return types
  //Everything should work fine as long as the class has only one constructor and the first arg is an Int and it has a public getter
  def identifier(a: Int) = "a"

  def c(target: Int): Int = 9

  def c: Int = 9
}

object ATestCaseObjectEnum {

  object FirstValue extends ATestCaseObjectEnum(1, "First value")

  object SecondValue extends ATestCaseObjectEnum(2, "Second value")

  object ThirdValue extends ATestCaseObjectEnum(3, "Third value")
}



class ObjectBasedEnumBinderSpecs extends Observes {


  val tag = ru.typeTag[ATestCaseObjectEnum]

  val sut = new ObjectBasedEnumBinder(tag.tpe, tag.mirror, BindingConfig.defaultConfig)

  describe("when binding an object that is a case object enum") {

    val field_name = "someFieldName"

    describe("and no value is provided") {

      val result = sut.bind(field_name, Map("someOtherFieldName" -> List("3")))

      it("should have return a bind failure with no such element exception as the cause") {
        val failure = result.asInstanceOf[BindingFailure[ATestCaseObjectEnum]]
        failure.cause.get.getClass should equal(classOf[NoSuchElementException])
      }
    }

    describe("and a value is provided") {

      describe("and it is not a valid int") {

        val result = sut.bind(field_name, Map(field_name -> List("b")))

        it("should have return a bind failure with an exception that is not no such element exception as the cause") {
          val failure = result.asInstanceOf[BindingFailure[ATestCaseObjectEnum]]
          failure.cause.get.getClass should not equal classOf[NoSuchElementException]
        }

      }

      describe("and it is a valid int") {

        describe("and it is not within the list of valid identifiers") {

          val result = sut.bind(field_name, Map(field_name -> List("200")))

          it("should have return a bind failure with no cause") {
            val failure = result.asInstanceOf[BindingFailure[ATestCaseObjectEnum]]
            failure.cause should be(None)
          }

        }

        describe("and it is within the list of valid identifiers") {

          describe("and the first value is tested") {

            val result = sut.bind(field_name, Map(field_name -> List("1")))

            it("should have return a binding pass with the right value") {
              result should equal(BindingPass(ATestCaseObjectEnum.FirstValue))
            }
          }

          describe("and the second value is tested") {

            val result = sut.bind(field_name, Map(field_name -> List("2")))

            it("should have return a binding pass with the right value") {
              result should equal(BindingPass(ATestCaseObjectEnum.SecondValue))
            }
          }

          describe("and the third value is tested") {

            val result = sut.bind(field_name, Map(field_name -> List("3")))

            it("should have return a binding pass with the right value") {
              result should equal(BindingPass(ATestCaseObjectEnum.ThirdValue))
            }
          }


        }

      }

    }

  }

}
