package testUtils

import org.scalatest.exceptions.TestFailedException

trait FunctionStubbingHelpers {

  def stubFunction[A, R](expectedParameter: A, resultOnSuccess: R) = {
    val stub: (A => R) = (arg) => if (arg == expectedParameter) resultOnSuccess else throw new TestFailedException(s"Function was called with wrong parameter: expected $expectedParameter, got $arg instead", 1)
    stub
  }

  def stubUnCallableFunction[A, R] = {
    val stub: (A => R) = (arg) => throw new TestFailedException("Function was not expected to be called, but was called anyway", 1)
    stub
  }

  def stubFunction[A, B, R](firstExpectedParameter: A, secondExpectedParameter: B, resultOnSuccess: R) = {
    val stub: (A, B) => R = (arg1, arg2) => if (arg1 == firstExpectedParameter && arg2 == secondExpectedParameter) resultOnSuccess
    else throw new TestFailedException("Function was called with wrong parameters, " +
      s"expected ($firstExpectedParameter,$secondExpectedParameter), got ($arg1,$arg2) instead.", 1)
    stub
  }

  def stubUnCallableFunction[A, B, R] = {
    val stub: (A, B) => R = (arg1, arg2) => throw new TestFailedException("Function was not expected to be called, but was called anyway", 1)
    stub
  }
}
