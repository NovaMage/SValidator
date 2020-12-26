package com.github.novamage.svalidator.binding.binders.special

import com.github.novamage.svalidator.binding._
import com.github.novamage.svalidator.binding.binders.{JsonTypedBinder, TypedBinder}
import com.github.novamage.svalidator.binding.internals.{JsonReflectiveBinderInformation, ReflectiveBinderInformation}
import io.circe.ACursor

import scala.collection.mutable.ListBuffer

/** Direct binder created by storing the information that was gained through reflection during the first time a type was
  * bound reflectively.  Optimizes the binding process by avoiding multiple uses of reflection for binding a given type.
  */
class ReflectivelyBuiltDirectBinder[A](information: ReflectiveBinderInformation) extends TypedBinder[A] {

  override def bind(fieldName: String,
                    valueMap: Map[String, Seq[String]],
                    bindingMetadata: Map[String, Any]): BindingResult[A] = {

    val argList = ListBuffer[Any]()
    val errorList = ListBuffer[FieldError]()
    val causeList = ListBuffer[Throwable]()

    information.paramsInfo.foreach { info =>
      val fieldNameWithPrefix = if (fieldName.trim.isEmpty) info.parameterName else fieldName.trim + "." + info.parameterName
      info.binder.bind(fieldNameWithPrefix, valueMap, bindingMetadata) match {
        case BindingPass(value) => argList.append(value)
        case BindingFailure(errors, cause) =>
          errorList.appendAll(errors)
          cause.foreach(causeList.append(_))
      }
    }

    errorList.toList match {
      case Nil =>
        BindingPass(information.constructorMirror.apply(argList.toList: _*).asInstanceOf[A])
      case nonEmptyList =>
        if (argList.forall(x => x == None || x == false) && causeList.forall(_.isInstanceOf[NoSuchElementException])) {
          BindingFailure[A](nonEmptyList, Some(new NoSuchElementException()))
        } else {
          BindingFailure[A](nonEmptyList, None)
        }
    }


  }

}


class JsonReflectivelyBuiltDirectBinder[A](information: JsonReflectiveBinderInformation) extends JsonTypedBinder[A] {

  override def bindJson(currentCursor: ACursor, fieldName: Option[String], bindingMetadata: Map[String, Any]): BindingResult[A] = {


    val argList = ListBuffer[Any]()
    val errorList = ListBuffer[FieldError]()
    val causeList = ListBuffer[Throwable]()

    information.paramsInfo.foreach { info =>
      val nextFieldName = fieldName match {
        case Some(field) => s"$field.${info.parameterName}"
        case None => info.parameterName
      }
      info.binder.bindJson(currentCursor.downField(info.parameterName), Some(nextFieldName), bindingMetadata) match {
        case BindingPass(value) => argList.append(value)
        case BindingFailure(errors, cause) =>
          errorList.appendAll(errors)
          cause.foreach(causeList.append(_))
      }
    }

    errorList.toList match {
      case Nil =>
        BindingPass(information.constructorMirror.apply(argList.toList: _*).asInstanceOf[A])
      case nonEmptyList =>
        if (argList.forall(x => x == None || x == false) && causeList.forall(_.isInstanceOf[NoSuchElementException])) {
          BindingFailure[A](nonEmptyList, Some(new NoSuchElementException()))
        } else {
          BindingFailure[A](nonEmptyList, None)
        }
    }


  }

}
