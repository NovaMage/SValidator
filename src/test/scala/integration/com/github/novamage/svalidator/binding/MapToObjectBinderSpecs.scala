package integration.com.github.novamage.svalidator.binding

import com.github.novamage.svalidator.binding.binders.TypedBinder
import com.github.novamage.svalidator.binding.binders.special.MapToObjectBinder
import com.github.novamage.svalidator.binding.exceptions.{NoBinderFoundException, NoDirectBinderNorConstructorForBindingException}
import com.github.novamage.svalidator.binding.{BindingPass, BindingResult, TypeBinderRegistry}
import testUtils.Observes

import java.sql.Timestamp
import java.text.SimpleDateFormat

class MapToObjectBinderSpecs extends Observes {

  val sut: MapToObjectBinder.type = MapToObjectBinder

  TypeBinderRegistry.initializeBinders()

  private val fullMap = Map(
    "aString" -> List("someValue"),
    "anInt" -> List("5"),
    "aLong" -> List("8"),
    "aBoolean" -> List("true"),
    "aTimestamp" -> List("2008-09-05"),
    "optionalText" -> List("someText"),
    "optionalInt" -> List("9"),
    "intList" -> List("10", "20", "30"),
    "enumeratedValue" -> List("1"),
    "aSimpleObjectBasedEnum" -> List("3"),
    "aTypeBasedEnum" -> List("2")
  )

  private val formatter = new SimpleDateFormat("yyyy-MM-dd")

  private val fullClass = AComplexClass("someValue", 5, 8, aBoolean = true, new Timestamp(formatter.parse("2008-09-05").getTime), Some("someText"), Some(9), List(10, 20, 30), AnEnumType.anExampleEnumValue, ASimpleObjectBasedEnum.ThirdOption, ATypeBasedEnum.TypeBasedSecondOption)

