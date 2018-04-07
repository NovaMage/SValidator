package com.github.novamage.svalidator.html

trait HtmlValuePresenter {

  def getValueToPresentFor(propertyValue: Any): Option[String]

}
