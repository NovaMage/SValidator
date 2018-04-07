package com.github.novamage.svalidator.html

trait HtmlResultConverter[A] {

  def apply(html: String): A

}
