package com.github.novamage.svalidator.html

/** Determines how values should be formatted when presenting them in html inputs
  *
  */
trait HtmlValuePresenter {

  /** Returns a string to display when the given property value is to be displayed in an html field
    *
    * @param propertyValue Property value extracted from a bound or validated instance
    * @return [[scala.Some Some]](valueToDisplay) if it's possible to display a value, [[scala.None None]] if the input
    *        should be considered empty and no value displayed.
    */
  def getValueToPresentFor(propertyValue: Any): Option[String]

}
