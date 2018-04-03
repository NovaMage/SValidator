package com.github.novamage.svalidator.html

trait HtmlValueExtractor {

  def extractValueFromProperty(propertyValue: Any): Option[String]

}
