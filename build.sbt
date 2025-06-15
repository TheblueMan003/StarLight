val scala3Version = "3.3.5"

lazy val root = project
  .in(file("."))
  .settings(
    name              := "StarLight",
    version           := "0.1.5-SNAPSHOT",
    organization      := "com.github.theblueman003",

    scalaVersion := scala3Version,

    publishMavenStyle := true,

    libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test,
    libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "2.1.1",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.11" % Test,
    libraryDependencies += "com.lihaoyi" %% "fastparse" % "3.1.1",
    libraryDependencies += "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4",
)
  