package com.github.novamage.svalidator.validation.simple

package object constructs {

  object not {
    def apply[A](functor: A => Boolean): (A => Boolean) = (parameter: A) => !functor(parameter)
  }

  def be = new BeConstruct

  def have = new HaveConstruct

  class BeConstruct {
    def prepareConstructWithRule[A, B](builder: SimpleValidationRuleBuilder[A, B]) = {
      new BeConstructWithRuleBuilder(builder)
    }
  }

  class HaveConstruct {

    def prepareConstructWithRule[A, B](builder: SimpleValidationRuleBuilder[A, B]) = {
      new HaveConstructWithRuleBuilder(builder)
    }
  }

  class BeConstructWithRuleBuilder[A, B](protected[constructs] val builder: SimpleValidationRuleBuilder[A, B]) {

  }

  class HaveConstructWithRuleBuilder[A, B](protected[constructs] val builder: SimpleValidationRuleBuilder[A, B]) {

  }

  implicit class BeConstructWithRuleBuilderForStringExtensions[A](construct: BeConstructWithRuleBuilder[A, String]) {

    def empty() = {
      construct.builder must { _.length == 0 }
    }
  }

  implicit class HaveConstructWithRuleBuilderForStringExtensions[A](construct: HaveConstructWithRuleBuilder[A, String]) {

    def maxLength(maxLength: Int) = {
      construct.builder must { _.length <= maxLength }
    }

  }

}
