package integration.com.github.novamage.svalidator.binding

import com.github.novamage.svalidator.utils.TypeBasedEnumeration
import integration.com.github.novamage.svalidator.binding

import java.sql.Timestamp

object AnEnumType extends Enumeration {
  type AnEnumType = Value

  val anExampleEnumValue: binding.AnEnumType.Value = Value(1, "Just an example value")
  val anotherExampleEnumValue: binding.AnEnumType.Value = Value(2, "Just another example value")
}

sealed abstract class ASimpleObjectBasedEnum(val id: Int, someDescription: String, somethingElse: Any) {


  override def equals(other: Any): Boolean = other match {
    case someValue: ASimpleObjectBasedEnum => this.id == someValue.id
    case _ => false
  }

  override def hashCode(): Int = id
}

object ASimpleObjectBasedEnum {

  object FirstOption extends ASimpleObjectBasedEnum(1, "The first option", "anything1")

  object SecondOption extends ASimpleObjectBasedEnum(2, "The second option", BigDecimal("1000"))

  object ThirdOption extends ASimpleObjectBasedEnum(3, "The third option", true)

}

sealed abstract case class ATypeBasedEnum(id: Int, description: String, somethingElse: Any) extends ATypeBasedEnum.Value

object ATypeBasedEnum extends TypeBasedEnumeration[ATypeBasedEnum] {

  object TypeBasedFirstOption extends ATypeBasedEnum(1, "The first typed option", "anything4")

  object TypeBasedSecondOption extends ATypeBasedEnum(2, "The second typed option", BigDecimal("390"))

  object TypeBasedThirdOption extends ATypeBasedEnum(3, "The third typed option", true)

}

class AClassWithMultipleConstructors(val someIntField: Int) {

  def this(aString: String) = this(aString.length)

  override def equals(other: Any): Boolean = {
    other match {
      case value: AClassWithMultipleConstructors => value.someIntField == this.someIntField
      case _ => false
    }
  }

  override def hashCode(): Int = someIntField.hashCode()
}

trait SomeTrait {

  def someMethod: Int
}

sealed abstract class AnotherObjectBasedEnumWithAnAlternativeConstructor(val id: Int, someDescription: String) {

  def this(id: Int) = this(id, "")
}

object AnotherObjectBasedEnumWithAnAlternativeConstructor {

  object AnotherFirstOption extends AnotherObjectBasedEnumWithAnAlternativeConstructor(1, "The first option")

  object AnotherSecondOption extends AnotherObjectBasedEnumWithAnAlternativeConstructor(2, "The second option")

  object AnotherThirdOption extends AnotherObjectBasedEnumWithAnAlternativeConstructor(3, "The third option")

}


sealed abstract class AnotherObjectBasedEnumWithAnNonIntFirstArgumentConstructor(val id: Long, someDescription: String, somethingElse: Any) {
}

object AnotherObjectBasedEnumWithAnNonIntFirstArgumentConstructor {

  object YetAnotherFirstOption extends AnotherObjectBasedEnumWithAnNonIntFirstArgumentConstructor(1, "The first option", "anything1")

  object YetAnotherSecondOption extends AnotherObjectBasedEnumWithAnNonIntFirstArgumentConstructor(2, "The second option", BigDecimal("1000"))

  object YetAnotherThirdOption extends AnotherObjectBasedEnumWithAnNonIntFirstArgumentConstructor(3, "The third option", true)

}

sealed abstract class AnotherObjectBasedEnumWithAPrivateGetterFirstArgumentConstructor(private val id: Int, someDescription: String, somethingElse: Any) {
}

object AnotherObjectBasedEnumWithAPrivateGetterFirstArgumentConstructor {

  object PrivateFirstOption extends AnotherObjectBasedEnumWithAPrivateGetterFirstArgumentConstructor(1, "The first option", "anything1")

