package com.github.novamage.svalidator.binding.binders.special

import com.github.novamage.svalidator.binding._
import com.github.novamage.svalidator.binding.binders.TypedBinder
import com.github.novamage.svalidator.validation.Localizer

import scala.collection.mutable.ListBuffer

class ReflectivelyBuiltDirectBinder[A](information: ReflectiveBinderInformation) extends TypedBinder[A] {

  override def bind(fieldName: String,
                    valueMap: Map[String, Seq[String]],
                    localizer: Localizer): BindingResult[A] = {

    val argList = ListBuffer[Any]()
    val errorList = ListBuffer[FieldError]()
    val causeList = ListBuffer[Throwable]()

    information.paramsInfo.foreach { info =>
      val fieldNameWithPrefix = if (fieldName.trim.isEmpty) info.parameterName else fieldName.trim + "." + info.parameterName
      info.binder.bind(fieldNameWithPrefix, valueMap, localizer) match {
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
