package com.github.novamage.svalidator.html

/** Intercepts attribute maps for all elements to allow decoration for the purposes of styling
  * @see [[com.github.novamage.svalidator.html.FormElementType FormElementType]]
  */
trait HtmlAttributeDecorator {

  /** Intercepts attribute maps for all elements to allow decoration for the purposes of styling
    *
    * @param elementType The type of html element(s) being intercepted
    * @param attributes The current attribute map for the element(s) being intercepted
    * @param errors The list of error messages associated with the field of the element being intercepted
    * @return The updated map of attributes after decoration
    */
  def decorateAttributes(elementType: FormElementType,
                         attributes: Map[String, Any],
                         errors: List[String]): Map[String, Any]

}
