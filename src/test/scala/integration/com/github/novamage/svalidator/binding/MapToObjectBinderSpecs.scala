package integration.com.github.novamage.svalidator.binding

import testUtils.Observes
import com.github.novamage.svalidator.binding.{BindingPass, TypeBinderRegistry}
import java.sql.Timestamp
import java.text.SimpleDateFormat
import com.github.novamage.svalidator.binding.exceptions.NoBinderFoundException
import com.github.novamage.svalidator.binding.binders.special.MapToObjectBinder

object AnEnumType extends Enumeration {
  type AnEnumType = Value

  val anExampleEnumValue = Value(1, "Just an example value")
  val anotherExampleEnumValue = Value(2, "Just another example value")
}

sealed class AnObjectBasedEnum(val id: Int, someDescription: String, somethingElse: Any)

object AnObjectBasedEnum {
  object FirstOption extends AnObjectBasedEnum(1, "The first option", "anything1")

  object SecondOption extends AnObjectBasedEnum(2, "The second option", BigDecimal("1000"))

  object ThirdOption extends AnObjectBasedEnum(3, "The third option", true)
}

sealed class AnotherObjectBasedEnumWithAnAlternativeConstructor(val id: Int, someDescription: String, somethingElse: Any) {

  def this(id: Int) = this(id, "", "")
}

object AnotherObjectBasedEnumWithAnAlternativeConstructor {
  object AnotherFirstOption extends AnotherObjectBasedEnumWithAnAlternativeConstructor(1, "The first option", "anything1")

  object AnotherSecondOption extends AnotherObjectBasedEnumWithAnAlternativeConstructor(2, "The second option", BigDecimal("1000"))

  object AnotherThirdOption extends AnotherObjectBasedEnumWithAnAlternativeConstructor(3, "The third option", true)
}


sealed class AnotherObjectBasedEnumWithAnNonIntFirstArgumentConstructor(val id: Long, someDescription: String, somethingElse: Any){
}

object AnotherObjectBasedEnumWithAnNonIntFirstArgumentConstructor {
  object YetAnotherFirstOption extends AnotherObjectBasedEnumWithAnNonIntFirstArgumentConstructor(1, "The first option", "anything1")

  object YetAnotherSecondOption extends AnotherObjectBasedEnumWithAnNonIntFirstArgumentConstructor(2, "The second option", BigDecimal("1000"))

  object YetAnotherThirdOption extends AnotherObjectBasedEnumWithAnNonIntFirstArgumentConstructor(3, "The third option", true)
}

sealed class AnotherObjectBasedEnumWithAPrivateGetterFirstArgumentConstructor(private val id: Int, someDescription: String, somethingElse: Any){
}

object AnotherObjectBasedEnumWithAPrivateGetterFirstArgumentConstructor {
  object PrivateFirstOption extends AnotherObjectBasedEnumWithAPrivateGetterFirstArgumentConstructor(1, "The first option", "anything1")

  object PrivateSecondOption extends AnotherObjectBasedEnumWithAPrivateGetterFirstArgumentConstructor(2, "The second option", BigDecimal("1000"))

  object PrivateThirdOption extends AnotherObjectBasedEnumWithAPrivateGetterFirstArgumentConstructor(3, "The third option", true)
}

sealed class AnObjectEnumWithAnEnumValueOutsideCompanionObject(val id: Int, someDescription: String, somethingElse: Any){
}


object AnObjectEnumWithAnEnumValueOutsideCompanionObject {

  object InsideOption1 extends AnObjectEnumWithAnEnumValueOutsideCompanionObject(2, "The second option", BigDecimal("1000"))

  object InsideOption2 extends AnObjectEnumWithAnEnumValueOutsideCompanionObject(3, "The third option", true)
}

object OutsideOption1 extends AnObjectEnumWithAnEnumValueOutsideCompanionObject(1, "The first option", "anything1")


case class AComplexClass(aString: String, anInt: Int, aLong: Long, aBoolean: Boolean, aTimestamp: Timestamp, optionalText: Option[String], optionalInt: Option[Int], intList: List[Int],
                         enumeratedValue: AnEnumType.Value, anObjectBasedEnum: AnObjectBasedEnum)

case class ASimpleRecursiveClass(anotherString: String, recursiveClass: ClassUsedInRecursiveClass)

case class ClassUsedInRecursiveClass(someInt: Int, someBoolean: Boolean)

case class AClassWithAnIndexedList(anIndexedList: List[AnIndexedListValue])

case class AnIndexedListValue(stringField: String, longField: Long)

case class AClassWithAnObjectEnumWithAnAlternateConstructor(objectEnumWithAlternateConstructor: AnotherObjectBasedEnumWithAnAlternativeConstructor)

case class AClassWithAnObjectEnumWithNonIntFirstArgOnConstructor(objectEnumWithNonIntFirstArgConstructor: AnotherObjectBasedEnumWithAnNonIntFirstArgumentConstructor)

case class AClassWithAnObjectEnumWithAPrivateIntFirstArgOnConstructor(anotherObjectBasedEnumWithAPrivateGetterFirstArgumentConstructor: AnotherObjectBasedEnumWithAPrivateGetterFirstArgumentConstructor)

