package integration.com.github.novamage.svalidator.binding

import com.github.novamage.svalidator.binding.binders.JsonTypedBinder
import com.github.novamage.svalidator.binding.binders.special.JsonToObjectBinder
import com.github.novamage.svalidator.binding.exceptions.{NoBinderFoundException, NoDirectBinderNorConstructorForBindingException}
import com.github.novamage.svalidator.binding.{BindingPass, BindingResult, TypeBinderRegistry}
import io.circe.{HCursor, Json}
import testUtils.Observes

import java.sql.Timestamp
import java.text.SimpleDateFormat

class JsonToObjectBinderSpecs extends Observes {

  private val sut: JsonToObjectBinder.type = JsonToObjectBinder

  TypeBinderRegistry.initializeBinders()

  private val fullJson = Json.obj(
    "aString" -> Json.fromString("someValue"),
    "anInt" -> Json.fromInt(5),
    "aLong" -> Json.fromLong(8),
    "aBoolean" -> Json.fromBoolean(true),
    "aTimestamp" -> Json.fromString("2008-09-05"),
    "optionalText" -> Json.fromString("someText"),
    "optionalInt" -> Json.fromInt(9),
    "intList" -> Json.fromValues(List(10, 20, 30).map(Json.fromInt)),
    "enumeratedValue" -> Json.fromInt(1),
    "aSimpleObjectBasedEnum" -> Json.fromInt(3),
    "aTypeBasedEnum" -> Json.fromInt(2)
  )
  val metadata = Map.empty[String, Any]

  private val formatter = new SimpleDateFormat("yyyy-MM-dd")

  private val fullClass = AComplexClass("someValue", 5, 8, aBoolean = true, new Timestamp(formatter.parse("2008-09-05").getTime), Some("someText"), Some(9), List(10, 20, 30), AnEnumType.anExampleEnumValue, ASimpleObjectBasedEnum.ThirdOption, ATypeBasedEnum.TypeBasedSecondOption)

  describe("when binding a complex class with many types in the constructor") {

    describe("and all values are provided") {

      val result = sut.bind[AComplexClass](fullJson, metadata)

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

      val result = sut.bind[AComplexClass](fullJson.hcursor.downField("aString").delete, metadata)

      it("should return a binding failure for the missing required field") {
        result.fieldErrors should have size 1
        result.fieldErrors.head.fieldName should equal("aString")
      }
    }

    describe("and the required int is missing") {

      val result = sut.bind[AComplexClass](fullJson.hcursor.downField("anInt").delete, metadata)

      it("should return a binding failure for the missing required field") {
        result.fieldErrors should have size 1
        result.fieldErrors.head.fieldName should equal("anInt")
      }
    }

    describe("and the required long is missing") {

      val result = sut.bind[AComplexClass](fullJson.hcursor.downField("aLong").delete, metadata)

      it("should return a binding failure for the missing required field") {
        result.fieldErrors should have size 1
        result.fieldErrors.head.fieldName should equal("aLong")
      }
    }

    describe("and the required boolean is missing") {

      val result = sut.bind[AComplexClass](fullJson.hcursor.downField("aBoolean").delete, metadata)

      it("should return a binding result with a class instantiated with all the values in the map bound to it " +
        "via constructor and use false for the missing boolean") {
        result should equal(BindingPass(fullClass.copy(aBoolean = false)))
      }
    }

    describe("and the required timestamp is missing") {

      val result = sut.bind[AComplexClass](fullJson.hcursor.downField("aTimestamp").delete, metadata)

      it("should return a binding failure for the missing required field") {
        result.fieldErrors should have size 1
        result.fieldErrors.head.fieldName should equal("aTimestamp")
      }
    }

    describe("and the required enum is missing") {

      val result = sut.bind[AComplexClass](fullJson.hcursor.downField("enumeratedValue").delete, metadata)

      it("should return a binding failure for the missing required field") {
        result.fieldErrors should have size 1
        result.fieldErrors.head.fieldName should equal("enumeratedValue")
      }
    }

    describe("and the required simple object based enum is missing") {

      val result = sut.bind[AComplexClass](fullJson.hcursor.downField("aSimpleObjectBasedEnum").delete, metadata)

      it("should return a binding failure for the missing required field") {
        result.fieldErrors should have size 1
        result.fieldErrors.head.fieldName should equal("aSimpleObjectBasedEnum")
      }
    }

    describe("and the required type based enum is missing") {

      val result = sut.bind[AComplexClass](fullJson.hcursor.downField("aTypeBasedEnum").delete, metadata)

      it("should return a binding failure for the missing required field") {
        result.fieldErrors should have size 1
        result.fieldErrors.head.fieldName should equal("aTypeBasedEnum")
      }
    }

    describe("and the optional text is missing") {

      val result = sut.bind[AComplexClass](fullJson.hcursor.downField("optionalText").delete, metadata)

      it("should return a binding result with a class instantiated with all the values in the map bound to it via constructor") {
        result should equal(BindingPass(fullClass.copy(optionalText = None)))
      }
    }

    describe("and the optional int is missing") {

      val result = sut.bind[AComplexClass](fullJson.hcursor.downField("optionalInt").delete, metadata)

      it("should return a binding result with a class instantiated with all the values in the map bound to it via constructor") {
        result should equal(BindingPass(fullClass.copy(optionalInt = None)))
      }
    }

    describe("and the list of integers is missing") {

      val result = sut.bind[AComplexClass](fullJson.hcursor.downField("intList").delete, metadata)

      it("should return a binding result with a class instantiated with all the values in the map bound to it via constructor") {
        result should equal(BindingPass(fullClass.copy(intList = List())))
      }
    }

  }

