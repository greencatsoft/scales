lazy val root = (project in file(".")).
  enablePlugins(ScalaJSPlugin).
  settings(
    name := "scales",
    description := "A Web Component based UI framework written in Scala.js.",
    organization := "com.greencatsoft",
    version := "0.1-SNAPSHOT",
    scalaVersion := "2.11.7",
    scalacOptions ++= Seq("-feature","-deprecation"),
    homepage := Some(url("http://github.com/greencatsoft/scales")),
    resolvers += Resolver.sonatypeRepo("snapshots"),
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.8.1",
	  "org.scala-js" %%% "scala-parser-combinators" % "1.0.2",
      "com.lihaoyi" %%% "scalatags" % "0.5.2",
	  "com.greencatsoft" %%% "greenlight" % "0.2-SNAPSHOT" % "test"
    ),
    jsDependencies in Test ++= Seq(
      ProvidedJS / "webcomponents.min.js",
      ProvidedJS / "object-observe-lite.min.js",
      RuntimeDOM
    ),
    scalaJSStage in Test := FastOptStage,
    testFrameworks := new TestFramework("com.greencatsoft.greenlight.Greenlight") :: Nil,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if(isSnapshot.value)
      	Some("snapshots" at nexus + "content/repositories/snapshots")
      else
      	Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    publishMavenStyle := true,
    pomIncludeRepository := { _ => false },
    pomExtra := (
      <licenses>
        <license>
          <name>Apache License 2.0</name>
          <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
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
  )
