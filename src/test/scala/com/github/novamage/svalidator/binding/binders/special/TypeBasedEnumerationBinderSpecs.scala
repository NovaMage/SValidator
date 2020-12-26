package com.github.novamage.svalidator.binding.binders.special

import com.github.novamage.svalidator.binding.binders.{JsonTypedBinder, TypedBinder}
import com.github.novamage.svalidator.binding.{BindingConfig, BindingFailure, BindingPass}
import io.circe.Json
import testUtils.Observes

import scala.reflect.runtime.{universe => ru}

sealed abstract class ATestCaseObjectEnum(val identifier: Int, description: String) {

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


class TypeBasedEnumerationBinderSpecs extends Observes {


  private val tag = ru.typeTag[ATestCaseObjectEnum]

  describe("when binding an object that is a case object enum") {

    val fieldName = "someFieldName"
    val metadata = mock[Map[String, Any]]

    describe("and the values map method of binding is used") {

      val sut: TypedBinder[Any] = new TypeBasedEnumerationBinder(tag.tpe, tag.mirror, BindingConfig.defaultConfig)

      describe("and no value is provided") {

        val result = sut.bind(fieldName, Map("someOtherFieldName" -> List("3")), metadata)

        it("should have return a bind failure with no such element exception as the cause") {
          val failure = result.asInstanceOf[BindingFailure[ATestCaseObjectEnum]]
          failure.cause.get.getClass should equal(classOf[NoSuchElementException])
        }
      }

      describe("and a value is provided") {

        describe("and it is not a valid int") {

          val result = sut.bind(fieldName, Map(fieldName -> List("b")), metadata)

          it("should have return a bind failure with an exception that is not no such element exception as the cause") {
            val failure = result.asInstanceOf[BindingFailure[ATestCaseObjectEnum]]
            failure.cause.get.getClass should not equal classOf[NoSuchElementException]
          }

        }

        describe("and it is a valid int") {

          describe("and it is not within the list of valid identifiers") {

            val result = sut.bind(fieldName, Map(fieldName -> List("200")), metadata)

            it("should have return a bind failure with no cause") {
              val failure = result.asInstanceOf[BindingFailure[ATestCaseObjectEnum]]
              failure.cause should be(None)
            }

          }

          describe("and it is within the list of valid identifiers") {

            describe("and the first value is tested") {

              val result = sut.bind(fieldName, Map(fieldName -> List("1")), metadata)

              it("should have return a binding pass with the right value") {
                result should equal(BindingPass(ATestCaseObjectEnum.FirstValue))
              }
            }

            describe("and the second value is tested") {

              val result = sut.bind(fieldName, Map(fieldName -> List("2")), metadata)

              it("should have return a binding pass with the right value") {
                result should equal(BindingPass(ATestCaseObjectEnum.SecondValue))
              }
            }

            describe("and the third value is tested") {

              val result = sut.bind(fieldName, Map(fieldName -> List("3")), metadata)

              it("should have return a binding pass with the right value") {
                result should equal(BindingPass(ATestCaseObjectEnum.ThirdValue))
              }
            }


          }

        }

      }

    }

    describe("and the json method of binding is used") {

      val sut: JsonTypedBinder[Any] = new TypeBasedEnumerationBinder(tag.tpe, tag.mirror, BindingConfig.defaultConfig)

      describe("and no value is provided") {

        val json = Json.obj("someOtherFieldName" -> Json.fromInt(3))

        val result = sut.bindJson(json.hcursor.downField(fieldName), Some(fieldName), metadata)

        it("should have return a bind failure with no such element exception as the cause") {
          val failure = result.asInstanceOf[BindingFailure[ATestCaseObjectEnum]]
          failure.cause.get.getClass should equal(classOf[NoSuchElementException])
        }
      }

      describe("and a value is provided") {

        describe("and the value provided is null") {

          val json = Json.obj(fieldName -> Json.Null)

          val result = sut.bindJson(json.hcursor.downField(fieldName), Some(fieldName), metadata)

          it("should have return a bind failure with no such element exception as the cause") {
            val failure = result.asInstanceOf[BindingFailure[ATestCaseObjectEnum]]
            failure.cause.get.getClass should equal(classOf[NoSuchElementException])
          }
        }

        describe("and it is not a valid int") {

          val json = Json.obj(fieldName -> Json.fromString("b"))

          val result = sut.bindJson(json.hcursor.downField(fieldName), Some(fieldName), metadata)

          it("should have return a bind failure with an exception that is not no such element exception as the cause") {
            val failure = result.asInstanceOf[BindingFailure[ATestCaseObjectEnum]]
            failure.cause.get.getClass should not equal classOf[NoSuchElementException]
          }

        }

        describe("and it is a valid int") {

          describe("and it is not within the list of valid identifiers") {

            val json = Json.obj(fieldName -> Json.fromInt(200))

            val result = sut.bindJson(json.hcursor.downField(fieldName), Some(fieldName), metadata)

            it("should have return a bind failure with no cause") {
              val failure = result.asInstanceOf[BindingFailure[ATestCaseObjectEnum]]
              failure.cause should be(None)
            }

          }

          describe("and it is within the list of valid identifiers") {

            describe("and the first value is tested") {

              val json = Json.obj(fieldName -> Json.fromInt(ATestCaseObjectEnum.FirstValue.identifier))

              val result = sut.bindJson(json.hcursor.downField(fieldName), Some(fieldName), metadata)

              it("should have return a binding pass with the right value") {
                result should equal(BindingPass(ATestCaseObjectEnum.FirstValue))
              }
            }

            describe("and the second value is tested") {

              val json = Json.obj(fieldName -> Json.fromInt(ATestCaseObjectEnum.SecondValue.identifier))

              val result = sut.bindJson(json.hcursor.downField(fieldName), Some(fieldName), metadata)

              it("should have return a binding pass with the right value") {
                result should equal(BindingPass(ATestCaseObjectEnum.SecondValue))
              }
            }

            describe("and the third value is tested") {

              val json = Json.obj(fieldName -> Json.fromInt(ATestCaseObjectEnum.ThirdValue.identifier))

              val result = sut.bindJson(json.hcursor.downField(fieldName), Some(fieldName), metadata)

              it("should have return a binding pass with the right value") {
                result should equal(BindingPass(ATestCaseObjectEnum.ThirdValue))
              }
            }


          }

        }

      }

    }


  }

}
