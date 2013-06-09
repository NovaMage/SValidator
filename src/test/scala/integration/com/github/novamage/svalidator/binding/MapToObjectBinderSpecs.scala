package integration.com.github.novamage.svalidator.binding

import testUtils.Observes
import com.github.novamage.svalidator.binding.{BindingPass, TypeBinderRegistry}
import java.sql.Timestamp
import java.text.SimpleDateFormat
import com.github.novamage.svalidator.binding.exceptions.NoBinderFoundException
import com.github.novamage.svalidator.binding.binders.special.MapToObjectBinder

case class AComplexClass(aString: String, anInt: Int, aLong: Long, aBoolean: Boolean, aTimestamp: Timestamp, optionalText: Option[String], optionalInt: Option[Int], intList: List[Int])

case class ASimpleRecursiveClass(anotherString: String, recursiveClass: ClassUsedInRecursiveClass)

case class ClassUsedInRecursiveClass(someInt: Int, someBoolean: Boolean)

class MapToObjectBinderSpecs extends Observes {

  val sut = MapToObjectBinder

  TypeBinderRegistry.initializeBinders()

  val full_map = Map(
    "aString" -> List("someValue"),
    "anInt" -> List("5"),
    "aLong" -> List("8"),
    "aBoolean" -> List("true"),
    "aTimestamp" -> List("2008-09-05"),
    "optionalText" -> List("someText"),
    "optionalInt" -> List("9"),
    "intList" -> List("10", "20", "30")
  )

  val formatter = new SimpleDateFormat("yyyy-MM-dd")

  val full_class = AComplexClass("someValue", 5, 8, true, new Timestamp(formatter.parse("2008-09-05").getTime), Some("someText"), Some(9), List(10, 20, 30))

  describe("when binding a complex class with many types in the constructor") {

    describe("and all values are provided") {

      val result = sut.bind[AComplexClass](full_map)

      it("should return a binding result with a class instantiated with all the values in the map bound to it via constructor") {
        result should equal(BindingPass(full_class))
      }
    }

    describe("and the required string is missing") {

      val result = sut.bind[AComplexClass](full_map - "aString")

      it("should return a binding failure for the missing required field") {
        result.fieldErrors should have size 1
        result.fieldErrors.head.fieldName should equal("aString")
      }
    }

    describe("and the required int is missing") {

      val result = sut.bind[AComplexClass](full_map - "anInt")

      it("should return a binding failure for the missing required field") {
        result.fieldErrors should have size 1
        result.fieldErrors.head.fieldName should equal("anInt")
      }
    }

    describe("and the required long is missing") {

      val result = sut.bind[AComplexClass](full_map - "aLong")

      it("should return a binding failure for the missing required field") {
        result.fieldErrors should have size 1
        result.fieldErrors.head.fieldName should equal("aLong")
      }
    }

    describe("and the required boolean is missing") {

      val result = sut.bind[AComplexClass](full_map - "aBoolean")

      it("should return a binding result with a class instantiated with all the values in the map bound to it " +
        "via constructor and use false for the missing boolean") {
        result should equal(BindingPass(full_class.copy(aBoolean = false)))
      }
    }

    describe("and the required timestamp is missing") {

      val result = sut.bind[AComplexClass](full_map - "aTimestamp")

      it("should return a binding failure for the missing required field") {
        result.fieldErrors should have size 1
        result.fieldErrors.head.fieldName should equal("aTimestamp")
      }
    }

    describe("and the optional text is missing") {

      val result = sut.bind[AComplexClass](full_map - "optionalText")

      it("should return a binding result with a class instantiated with all the values in the map bound to it via constructor") {
        result should equal(BindingPass(full_class.copy(optionalText = None)))
      }
    }

    describe("and the optional int is missing") {

      val result = sut.bind[AComplexClass](full_map - "optionalInt")

      it("should return a binding result with a class instantiated with all the values in the map bound to it via constructor") {
        result should equal(BindingPass(full_class.copy(optionalInt = None)))
      }
    }

    describe("and the list of integers is missing") {

      val result = sut.bind[AComplexClass](full_map - "intList")

      it("should return a binding result with a class instantiated with all the values in the map bound to it via constructor") {
        result should equal(BindingPass(full_class.copy(intList = List())))
      }
    }
  }

  describe("when binding a type with another custom type its constructor") {


    describe("and recursion into the type is allowed") {
      TypeBinderRegistry.allowRecursiveBindingForType[ClassUsedInRecursiveClass]()

      val value_map = Map(
        "anotherString" -> List("anotherValue"),
        "recursiveClass.someInt" -> List("8"),
        "recursiveClass.someBoolean" -> List("true")
      )

      val result = sut.bind[ASimpleRecursiveClass](value_map)

      it("should have bound properly the top class and the recursively bound class") {
        result should equal(BindingPass(ASimpleRecursiveClass("anotherValue", ClassUsedInRecursiveClass(8, true))))
      }
    }

    describe("and recursion into the type is not allowed") {

      val value_map = Map(
        "anotherString" -> List("anotherValue"),
        "recursiveClass.someInt" -> List("8"),
        "recursiveClass.someBoolean" -> List("true")
      )

      val result = try {
        Left(sut.bind[ASimpleRecursiveClass](value_map))
      } catch {
        case ex: NoBinderFoundException => Right(ex)
      }

      it("should have bound properly the top class and the recursively bound class") {
        result should be('right)
      }
    }
  }

}