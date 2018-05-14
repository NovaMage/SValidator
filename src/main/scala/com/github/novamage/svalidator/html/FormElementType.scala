package com.github.novamage.svalidator.html

import com.github.novamage.svalidator.utils.TypeBasedEnumeration

sealed abstract case class FormElementType(id: Int, htmlElementName: String) extends FormElementType.Value {

  override def description: String = {
    getClass.getSimpleName.replace("$", "")
  }

}

object FormElementType extends TypeBasedEnumeration[FormElementType] {

  object Form extends FormElementType(1, "form")

  object Hidden extends FormElementType(2, "input")

  object TextBox extends FormElementType(3, "input")

  object Password extends FormElementType(4, "input")

  object CheckBox extends FormElementType(5, "input")

  object Select extends FormElementType(6, "select")

  object SelectOption extends FormElementType(7, "option")

  object RadioGroupOption extends FormElementType(8, "input")

  object CheckBoxGroupOption extends FormElementType(9, "input")

  object TextArea extends FormElementType(10, "textarea")

  object Button extends FormElementType(11, "button")

  object Submit extends FormElementType(12, "input")

}
