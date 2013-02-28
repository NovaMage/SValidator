package testUtils

import org.scalatest.path
import org.scalatest.mock.MockitoSugar
import org.scalatest.matchers.ShouldMatchers
import org.mockito.Mockito
import org.mockito.Mockito._

class Observes extends path.FunSpec with MockitoSugar with ShouldMatchers {

  def when[T](method_call: T) = {
    Mockito.when(method_call)
  }

  def verify[T](mock: T) = {
    Mockito.verify(mock)
  }

  def verifyNever[T](mock: T) = {
    Mockito.verify(mock, never())
  }

  def spy[T](spied: T) = Mockito.spy(spied)

  def reset[T](mock: T) = {
    Mockito.reset(mock)
    mock
  }

  implicit def TestingExtensions[T](aTestObject: T) = new TestExtensions[T](aTestObject)
}

class TestExtensions[T](aTestObject: T) {

  def wasToldTo(methodCall: T => Unit) {
    methodCall(verify(aTestObject, atLeastOnce()))
  }

  def wasNeverToldTo(methodCall: T => Unit) {
    methodCall(verify(aTestObject, never()))
  }

}




