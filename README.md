SValidator
==========

A framework for validation of scala objects in a fluent and concise way.

This project is heavily inspired by the [FluentValidation library for .NET](http://fluentvalidation.codeplex.com/)

Usage
=====

Consider the following case class.

```scala

case class Person(firstName: String,
                  lastName: String,
                  age: Int,
                  married: Boolean,
                  hasJob: Boolean,
                  notes: Option[String])

```
