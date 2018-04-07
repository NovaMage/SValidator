package com.github.novamage.svalidator.html

trait HtmlAttributeDecorator {

  def decorateAttributes(elementType: FormElementType,
                         attributes: Map[String, Any],
                         errors: List[String]): Map[String, Any]

}
