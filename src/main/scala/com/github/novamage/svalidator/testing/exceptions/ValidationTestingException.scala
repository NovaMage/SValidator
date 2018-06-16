package com.github.novamage.svalidator.testing.exceptions

/** Thrown by validation testing helpers methods when conditions of testing are unmet
  *
  * @param message A descriptive message of why the test failed
  */
class ValidationTestingException(message: String) extends Exception(message) {

}