  describe("when binding a type with another custom type its constructor") {


    describe("and recursion into the type is allowed") {

      TypeBinderRegistry.allowRecursiveBindingForType[ClassUsedInRecursiveClass]()

      val json = Json.obj(
        "anotherString" -> Json.fromString("anotherValue"),
        "recursiveClass" -> Json.obj(
          "someInt" -> Json.fromInt(8),
          "someBoolean" -> Json.fromBoolean(true)
        )
      )

      val result = sut.bind[ASimpleRecursiveClass](json, metadata)

      it("should have bound properly the top class and the recursively bound class") {
        result should equal(BindingPass(ASimpleRecursiveClass("anotherValue", new ClassUsedInRecursiveClass(8, true))))
      }
    }

    describe("and recursion into the type is not allowed") {

      val json = Json.obj(
        "anotherString" -> Json.fromString("anotherValue"),
        "recursiveClass" -> Json.obj(
          "someInt" -> Json.fromInt(8),
          "someBoolean" -> Json.fromBoolean(true)
        )
      )

      it("should have bound properly the top class and the recursively bound class") {
        intercept[NoBinderFoundException] {
          sut.bind[ASimpleRecursiveClass](json, metadata)
        }
      }
    }

    describe("when binding a class that has a list of a custom type and it is indexed") {

      TypeBinderRegistry.allowRecursiveBindingForType[AnIndexedListValue]()

      val json =
        Json.obj(
          "anIndexedList" ->
            Json.fromValues(
              List(
                Json.obj(
                  "stringField" -> Json.fromString("alpha"),
                  "longField" -> Json.fromLong(3)
                ),
                Json.obj(
                  "stringField" -> Json.fromString("beta"),
                  "longField" -> Json.fromLong(9)
                ),
                Json.obj(
                  "stringField" -> Json.fromString("gamma"),
                  "longField" -> Json.fromLong(1)
                ),
                Json.obj(
                  "stringField" -> Json.fromString("lambda"),
                  "longField" -> Json.fromLong(39)
                )
              )
            )
        )

      val result = sut.bind[AClassWithAnIndexedList](json, metadata)

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
    val json = Json.obj(fieldName -> Json.fromInt(1))
    val cursorAtTargetField = json.hcursor.downField(fieldName)

    describe("and the type based enum has an alternate constructor") {

      val result = sut.bind[AnotherObjectBasedEnumWithAnAlternativeConstructor](cursorAtTargetField, metadata)

      it("should have successfully bound the enum using the primary constructor") {
        result should equal(BindingPass(AnotherObjectBasedEnumWithAnAlternativeConstructor.AnotherFirstOption))
      }

    }

    describe("and the type based enum has one constructor, but the first parameter isn't an int") {

      it("should have thrown a no binder found exception") {
        intercept[NoBinderFoundException] {
          sut.bind[AnotherObjectBasedEnumWithAnNonIntFirstArgumentConstructor](cursorAtTargetField, metadata)
        }
      }

    }

    describe("and the type based enum has one constructor, the first parameter is an int, but it doesn't have a public getter") {

      it("should have thrown a no binder found exception") {
        intercept[NoBinderFoundException] {
          sut.bind[AnotherObjectBasedEnumWithAPrivateGetterFirstArgumentConstructor](cursorAtTargetField, metadata)
        }
      }

    }

    describe("and the type based enum that has a value outside the companion object of the enum class") {

      it("should have thrown a no binder found exception") {
        intercept[NoBinderFoundException] {
          sut.bind[AnObjectEnumWithAnEnumValueOutsideCompanionObject](cursorAtTargetField, metadata)
        }
      }

    }

    describe("and the type based enum has a non module class (i.e. A non object type) descendant value inside the companion object of the enum class") {

      it("should have thrown a no binder found exception") {
        intercept[NoBinderFoundException] {
          sut.bind[AnObjectEnumWithAnEnumValueThatIsNotAModuleClass](cursorAtTargetField, metadata)
        }
      }

    }

    describe("and the type based enum is not sealed") {

      it("should have thrown a no binder found exception") {
        intercept[NoBinderFoundException] {
          sut.bind[ANonSealedObjectBasedEnum](cursorAtTargetField, metadata)
        }
      }

    }

    describe("and the type based enum is not abstract") {

      it("should have thrown a no binder found exception") {
        intercept[NoBinderFoundException] {
          sut.bind[ANonAbstractBaseClassObjectBasedEnum](cursorAtTargetField, metadata)
        }
      }

    }

    describe("and the type based enum but it has no companion object") {

      it("should have thrown a no binder found exception") {
        intercept[NoBinderFoundException] {
          sut.bind[AnObjectBasedEnumWithNoCompanionObject](cursorAtTargetField, metadata)
        }
      }

    }

    describe("and the type based enum has no descendants") {

      it("should have thrown a no binder found exception") {
        intercept[NoBinderFoundException] {
          sut.bind[AnObjectBasedEnumWithNoDescendants](cursorAtTargetField, metadata)
        }
      }

    }
  }


  describe("when binding a class that has a direct binder for it") {

    val metadata = mock[Map[String, Any]]

    val direct_binder = mock[JsonTypedBinder[AComplexClass]]
    val direct_bind_result = mock[BindingResult[AComplexClass]]
    val cursor = mock[HCursor]
    when(direct_binder.bindJson(cursor, None, metadata)) thenReturn direct_bind_result
    TypeBinderRegistry.registerJsonBinder(direct_binder)

    val result = sut.bind[AComplexClass](cursor, metadata)

    it("should have returned the result done by the direct binder") {
      result should be theSameInstanceAs direct_bind_result
    }

  }

  describe("when binding to a class that has multiple constructors") {

    val json = Json.obj(
      "someIntField" -> Json.fromInt(5),
      "aString" -> Json.fromString("aVeryRidiculouslyLongString")
    )

    val result = sut.bind[AClassWithMultipleConstructors](json, metadata)

    it("should have properly bound the class using its primary constructor") {
      result should equal(BindingPass(new AClassWithMultipleConstructors(5)))
    }

  }

  describe("when binding to a trait, and no direct binder exists for it") {

    val json = Json.obj(
      "someField" -> Json.fromString("someValue")
    )

    it("should have thrown an exception complaining about the lack of a constructor or direct binder for the trait") {
      intercept[NoDirectBinderNorConstructorForBindingException] {
        sut.bind[SomeTrait](json, metadata)
      }
    }

  }

  describe("when binding to a trait, and a direct binder exists for it") {

    val metadata = mock[Map[String, Any]]

    val direct_binder = mock[JsonTypedBinder[SomeTrait]]
    val direct_bind_result = mock[BindingResult[SomeTrait]]
    val cursor = mock[HCursor]

    when(direct_binder.bindJson(cursor, None, metadata)) thenReturn direct_bind_result
    TypeBinderRegistry.registerJsonBinder(direct_binder)

    val result = sut.bind[SomeTrait](cursor, metadata)

    it("should have returned the result done by the direct binder") {
      result should be theSameInstanceAs direct_bind_result
    }

  }

  describe("when binding a class with a defined concrete generic") {

    describe("and there's a single generic") {

      val json = Json.obj(
        "genericField" -> Json.obj(
          "intField" -> Json.fromInt(5)
        )
      )
      val metadata = mock[Map[String, Any]]
      TypeBinderRegistry.allowRecursiveBindingForType[AClassUsedInAGenericClass]()

      val expected_result = BindingPass(AClassWithAGeneric(AClassUsedInAGenericClass(5)))

      val result = sut.bind[AClassWithAGeneric[AClassUsedInAGenericClass]](json, bindingMetadata = metadata)

      it("should have returned a successful binding result with the expected generic value") {
        result should equal(expected_result)
      }

    }

    describe("and there are multiple generics") {

      val json = Json.obj(
        "genericField" -> Json.obj(
          "intField" -> Json.fromInt(5)
        ),
        "anotherGenericField" -> Json.obj(
          "stringField" -> Json.fromString("Hello")
        )
      )
      val metadata = mock[Map[String, Any]]
      TypeBinderRegistry.allowRecursiveBindingForType[AClassUsedInAGenericClass]()
      TypeBinderRegistry.allowRecursiveBindingForType[AnotherClassUsedInAGenericClass]()

      val expected_result = BindingPass(AClassWithMultipleGenerics(AClassUsedInAGenericClass(5), AnotherClassUsedInAGenericClass("Hello")))

      val result = sut.bind[AClassWithMultipleGenerics[AClassUsedInAGenericClass, AnotherClassUsedInAGenericClass]](json, bindingMetadata = metadata)

      it("should have returned a successful binding result with the expected generic value") {
        result should equal(expected_result)
      }

    }


  }

}