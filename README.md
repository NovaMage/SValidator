SValidator
==========

A library for validation of scala objects in a fluent and concise manner.

This project is heavily inspired by the [FluentValidation library for .NET](https://github.com/JeremySkinner/FluentValidation) and
licensed under the MIT license.

Installation
===========

SValidator is available on Maven for scala 2.12.  Just add the following line to your build.sbt:

```
libraryDependencies += "com.github.novamage" % "svalidator_2.12" % "0.9.14"
```

Quick Usage
===========

Consider the following case class.

```scala
case class Person(firstName: String,
                  lastName: String,
                  age: Int,
                  married: Boolean,
                  hasJob: Boolean,
                  notes: Option[String])
```

Create a class that inherits from `com.github.novamage.svalidator.validation.simple.SimpleValidator[A]` where A is
the type of the object you want to validate.  Then, import `com.github.novamage.svalidator.validation.simple.constructs._`
and make a fluent and elegant validation by using the `WithRules` builder on your simple validator, like:

```scala
  override def validate(implicit instance: Person): ValidationSummary = WithRules(

    For { _.firstName } ForField 'firstName
      mustNot be empty () withMessage "First name is required"
      must have maxLength 32 withMessage "Must have 32 characters or less",

    For { _.lastName } ForField 'lastName
      mustNot be empty () withMessage "Last name is required"
      must have maxLength 32 withMessage "Must have 32 characters or less",

    For { _.age } ForField 'age
      mustNot be negative () withMessage "Must be a positive number",

    For { _.married } ForField 'married
      must be(false) when { _.age < 18 } withMessage "Must be 18 years or older to allow marking marriage",

    For { _.hasJob } ForField 'hasJob
      must be(false) when { _.age < 21 } withMessage "Must be 21 years or older to allow marking a job"
    )
```

To perform the actual validation, create an instance of your validator class, and call the method `validate` passing in 
the instance to validate.

Check the [wiki](https://github.com/NovaMage/SValidator/wiki/SValidator) for more details.


