package com.github.novamage.svalidator.binding.binders.special

import com.github.novamage.svalidator.binding.binders.ITypedBinder
import com.github.novamage.svalidator.binding.{BindingPass, BindingResult}

class ListBinderWrapper(wrappedBinder: ITypedBinder[_]) extends ITypedBinder[List[Any]] {

  def bind(fieldName: String, valueMap: Map[String, Seq[String]]): BindingResult[List[Any]] = {
    val nonIndexedFieldName = valueMap.get(fieldName)
    val indexedKeys = valueMap.keys.filter(_.startsWith(fieldName + "["))
    val valueList = nonIndexedFieldName match {
      case Some(values) => values.toList map {
        value => wrappedBinder.bind(fieldName, Map(fieldName -> List(value)))
      } collect {
        case BindingPass(value) => value
      }
      case None => {
        (for {
          i <- 0 until indexedKeys.size
        } yield {
          wrappedBinder.bind(fieldName + s"[$i]", valueMap)
        }) collect {
          case BindingPass(value) => value
        }
      }
    }
    BindingPass(valueList.toList)
  }
}