  describe("when binding a complex class with many types in the constructor") {

    describe("and all values are provided") {

      val result = sut.bind[AComplexClass](fullMap)

      it("should return a binding result with a class instantiated with all the values in the map bound to it via constructor") {
        //Testing values individually for granularity purposes on test failure
        result.value.get.aBoolean should equal(fullClass.aBoolean)
        result.value.get.aLong should equal(fullClass.aLong)
        result.value.get.anInt should equal(fullClass.anInt)
        result.value.get.aSimpleObjectBasedEnum should equal(fullClass.aSimpleObjectBasedEnum)
        result.value.get.aString should equal(fullClass.aString)
        result.value.get.aTimestamp should equal(fullClass.aTimestamp)
        result.value.get.enumeratedValue should equal(fullClass.enumeratedValue)
        result.value.get.aTypeBasedEnum should equal(fullClass.aTypeBasedEnum)
        result.value.get.intList should equal(fullClass.intList)
        result.value.get.optionalInt should equal(fullClass.optionalInt)
        result.value.get.optionalText should equal(fullClass.optionalText)
      }
    }

    describe("and the required string is missing") {

      val result = sut.bind[AComplexClass](fullMap - "aString")

      it("should return a binding failure for the missing required field") {
        result.fieldErrors should have size 1
        result.fieldErrors.head.fieldName should equal("aString")
      }
    }

    describe("and the required int is missing") {

      val result = sut.bind[AComplexClass](fullMap - "anInt")

      it("should return a binding failure for the missing required field") {
        result.fieldErrors should have size 1
        result.fieldErrors.head.fieldName should equal("anInt")
      }
    }

    describe("and the required long is missing") {

      val result = sut.bind[AComplexClass](fullMap - "aLong")

      it("should return a binding failure for the missing required field") {
        result.fieldErrors should have size 1
        result.fieldErrors.head.fieldName should equal("aLong")
      }
    }

    describe("and the required boolean is missing") {

      val result = sut.bind[AComplexClass](fullMap - "aBoolean")

      it("should return a binding result with a class instantiated with all the values in the map bound to it " +
        "via constructor and use false for the missing boolean") {
        result should equal(BindingPass(fullClass.copy(aBoolean = false)))
      }
    }

    describe("and the required timestamp is missing") {

      val result = sut.bind[AComplexClass](fullMap - "aTimestamp")

      it("should return a binding failure for the missing required field") {
        result.fieldErrors should have size 1
        result.fieldErrors.head.fieldName should equal("aTimestamp")
      }
    }

    describe("and the required enum is missing") {

      val result = sut.bind[AComplexClass](fullMap - "enumeratedValue")

      it("should return a binding failure for the missing required field") {
        result.fieldErrors should have size 1
        result.fieldErrors.head.fieldName should equal("enumeratedValue")
      }
    }

    describe("and the required simple object based enum is missing") {

      val result = sut.bind[AComplexClass](fullMap - "aSimpleObjectBasedEnum")

      it("should return a binding failure for the missing required field") {
        result.fieldErrors should have size 1
        result.fieldErrors.head.fieldName should equal("aSimpleObjectBasedEnum")
      }
    }

    describe("and the required type based enum is missing") {

      val result = sut.bind[AComplexClass](fullMap - "aTypeBasedEnum")

      it("should return a binding failure for the missing required field") {
        result.fieldErrors should have size 1
        result.fieldErrors.head.fieldName should equal("aTypeBasedEnum")
      }
    }

    describe("and the optional text is missing") {

      val result = sut.bind[AComplexClass](fullMap - "optionalText")

      it("should return a binding result with a class instantiated with all the values in the map bound to it via constructor") {
        result should equal(BindingPass(fullClass.copy(optionalText = None)))
      }
    }

    describe("and the optional int is missing") {

      val result = sut.bind[AComplexClass](fullMap - "optionalInt")

      it("should return a binding result with a class instantiated with all the values in the map bound to it via constructor") {
        result should equal(BindingPass(fullClass.copy(optionalInt = None)))
      }
    }

    describe("and the list of integers is missing") {

      val result = sut.bind[AComplexClass](fullMap - "intList")

      it("should return a binding result with a class instantiated with all the values in the map bound to it via constructor") {
        result should equal(BindingPass(fullClass.copy(intList = List())))
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
        result should equal(BindingPass(ASimpleRecursiveClass("anotherValue", new ClassUsedInRecursiveClass(8, true))))
      }
    }

    describe("and recursion into the type is not allowed") {

      val value_map = Map(
        "anotherString" -> List("anotherValue"),
        "recursiveClass.someInt" -> List("8"),
        "recursiveClass.someBoolean" -> List("true")
      )


      it("should have bound properly the top class and the recursively bound class") {
        intercept[NoBinderFoundException] {
          sut.bind[ASimpleRecursiveClass](value_map)
        }
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

  describe("when binding a type based enum") {
    val fieldName = "someFieldName"
    val values_map = Map(fieldName -> List("1"))

    describe("and the type based enum has an alternate constructor") {

      val result = sut.bind[AnotherObjectBasedEnumWithAnAlternativeConstructor](values_map, Some(fieldName))

      it("should have successfully bound the enum using the primary constructor") {
        result should equal(BindingPass(AnotherObjectBasedEnumWithAnAlternativeConstructor.AnotherFirstOption))
      }

    }

    describe("and the type based enum has one constructor, but the first parameter isn't an int") {

      it("should have thrown a no binder found exception") {
        intercept[NoBinderFoundException] {
          sut.bind[AnotherObjectBasedEnumWithAnNonIntFirstArgumentConstructor](values_map, Some(fieldName))
        }
      }

    }

    describe("and the type based enum has one constructor, the first parameter is an int, but it doesn't have a public getter") {

      it("should have thrown a no binder found exception") {
        intercept[NoBinderFoundException] {
          sut.bind[AnotherObjectBasedEnumWithAPrivateGetterFirstArgumentConstructor](values_map, Some(fieldName))
        }
      }

    }

    describe("and the type based enum that has a value outside the companion object of the enum class") {

      it("should have thrown a no binder found exception") {
        intercept[NoBinderFoundException] {
          sut.bind[AnObjectEnumWithAnEnumValueOutsideCompanionObject](values_map, Some(fieldName))
        }
      }

    }

    describe("and the type based enum has a non module class (i.e. A non object type) descendant value inside the companion object of the enum class") {

      it("should have thrown a no binder found exception") {
        intercept[NoBinderFoundException] {
          sut.bind[AnObjectEnumWithAnEnumValueThatIsNotAModuleClass](values_map, Some(fieldName))
        }
      }

    }

    describe("and the type based enum is not sealed") {

      it("should have thrown a no binder found exception") {
        intercept[NoBinderFoundException] {
          sut.bind[ANonSealedObjectBasedEnum](values_map, Some(fieldName))
        }
      }

    }

    describe("and the type based enum is not abstract") {

      it("should have thrown a no binder found exception") {
        intercept[NoBinderFoundException] {
          sut.bind[ANonAbstractBaseClassObjectBasedEnum](values_map, Some(fieldName))
        }
      }

    }

    describe("and the type based enum but it has no companion object") {

      it("should have thrown a no binder found exception") {
        intercept[NoBinderFoundException] {
          sut.bind[AnObjectBasedEnumWithNoCompanionObject](values_map, Some(fieldName))
        }
      }

    }

    describe("and the type based enum has no descendants") {

      it("should have thrown a no binder found exception") {
        intercept[NoBinderFoundException] {
          sut.bind[AnObjectBasedEnumWithNoDescendants](values_map, Some(fieldName))
        }
      }

    }
  }


  describe("when binding a class that has a direct binder for it") {

    val values_map = Map(
      "someField" -> List("someValue")
    )
    val metadata = mock[Map[String, Any]]

    val direct_binder = mock[TypedBinder[AComplexClass]]
    val direct_bind_result = mock[BindingResult[AComplexClass]]
    when(direct_binder.bind("", values_map, metadata)) thenReturn direct_bind_result
    TypeBinderRegistry.registerBinder(direct_binder)

    val result = sut.bind[AComplexClass](values_map, bindingMetadata = metadata)

    it("should have returned the result done by the direct binder") {
      result should be theSameInstanceAs direct_bind_result
    }

  }

  describe("when binding to a class that has multiple constructors") {

    val values_map = Map(
      "someIntField" -> List("5"),
      "aString" -> List("aVeryRidiculouslyLongString")
    )

    val result = sut.bind[AClassWithMultipleConstructors](values_map)

    it("should have properly bound the class using its primary constructor") {
      result should equal(BindingPass(new AClassWithMultipleConstructors(5)))
    }

  }

  describe("when binding to a trait, and no direct binder exists for it") {

    val values_map = Map(
      "someField" -> List("someValue")
    )

    it("should have thrown an exception complaining about the lack of a constructor or direct binder for the trait") {
      intercept[NoDirectBinderNorConstructorForBindingException] {
        sut.bind[SomeTrait](values_map)
      }
    }

  }

  describe("when binding to a trait, and a direct binder exists for it") {

    val values_map = Map(
      "someField" -> List("someValue")
    )
    val metadata = mock[Map[String, Any]]

    val direct_binder = mock[TypedBinder[SomeTrait]]
    val direct_bind_result = mock[BindingResult[SomeTrait]]
    when(direct_binder.bind("", values_map, metadata)) thenReturn direct_bind_result
    TypeBinderRegistry.registerBinder(direct_binder)

    val result = sut.bind[SomeTrait](values_map, bindingMetadata = metadata)

    it("should have returned the result done by the direct binder") {
      result should be theSameInstanceAs direct_bind_result
    }

  }

  describe("when binding a class with a defined concrete generic") {

    describe("and there's a single generic") {

      val values_map = Map(
        "genericField.intField" -> List("5")
      )
      val metadata = mock[Map[String, Any]]
      TypeBinderRegistry.allowRecursiveBindingForType[AClassUsedInAGenericClass]()

      val expected_result = BindingPass(AClassWithAGeneric(AClassUsedInAGenericClass(5)))

      val result = sut.bind[AClassWithAGeneric[AClassUsedInAGenericClass]](values_map, bindingMetadata = metadata)

      it("should have returned a successful binding result with the expected generic value") {
        result should equal(expected_result)
      }

    }

    describe("and there are multiple generics") {

      val values_map = Map(
        "genericField.intField" -> List("5"),
        "anotherGenericField.stringField" -> List("Hello")
      )
      val metadata = mock[Map[String, Any]]
      TypeBinderRegistry.allowRecursiveBindingForType[AClassUsedInAGenericClass]()
      TypeBinderRegistry.allowRecursiveBindingForType[AnotherClassUsedInAGenericClass]()

      val expected_result = BindingPass(AClassWithMultipleGenerics(AClassUsedInAGenericClass(5), AnotherClassUsedInAGenericClass("Hello")))

      val result = sut.bind[AClassWithMultipleGenerics[AClassUsedInAGenericClass, AnotherClassUsedInAGenericClass]](values_map, bindingMetadata = metadata)

      it("should have returned a successful binding result with the expected generic value") {
        result should equal(expected_result)
      }

    }


  }

}