  object PrivateSecondOption extends AnotherObjectBasedEnumWithAPrivateGetterFirstArgumentConstructor(2, "The second option", BigDecimal("1000"))

  object PrivateThirdOption extends AnotherObjectBasedEnumWithAPrivateGetterFirstArgumentConstructor(3, "The third option", true)

}

sealed abstract class AnObjectEnumWithAnEnumValueOutsideCompanionObject(val id: Int, someDescription: String, somethingElse: Any) {
}


object AnObjectEnumWithAnEnumValueOutsideCompanionObject {

  object InsideOption1 extends AnObjectEnumWithAnEnumValueOutsideCompanionObject(2, "The second option", BigDecimal("1000"))

  object InsideOption2 extends AnObjectEnumWithAnEnumValueOutsideCompanionObject(3, "The third option", true)

}

object OutsideOption1 extends AnObjectEnumWithAnEnumValueOutsideCompanionObject(1, "The first option", "anything1")

sealed abstract class AnObjectEnumWithAnEnumValueThatIsNotAModuleClass(val id: Int, someDescription: String, somethingElse: Any)

object AnObjectEnumWithAnEnumValueThatIsNotAModuleClass {

  class ANonModuleOption extends AnObjectEnumWithAnEnumValueThatIsNotAModuleClass(4, "The fourth option", true)

  object AModuleOption1 extends AnObjectEnumWithAnEnumValueThatIsNotAModuleClass(2, "The second option", BigDecimal("1000"))

  object AModuleOption2 extends AnObjectEnumWithAnEnumValueThatIsNotAModuleClass(3, "The third option", true)

}

abstract class ANonSealedObjectBasedEnum(val id: Int, someDescription: String, somethingElse: Any)

object ANonSealedObjectBasedEnum {

  object Option1 extends ANonSealedObjectBasedEnum(1, "Option 1", "something1")

  object Option2 extends ANonSealedObjectBasedEnum(2, "Option 2", "something2")

}

sealed class ANonAbstractBaseClassObjectBasedEnum(val id: Int, someDescription: String, somethingElse: Any)

object ANonAbstractBaseClassObjectBasedEnum {

  object Option1 extends ANonAbstractBaseClassObjectBasedEnum(1, "Option 1", "something1")

  object Option2 extends ANonAbstractBaseClassObjectBasedEnum(2, "Option 2", "something2")

}

sealed abstract class AnObjectBasedEnumWithNoCompanionObject(val id: Int, someDescription: String, somethingElse: Any)

sealed abstract class AnObjectBasedEnumWithNoDescendants(val id: Int, someDescription: String, somethingElse: Any)

object AnObjectBasedEnumWithNoDescendants {

}


case class AComplexClass(aString: String, anInt: Int, aLong: Long, aBoolean: Boolean, aTimestamp: Timestamp, optionalText: Option[String], optionalInt: Option[Int], intList: List[Int],
                         enumeratedValue: AnEnumType.Value, aSimpleObjectBasedEnum: ASimpleObjectBasedEnum, aTypeBasedEnum: ATypeBasedEnum)

case class ASimpleRecursiveClass(anotherString: String, recursiveClass: ClassUsedInRecursiveClass)

class ClassUsedInRecursiveClass(val someInt: Int, val someBoolean: Boolean) {

  override def equals(any: Any): Boolean = {
    any match {
      case other: ClassUsedInRecursiveClass => this.someBoolean == other.someBoolean && this.someInt == other.someInt
      case _ => false
    }
  }

  override def hashCode: Int = this.someInt.hashCode()
}

case class AClassWithAnIndexedList(anIndexedList: List[AnIndexedListValue])

case class AnIndexedListValue(stringField: String, longField: Long)

case class AClassUsedInAGenericClass(intField: Int)

case class AnotherClassUsedInAGenericClass(stringField: String)

case class AClassWithAGeneric[A](genericField: A)

case class AClassWithMultipleGenerics[A, B](genericField: A, anotherGenericField: B)