case class AClassWithAnObjectEnumThatHasAValueOutsideItsCompanionObject(anObjectEnumWithAnEnumValueOutsideCompanionObject: AnObjectEnumWithAnEnumValueOutsideCompanionObject)

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
    "intList" -> List("10", "20", "30"),
    "enumeratedValue" -> List("1"),
    "anObjectBasedEnum" -> List("3")
  )

  val formatter = new SimpleDateFormat("yyyy-MM-dd")

  val full_class = AComplexClass("someValue", 5, 8, true, new Timestamp(formatter.parse("2008-09-05").getTime), Some("someText"), Some(9), List(10, 20, 30), AnEnumType.anExampleEnumValue, AnObjectBasedEnum.ThirdOption)

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

    describe("and the required enum is missing") {

      val result = sut.bind[AComplexClass](full_map - "enumeratedValue")

      it("should return a binding failure for the missing required field") {
        result.fieldErrors should have size 1
        result.fieldErrors.head.fieldName should equal("enumeratedValue")
      }
    }

    describe("and the required object enum is missing") {

      val result = sut.bind[AComplexClass](full_map - "anObjectBasedEnum")

      it("should return a binding failure for the missing required field") {
        result.fieldErrors should have size 1
        result.fieldErrors.head.fieldName should equal("anObjectBasedEnum")
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

    describe("when binding a class that has a list of a custom type and it is indexed") {

      TypeBinderRegistry.allowRecursiveBindingForType[AnIndexedListValue]()

      val value_map = Map(
        "anIndexedList[0].stringField" -> List("alpha"),
        "anIndexedList[0].longField" -> List("3"),
        "anIndexedList[1].stringField" -> List("beta"),
        "anIndexedList[1].longField" -> List("9"),
        "anIndexedList[2].stringField" -> List("gamma"),
        "anIndexedList[2].longField" -> List("1"),
        "anIndexedList[3].stringField" -> List("lambda"),
        "anIndexedList[3].longField" -> List("39")
      )

      val result = sut.bind[AClassWithAnIndexedList](value_map)

      it("should have bound properly the list and the recursive values") {
        result should equal(BindingPass(AClassWithAnIndexedList(List(
          AnIndexedListValue("alpha", 3),
          AnIndexedListValue("beta", 9),
          AnIndexedListValue("gamma", 1),
          AnIndexedListValue("lambda", 39)
        ))))
      }
    }

    describe("when binding a class that has a list of a custom type and it is indexed and the value map is not normalized") {

      TypeBinderRegistry.allowRecursiveBindingForType[AnIndexedListValue]()

      val value_map = Map(
        "anIndexedList[0][stringField]" -> List("alpha"),
        "anIndexedList[0][longField]" -> List("3"),
        "anIndexedList[1][stringField]" -> List("beta"),
        "anIndexedList[1][longField]" -> List("9"),
        "anIndexedList[2][stringField]" -> List("gamma"),
        "anIndexedList[2][longField]" -> List("1"),
        "anIndexedList[3][stringField]" -> List("lambda"),
        "anIndexedList[3][longField]" -> List("39")
      )

      val result = sut.bind[AClassWithAnIndexedList](value_map)

      it("should have bound properly the list and the recursive values") {
        result should equal(BindingPass(AClassWithAnIndexedList(List(
          AnIndexedListValue("alpha", 3),
          AnIndexedListValue("beta", 9),
          AnIndexedListValue("gamma", 1),
          AnIndexedListValue("lambda", 39)
        ))))
      }
    }
  }

  describe("when binding a class that has an object enum that has an alternate constructor") {
    val values_map = Map(
      "objectEnumWithAlternateConstructor" -> List("1")
    )

    val result = try {
      Left(sut.bind[AClassWithAnObjectEnumWithAnAlternateConstructor](values_map))
    } catch {
      case ex: NoBinderFoundException => Right(ex)
    }

    it("should have thrown a no binder found exception") {
      result should be('right)
    }

  }

  describe("when binding a class that has an object enum that has one constructor, but the first parameter isn't an int") {
    val values_map = Map(
      "objectEnumWithNonIntFirstArgConstructor" -> List("2")
    )

    val result = try {
      Left(sut.bind[AClassWithAnObjectEnumWithNonIntFirstArgOnConstructor](values_map))
    } catch {
      case ex: NoBinderFoundException => Right(ex)
    }

    it("should have thrown a no binder found exception") {
      result should be('right)
    }

  }

  describe("when binding a class that has an object enum that has one constructor, the first parameter is an int, but it doesn't have a public getter") {
    val values_map = Map(
      "anotherObjectBasedEnumWithAPrivateGetterFirstArgumentConstructor" -> List("2")
    )

    val result = try {
      Left(sut.bind[AClassWithAnObjectEnumWithAPrivateIntFirstArgOnConstructor](values_map))
    } catch {
      case ex: NoBinderFoundException => Right(ex)
    }

    it("should have thrown a no binder found exception") {
      result should be('right)
    }

  }

  describe("when binding a class that has an object enum that has a value outside the companion object of the enum class") {
    val values_map = Map(
      "anObjectEnumWithAnEnumValueOutsideCompanionObject" -> List("2")
    )

    val result = try {
      Left(sut.bind[AClassWithAnObjectEnumThatHasAValueOutsideItsCompanionObject](values_map))
    } catch {
      case ex: NoBinderFoundException => Right(ex)
    }

    it("should have thrown a no binder found exception") {
      result should be('right)
    }

  }

}