package com.github.novamage.svalidator.binding.binders.special

import com.github.novamage.svalidator.binding.binders.{JsonTypedBinder, TypedBinder}
import com.github.novamage.svalidator.binding.{BindingFailure, BindingPass, BindingResult}
import io.circe.{ACursor, HCursor}

/** Binds options of a given type, provided that a binder for the type parameter is provided.
  */
class OptionBinder(wrappedBinder: TypedBinder[_])
  extends TypedBinder[Option[Any]] {

  def bind(fieldName: String, valueMap: Map[String, Seq[String]], bindingMetadata: Map[String, Any]): BindingResult[Option[Any]] = {
    wrappedBinder.bind(fieldName, valueMap, bindingMetadata) match {
      case BindingPass(value) => BindingPass(Option(value))
      case BindingFailure(errors, cause) => cause match {
        case Some(x) if x.isInstanceOf[NoSuchElementException] => BindingPass(None)
        case _ => BindingFailure(errors, cause)
      }
    }
  }

}

class JsonOptionBinder(wrappedBinder: JsonTypedBinder[_])
  extends JsonTypedBinder[Option[Any]] {

  override def bindJson(currentCursor: ACursor, fieldName: String, bindingMetadata: Map[String, Any]): BindingResult[Option[Any]] = {
    wrappedBinder.bindJson(currentCursor, fieldName, bindingMetadata) match {
      case BindingPass(value) => BindingPass(Option(value))
      case BindingFailure(errors, cause) => cause match {
        case Some(x) if x.isInstanceOf[NoSuchElementException] => BindingPass(None)
        case _ => BindingFailure(errors, cause)
      }
    }
  }

}