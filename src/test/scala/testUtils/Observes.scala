package testUtils

import org.mockito.Mockito
import org.mockito.Mockito._
import org.mockito.stubbing.OngoingStubbing
import org.scalatest
import org.scalatest.mockito.MockitoSugar
import org.scalatest.path

class Observes extends path.FunSpec with MockitoSugar with scalatest.Matchers with FunctionStubbingHelpers {

  val identityLocalization: String => String = x => x

  def when[T](method_call: T): OngoingStubbing[T] = {
    Mockito.when(method_call)
  }

  def spy[T](spied: T): T = Mockito.spy(spied)

  def reset[T](mock: T): T = {
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

