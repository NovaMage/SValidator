package com.github.novamage.svalidator.html

object DefaultHtmlAttributeDecorator extends HtmlAttributeDecorator {

  override def decorateAttributes(elementType: FormElementType,
                                  attributes: Map[String, Any]): Map[String, Any] = attributes

}
