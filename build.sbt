name := "SValidator"

version := "0.3.1"

scalaVersion := "2.10.3"

scalacOptions ++= Seq("-feature", "-Xfatal-warnings", "-unchecked", "-deprecation")

exportJars := true

parallelExecution in Test := false

libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.10.3"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.0.M5b" % "test"

libraryDependencies += "junit" % "junit" % "4.6" % "test"

libraryDependencies += "org.mockito" % "mockito-all" % "1.9.5" % "test"
