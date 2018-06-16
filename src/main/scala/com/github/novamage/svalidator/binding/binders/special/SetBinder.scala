package com.github.novamage.svalidator.binding.binders.special

import com.github.novamage.svalidator.binding.binders.TypedBinder
import com.github.novamage.svalidator.binding.{BindingFailure, BindingPass, BindingResult, FieldError}

import scala.collection.mutable.ListBuffer

/** Binds lists of a given type, provided that a binder for the type parameter is provided.
  */
class SetBinder(wrappedBinder: TypedBinder[_]) extends TypedBinder[Set[Any]] {

  def bind(fieldName: String, valueMap: Map[String, Seq[String]]): BindingResult[Set[Any]] = {
    val fieldErrors = new ListBuffer[FieldError]
    val validValues = new ListBuffer[Any]
    val nonIndexedFieldName = valueMap.get(fieldName)
    nonIndexedFieldName match {
      case Some(values) =>
        values.toList map {
          value => wrappedBinder.bind(fieldName, Map(fieldName -> List(value)))
        } foreach {
          case x: BindingFailure[_] => fieldErrors.appendAll(x.fieldErrors.map(_.copy(fieldName = fieldName)))
          case BindingPass(validValue) => validValues.append(validValue)
        }
      case None =>
        val indexedKeys = valueMap.keys.filter(_.startsWith(fieldName + "["))
        val indexes = indexedKeys.map(_.replace(fieldName + "[", "").split("]").head.toInt).groupBy(identity).keys.toList.sortBy(identity)
        (for {
          i <- indexes
        } yield {
          wrappedBinder.bind(s"$fieldName[$i]", valueMap)
        }) foreach {
          case x: BindingFailure[_] => fieldErrors.appendAll(x.fieldErrors)
          case BindingPass(validValue) => validValues.append(validValue)
        }
    }
    if (fieldErrors.isEmpty)
      BindingPass(validValues.toSet)
    else
      BindingFailure(fieldErrors.toList, None)
  }
}

