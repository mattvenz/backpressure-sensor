import ScalaProject._

val weaverVersion = "0.6.3"
val monixVersion = "3.4.0"
val catsVersion = "2.3.0"
val monixDeps = List(
    "org.typelevel" %% "cats-core" % catsVersion,
    "org.typelevel" %% "cats-effect" % catsVersion,
    "io.monix" %% "monix" % monixVersion,
)

ThisBuild / versionScheme := Some("semver-spec")

lazy val commonSettings = Seq(
  organization := "net.gfxmonk",
  testFrameworks += new TestFramework("weaver.framework.CatsEffect"),
  testFrameworks += new TestFramework("weaver.framework.Monix"),
  libraryDependencies ++= Seq(
    "com.disneystreaming" %% "weaver-cats" % weaverVersion % Test,
    "com.disneystreaming" %% "weaver-monix" % weaverVersion % Test,
  ) ++ monixDeps.map(_ % Test),
)

lazy val core = (project in file("core")).settings(
  commonSettings,
  publicProjectSettings,
  name := "backpressure-sensor-core",
)

lazy val testkit = (project in file("testkit")).settings(
  commonSettings,
  publicProjectSettings,
  name := "backpressure-sensor-testkit",
).dependsOn(core)

lazy val statsd = (project in file("statsd")).settings(
  commonSettings,
  publicProjectSettings,
  name := "backpressure-sensor-statsd",
  libraryDependencies ++= Seq(
    "com.datadoghq" % "java-dogstatsd-client" % "2.13.0"
  )
).dependsOn(core)

lazy val monix = (project in file("monix")).settings(
  commonSettings,
  publicProjectSettings,
  name := "backpressure-sensor-monix",
  libraryDependencies ++= monixDeps,
).dependsOn(core, testkit % Test, statsd)

lazy val akka = (project in file("akka")).settings(
  commonSettings,
  publicProjectSettings,
  name := "backpressure-sensor-akka",
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-stream" % "2.6.15",
  ),
).dependsOn(core, testkit % Test, statsd)

lazy val root = (project in file("."))
  .settings(
    name := "root",
    hiddenProjectSettings
  )
  .aggregate(core, statsd, monix, akka)