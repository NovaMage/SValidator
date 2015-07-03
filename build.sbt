name := "SValidator"

version := "0.3.2"

scalaVersion := "2.10.5"

scalacOptions ++= Seq("-feature", "-Xfatal-warnings", "-unchecked", "-deprecation")

exportJars := true

parallelExecution in Test := false

libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.10.5"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.2.4" % "test"

libraryDependencies += "org.mockito" % "mockito-all" % "1.9.5" % "test"
