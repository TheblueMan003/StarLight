scalaVersion := "3.2.0"

name              := "StarLight"

version           := "0.1.5-SNAPSHOT"

organization      := "com.github.theblueman003"

publishMavenStyle := true

libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "2.1.1"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.11" % Test

libraryDependencies += "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4"

//libraryDependencies += "com.github.Querz" %% "NBT" % "6.1"