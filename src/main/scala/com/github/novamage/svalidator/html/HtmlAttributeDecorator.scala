package com.github.novamage.svalidator.html

trait HtmlAttributeDecorator {

  def decorateAttributes(elementType: FormElementType,
                         attributes: Map[String, Any]): Map[String, Any]

}
