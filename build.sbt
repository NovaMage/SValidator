name := "SValidator"

version := "0.0.1"

scalaVersion := "2.10.1-RC2"

scalacOptions += "-feature"

mainClass := Some("com.github.novamage.svalidator.Main")

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.0.M5b" % "test"

libraryDependencies += "org.mockito" % "mockito-all" % "1.9.5" % "test"
