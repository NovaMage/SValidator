package com.github.novamage.svalidator.binding.binders.special

import com.github.novamage.svalidator.binding.binders.{JsonTypedBinder, TypedBinder}
import com.github.novamage.svalidator.binding._
import io.circe.ACursor

import scala.collection.mutable.ListBuffer

/** Binds lists of a given type, provided that a binder for the type parameter is provided.
  */
class ListBinder(wrappedBinder: TypedBinder[_]) extends TypedBinder[List[Any]] {

  def bind(fieldName: String, valueMap: Map[String, Seq[String]], bindingMetadata: Map[String, Any]): BindingResult[List[Any]] = {
    val fieldErrors = new ListBuffer[FieldError]
    val validValues = new ListBuffer[Any]
    val nonIndexedFieldName = valueMap.get(fieldName)
    nonIndexedFieldName match {
      case Some(values) =>
        values.toList map {
          value => wrappedBinder.bind(fieldName, Map(fieldName -> List(value)), bindingMetadata)
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
          wrappedBinder.bind(s"$fieldName[$i]", valueMap, bindingMetadata)
        }) foreach {
          case x: BindingFailure[_] => fieldErrors.appendAll(x.fieldErrors)
          case BindingPass(validValue) => validValues.append(validValue)
        }
    }
    if (fieldErrors.isEmpty)
      BindingPass(validValues.toList)
    else
      BindingFailure(fieldErrors.toList, None)
  }

}

class JsonListBinder(wrappedBinder: JsonTypedBinder[_], config: BindingConfig) extends JsonTypedBinder[List[Any]] {

  override def bindJson(currentCursor: ACursor, fieldName: String, bindingMetadata: Map[String, Any]): BindingResult[List[Any]] = {
    val firstIndexCursor = currentCursor.downArray
    if (firstIndexCursor.succeeded) {
      val values = currentCursor.values.getOrElse(Nil)
      val fieldErrors = new ListBuffer[FieldError]
      val validValues = new ListBuffer[Any]
      values.zipWithIndex.foreach { case (json, index) =>
        wrappedBinder.bindJson(json.hcursor, s"$fieldName[$index]", bindingMetadata) match {
          case BindingPass(boundValue) => validValues.append(boundValue)
          case BindingFailure(errors, _) => fieldErrors.appendAll(errors)
        }
      }
      if (fieldErrors.isEmpty)
        BindingPass(validValues.toList)
      else
        BindingFailure(fieldErrors.toList, None)
    } else {
      val value = currentCursor.focus.map(_.toString())
      value.map(_.trim).filter(_.nonEmpty) match {
        case Some(invalidValue) => BindingFailure(fieldName, config.languageConfig.invalidSequenceMessage(fieldName, invalidValue), None)
        case None => BindingPass(Nil)
      }
    }
  }
}
