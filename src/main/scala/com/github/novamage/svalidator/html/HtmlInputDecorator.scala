package com.github.novamage.svalidator.html

trait HtmlInputDecorator {

  def decorateSelect(selectHtml: String,
                     name: String,
                     errors: List[String]): String = ???

  def decorateSelectOption(optionHtml: String,
                           value: Any,
                           text: Any,
                           index: Int): String = ???


  def decorateTextBox(inputHtml: String,
                      fieldName: String,
                      errors: List[String]): String = ???

  def decorateCheckBox(inputHtml: String,
                       name: String,
                       errors: List[String]): String = ???

}
