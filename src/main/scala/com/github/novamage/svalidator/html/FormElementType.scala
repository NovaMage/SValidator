package com.github.novamage.svalidator.html

import com.github.novamage.svalidator.utils.TypeBasedEnumeration

/** A type based enumeration with values for each possible form element whose attributes can be intercepted by the
  * [[com.github.novamage.svalidator.html.HtmlAttributeDecorator HtmlAttributeDecorator]]
  *
  * @param id An integer identifier for the enumerated value
  * @param htmlElementName The html tag used for the type of element
  */
sealed abstract case class FormElementType(id: Int, htmlElementName: String) extends FormElementType.Value {

  override def description: String = {
    getClass.getSimpleName.replace("$", "")
  }

}

/** A type based enumeration with values for each possible form element whose attributes can be intercepted by the
  * [[com.github.novamage.svalidator.html.HtmlAttributeDecorator HtmlAttributeDecorator]]
  */
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
