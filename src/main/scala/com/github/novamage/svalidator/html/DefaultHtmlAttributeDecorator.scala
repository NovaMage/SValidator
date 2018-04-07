package com.github.novamage.svalidator.html

object DefaultHtmlAttributeDecorator extends HtmlAttributeDecorator {

  override def decorateAttributes(elementType: FormElementType,
                                  attributes: Map[String, Any],
                                  errors: List[String]): Map[String, Any] = {
    if (errors.nonEmpty) {
      val newClassValue = (attributes.getOrElse("class", "").toString + " error-field").trim
      attributes.updated("class", newClassValue)
    } else {
      attributes
    }
  }

}
