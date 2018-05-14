package com.github.novamage.svalidator.utils

import scala.reflect.runtime.{universe => ru}

abstract class TypeBasedEnumeration[A: ru.TypeTag] {

  trait Value {
    self: A =>

    def id: Int

    def description: String

    override def toString: String = description

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

  def valuesMap: Map[Int, A] = _valuesMap

  def values: List[A] = _values

  def apply(id: Int): A = _valuesMap(id)

}

