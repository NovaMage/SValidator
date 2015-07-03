package testUtils

import org.mockito.Mockito
import org.mockito.Mockito._
import org.scalatest
import org.scalatest.mock.MockitoSugar
import org.scalatest.path

class Observes extends path.FunSpec with MockitoSugar with scalatest.Matchers with FunctionStubbingHelpers {

  def when[T](method_call: T) = {
    Mockito.when(method_call)
  }

  def spy[T](spied: T) = Mockito.spy(spied)

  def reset[T](mock: T) = {
    Mockito.reset(mock)
    mock
  }

  implicit class ObservedTestExtensions[A](aMockObject: A) {

    def wasToldTo(methodCall: A => Unit) {
      methodCall(verify(aMockObject, atLeastOnce()))
    }

    def wasNeverToldTo(methodCall: A => Unit) {
      methodCall(verify(aMockObject, never()))
    }
  }

}

