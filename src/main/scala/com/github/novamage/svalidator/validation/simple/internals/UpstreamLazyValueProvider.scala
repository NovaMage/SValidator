package com.github.novamage.svalidator.validation.simple.internals

trait UpstreamLazyValueProvider[A] {


  def fetchValue: A


}
