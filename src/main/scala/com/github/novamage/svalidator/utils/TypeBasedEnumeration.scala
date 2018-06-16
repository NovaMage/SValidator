package com.github.novamage.svalidator.utils

import scala.reflect.runtime.{universe => ru}

/** Convenience class to create type safe case object enumerations
  *
  * @tparam A Base type of the enumerated value
  */
abstract class TypeBasedEnumeration[A: ru.TypeTag] {

  trait Value {
    self: A =>

    /** Returns an integer identifier for this enumerated value
      */
    def id: Int

    /** Returns a string description for this enumerated value
      */
    def description: String

    /** Returns the description field for this enumerated value
      */
    override def toString: String = description

    /** Returns the hashcode of the id of this enumerated value
      */
    override def hashCode: Int = id.hashCode()

    override def equals(obj: Any): Boolean = {
      obj match {
        case someValue: Value => this.id == someValue.id
        case _ => false
      }
    }
  }

  private lazy val _values: List[A with Value] = {
    val tag = ru.typeTag[A]
    val mirror = tag.mirror
    val runtimeType = tag.tpe
    val classSymbol = runtimeType.typeSymbol.asClass
    val enclosingModule = classSymbol.companion.asModule
    val reflectedEnclosingModule = mirror.reflectModule(enclosingModule)
    val enclosingObjectInstance = reflectedEnclosingModule.instance
    val reflectedInstance = mirror.reflect(enclosingObjectInstance)
    val instanceSymbol = reflectedInstance.symbol
    (classSymbol.knownDirectSubclasses map {
      descendantType =>
        val innerObjectModule = instanceSymbol.typeSignature.member(ru.TermName(descendantType.name.decodedName.toString)).asModule
        val moduleClass = innerObjectModule.moduleClass
        val reflectedCompanion = mirror.reflectClass(moduleClass.asClass)
        val companionConstructorTerm = moduleClass.asType.typeSignature.decl(ru.termNames.CONSTRUCTOR).asMethod
        val objectInstance = reflectedCompanion.reflectConstructor(companionConstructorTerm).apply()
        objectInstance.asInstanceOf[A with Value]
    }).toList
  }

  private lazy val _valuesMap: Map[Int, A] = _values.map(x => x.id -> x.asInstanceOf[A]).toMap

  /** Returns a map of the id of each enumerated value to its corresponding value
    */
  def valuesMap: Map[Int, A] = _valuesMap

  /** Returns a list of all enumerated values of this type
    */
  def values: List[A] = _values

  /** Returns the enumerated value whose id matches the argument int.
    */
  def apply(id: Int): A = _valuesMap(id)

}

