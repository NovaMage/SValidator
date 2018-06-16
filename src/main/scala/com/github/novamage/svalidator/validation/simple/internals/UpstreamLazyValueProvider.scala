package com.github.novamage.svalidator.validation.simple.internals

trait UpstreamLazyValueProvider[A] {


  protected[simple] def fetchValue: A


}
