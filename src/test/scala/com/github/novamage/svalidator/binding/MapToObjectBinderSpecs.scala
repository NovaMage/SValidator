package com.github.novamage.svalidator.binding

import testUtils.Observes


case class StringConstructorClass(someString: String)

case class IntConstructorClass(someInt: Int)

class MapToObjectBinderSpecs extends Observes {

  val sut = new MapToObjectBinder

  describe("when testing the binding of a class with a simple constructor with a string argument") {


    describe("and the argument is not present in the values map") {
      val result = sut.performBind[StringConstructorClass](Map("aDifferentField" -> List("someValue")))

      it("should have returned a Binding Pass for the field with a null value") {
        result should equal(BindingPass(StringConstructorClass(null)))
      }
    }

    describe("and the argument is present in the values map") {
      val result = sut.performBind[StringConstructorClass](Map("someString" -> List("someValue")))

      it("should have bound the value to the class properly") {
        result should equal(BindingPass(StringConstructorClass("someValue")))
      }
    }
  }

  describe("when testing the binding of a class with a simple constructor with an int argument") {


    describe("and the argument is not present in the values map") {
      val result = sut.performBind[IntConstructorClass](Map("someOtherInt" -> List("5")))

      it("should have returned a Binding Failure with an error for the int field") {
        val binding_failure = result.asInstanceOf[BindingFailure[IntConstructorClass]]
        binding_failure.fieldErrors.filter(_.fieldName == "someInt") should have size 1
      }
    }

    describe("and the argument is present in the values map but it is not a valid int") {
      val result = sut.performBind[IntConstructorClass](Map("someInt" -> List("aStringThatCanNotBeParsedAsInt")))

      it("should have returned a Binding Failure with an error for the int field") {
        val binding_failure = result.asInstanceOf[BindingFailure[IntConstructorClass]]
        binding_failure.fieldErrors.filter(_.fieldName == "someInt") should have size 1
      }
    }

    describe("and the argument is present in the values map") {
      val result = sut.performBind[IntConstructorClass](Map("someInt" -> List("18")))

      it("should have bound the value to the class properly") {
        result should equal(BindingPass(IntConstructorClass(18)))
      }
    }
  }

}
