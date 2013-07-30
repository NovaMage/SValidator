package com.github.novamage.svalidator.binding.binders.special

import com.github.novamage.svalidator.binding.binders.ITypedBinder
import com.github.novamage.svalidator.binding.{BindingPass, BindingResult}

class ListBinderWrapper(wrappedBinder: ITypedBinder[_]) extends ITypedBinder[List[Any]] {

  def bind(fieldName: String, valueMap: Map[String, Seq[String]]): BindingResult[List[Any]] = {
    val nonIndexedFieldName = valueMap.get(fieldName)
    val valueList = nonIndexedFieldName match {
      case Some(values) => values.toList map {
        value => wrappedBinder.bind(fieldName, Map(fieldName -> List(value)))
      } collect {
        case BindingPass(value) => value
      }
      case None => {
        val indexedKeys = valueMap.keys.filter(_.startsWith(fieldName + "["))
        val indexes = indexedKeys.map(_.replace(fieldName + "[", "").split("]").head.toInt).groupBy(x => x).keys.toList.sortBy(x => x)
        (for {
          i <- indexes
        } yield {
          wrappedBinder.bind(s"$fieldName[$i]", valueMap)
        }) collect {
          case BindingPass(value) => value
        }
      }
    }
    BindingPass(valueList.toList)
  }
}
