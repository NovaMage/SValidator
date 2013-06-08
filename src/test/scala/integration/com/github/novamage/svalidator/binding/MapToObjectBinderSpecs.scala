package integration.com.github.novamage.svalidator.binding

import testUtils.Observes
import java.sql.Timestamp
import java.text.SimpleDateFormat
import com.github.novamage.svalidator.binding.{BindingPass, TypeBinderRegistry, MapToObjectBinder}


case class BooleanConstructorClass(someBoolean: Boolean)

case class StringConstructorClass(someString: String)

case class IntConstructorClass(someInt: Int)

case class LongConstructorClass(someLong: Long)

case class TimestampConstructorClass(someTimestamp: Timestamp)

class MapToObjectBinderSpecs extends Observes {

  val sut = MapToObjectBinder

  TypeBinderRegistry.initializeBinders()

  describe("when testing the binding of a class with a simple constructor with a string argument") {

    describe("and the argument is not present in the values map") {
      val result = sut.performBind[StringConstructorClass](Map("aDifferentField" -> List("someValue")))

      it("should have returned a Binding Failure with a field error for the string field") {
        result.fieldErrors.filter(_.fieldName == "someString") should have size 1
      }
    }

    describe("and the argument is present in the values map") {

      describe("and the argument is an empty string") {
        val result = sut.performBind[StringConstructorClass](Map("someString" -> List("")))

        it("should have returned a Binding Failure with a field error for the string field") {
          result.fieldErrors.filter(_.fieldName == "someString") should have size 1
        }
      }

      describe("and the argument is a whitespace string") {
        val result = sut.performBind[StringConstructorClass](Map("someString" -> List("             ")))

        it("should have returned a Binding Failure with a field error for the string field") {
          result.fieldErrors.filter(_.fieldName == "someString") should have size 1
        }
      }

      describe("and the value is a non-whitespace string with spaces on the edges") {
        val result = sut.performBind[StringConstructorClass](Map("someString" -> List(" someValue ")))

        it("should have bound the trimmed value to the class properly") {
          result should equal(BindingPass(StringConstructorClass("someValue")))
        }
      }
    }
  }

  describe("when testing the binding of a class with a simple constructor with an int argument") {


    describe("and the argument is not present in the values map") {
      val result = sut.performBind[IntConstructorClass](Map("someOtherInt" -> List("5")))

      it("should have returned a Binding Failure with an error for the int field") {
        result.fieldErrors.filter(_.fieldName == "someInt") should have size 1
      }
    }

    describe("and the argument is present in the values map but it is not a valid int") {
      val result = sut.performBind[IntConstructorClass](Map("someInt" -> List("aStringThatCanNotBeParsedAsInt")))

      it("should have returned a Binding Failure with an error for the int field") {
        result.fieldErrors.filter(_.fieldName == "someInt") should have size 1
      }
    }

    describe("and the argument is present in the values map") {
      val result = sut.performBind[IntConstructorClass](Map("someInt" -> List("18")))

      it("should have bound the value to the class properly") {
        result should equal(BindingPass(IntConstructorClass(18)))
      }
    }
  }

  describe("when testing the binding of a class with a simple constructor with a long argument") {


    describe("and the argument is not present in the values map") {
      val result = sut.performBind[LongConstructorClass](Map("someOtherLong" -> List("9")))

      it("should have returned a Binding Failure with an error for the long field") {
        result.fieldErrors.filter(_.fieldName == "someLong") should have size 1
      }
    }

    describe("and the argument is present in the values map but it is not a valid Long") {
      val result = sut.performBind[LongConstructorClass](Map("someLong" -> List("aStringThatCanNotBeParsedAsLong")))

      it("should have returned a Binding Failure with an error for the int field") {
        result.fieldErrors.filter(_.fieldName == "someLong") should have size 1
      }
    }

    describe("and the argument is present in the values map") {
      val result = sut.performBind[LongConstructorClass](Map("someLong" -> List("49")))

      it("should have bound the value to the class properly") {
        result should equal(BindingPass(LongConstructorClass(49)))
      }
    }
  }

  describe("when testing the binding of a class with a simple constructor with a boolean argument") {


    describe("and the argument is not present in the values map") {
      val result = sut.performBind[BooleanConstructorClass](Map("someOtherBoolean" -> List("true")))

      it("should have returned a Binding Pass with the value set to false") {
        result should equal(BindingPass(BooleanConstructorClass(false)))
      }
    }

    describe("and the argument is present in the values map with a false value") {
      val result = sut.performBind[BooleanConstructorClass](Map("someBoolean" -> List("false")))

      it("should have returned a Binding Pass with the value set to false") {
        result should equal(BindingPass(BooleanConstructorClass(false)))
      }
    }

    describe("and the argument is present in the values map with a true value") {
      val result = sut.performBind[BooleanConstructorClass](Map("someBoolean" -> List("true")))

      it("should have returned a Binding Pass with the value set to false") {
        result should equal(BindingPass(BooleanConstructorClass(true)))
      }
    }

    describe("and the argument is present in the values map with a value that is not a Boolean") {
      val result = sut.performBind[BooleanConstructorClass](Map("someBoolean" -> List("18")))

      it("should have returned a Binding Pass with the value set to false") {
        result.fieldErrors.filter(_.fieldName == "someBoolean") should have size 1
      }
    }

  }

  describe("when testing the binding of a class with a simple constructor with a timestamp argument") {


    describe("and the argument is not present in the values map") {
      val result = sut.performBind[TimestampConstructorClass](Map("someOtherTimestamp" -> List("2013-06-08")))

      it("should have returned a Binding Failure with an error for the field") {
        result.fieldErrors.filter(_.fieldName == "someTimestamp") should have size 1
      }
    }

    describe("and the argument is present in the values map with a valid date in the expected format (yyyy-MM-dd by default)") {

      val formatter = new SimpleDateFormat("yyyy-MM-dd")
      val dateString = "2013-02-14"

      val result = sut.performBind[TimestampConstructorClass](Map("someTimestamp" -> List(dateString)))

      it("should have returned a Binding Pass with the value set to the parsed date") {
        result should equal(BindingPass(TimestampConstructorClass(new Timestamp(formatter.parse(dateString).getTime))))
      }
    }

    describe("and the argument is present in the values map with a value that is not a valid date in the expected format") {
      val result = sut.performBind[TimestampConstructorClass](Map("someTimestamp" -> List("aStringThatCanNotBeParsedAsTimestamp")))

      it("should have returned a Binding Pass with the value set to false") {
        result.fieldErrors.filter(_.fieldName == "someTimestamp") should have size 1
      }
    }

  }
}