package com.github.novamage.svalidator.validation.simple

trait UpstreamLazyValueProvider[A] {


  def fetchValue: A


}
