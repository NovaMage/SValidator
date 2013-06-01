package com.github.novamage.svalidator.binding

import testUtils.Observes


case class Person(name: String, age: Int)

class Job(position: String) {

}

class MapToObjectBinderSpecs extends Observes {

  val sut = new MapToObjectBinder


  describe("when testing stuff with type tags") {

    val result = sut.performBind[Person](Map("name" -> List("someValue"), "age" -> List("25")))

    it("should have bound the person value properly") {
      result should equal(BindingResult(List(), Some(Person("someValue", 25))))
    }
  }

}
