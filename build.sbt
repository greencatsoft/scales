name := "scales"

description in ThisBuild := "A Web Component based UI framework written in Scala.js."

organization in ThisBuild := "com.greencatsoft"

version in ThisBuild := "0.1-SNAPSHOT"

homepage in ThisBuild := Some(url("http://github.com/greencatsoft/scales"))

licenses in ThisBuild := Seq("Apache License 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.html"))

pomExtra in ThisBuild := (
  <scm>
    <url>git@github.com:greencatsoft/scalajs-angular.git</url>
    <connection>scm:git:git@github.com:greencatsoft/scalajs-angular.git</connection>
  </scm>
  <developers>
    <developer>
      <id>mysticfall</id>
      <name>Xavier Cho</name>
      <url>http://github.com/mysticfall</url>
    </developer>
  </developers>
)

resolvers in ThisBuild += Resolver.sonatypeRepo("snapshots")

val scalaSettings = Seq(
  scalaVersion := "2.11.7",
  scalacOptions ++= Seq("-feature","-deprecation"),
  unmanagedSourceDirectories in Compile := (scalaSource in Compile).value :: Nil,
  unmanagedSourceDirectories in Test := (scalaSource in Test).value :: Nil)

val scalaJSSettings = Seq(
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.8.1",
    "org.scala-js" %%% "scala-parser-combinators" % "1.0.2",
    "com.lihaoyi" %%% "scalatags" % "0.5.2"
  )
)

lazy val root = (project in file("."))
  .settings(scalaSettings)
  .settings(
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if(isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    publishMavenStyle := true,
    pomIncludeRepository := { _ => false },
    unmanagedSourceDirectories in Compile := Nil,
    unmanagedSourceDirectories in Test := Nil,
    mappings in (Compile, packageBin) ++= mappings.in(core, Compile, packageBin).value,
    mappings in (Compile, packageSrc) ++= mappings.in(core, Compile, packageSrc).value
  )
  .aggregate(test, core)

lazy val core = (project in file("core"))
  .enablePlugins(ScalaJSPlugin)
  .settings(scalaSettings)
  .settings(scalaJSSettings)
  .settings(
    name := "scales-core",
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value % "compile"),
    publish := {},
    publishLocal := {}
  )

lazy val test = (project in file("test"))
  .enablePlugins(ScalaJSPlugin)
  .settings(scalaSettings)
  .settings(scalaJSSettings)
  .settings(
    name := "scales-test",
    libraryDependencies ++= Seq(
      "com.greencatsoft" %%% "greenlight" % "0.2-SNAPSHOT" % "test"),
    jsDependencies in Test ++= Seq(
      ProvidedJS / "webcomponents.min.js",
      ProvidedJS / "object-observe-lite.min.js",
      RuntimeDOM
    ),
    scalaJSStage in Test := FastOptStage,
    testFrameworks := new TestFramework("com.greencatsoft.greenlight.Greenlight") :: Nil,
    publish := {},
    publishLocal := {}
  )
  .dependsOn(core % "test->test;compile->compile")
