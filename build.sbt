name := "cassandra4io"

inThisBuild(
  List(
    organization := "com.ringcentral",
    organizationName := "ringcentral",
    licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    scalaVersion := crossScalaVersions.value.head,
    crossScalaVersions := Seq("2.13.8", "2.12.15"),
    licenses := Seq(("Apache-2.0", url("https://opensource.org/licenses/Apache-2.0"))),
    homepage := Some(url("https://github.com/ringcentral/cassandra4io")),
    developers := List(
      Developer(id = "narma", name = "Sergey Rublev", email = "alzo@alzo.space", url = url("https://narma.github.io")),
      Developer(
        id = "alexuf",
        name = "Alexey Yuferov",
        email = "aleksey.yuferov@icloud.com",
        url = url("https://github.com/alexuf")
      )
    )
  )
)

lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    IntegrationTest / fork := true,
    libraryDependencies ++= Seq(
      "org.typelevel"       %% "cats-effect"                    % "3.3.5",
      "co.fs2"              %% "fs2-core"                       % "3.2.5",
      "com.datastax.oss"     % "java-driver-core"               % "4.14.0",
      "com.chuusai"         %% "shapeless"                      % "2.3.8"
    ) ++ Seq(
      "com.disneystreaming" %% "weaver-cats"                    % "0.7.10"  % "it,test",
      "org.testcontainers"   % "testcontainers"                 % "1.16.3" % "it",
      "com.dimafeng"        %% "testcontainers-scala-cassandra" % "0.40.2" % "it",
      "ch.qos.logback"       % "logback-classic"                % "1.2.11" % "it,test"
    ) ++ (scalaBinaryVersion.value match {
      case v if v.startsWith("2.13") =>
        Seq.empty

      case v if v.startsWith("2.12") =>
        Seq("org.scala-lang.modules" %% "scala-collection-compat" % "2.6.0")

      case other                     =>
        sys.error(s"Unsupported scala version: $other")
    })
  )

Compile / compile / scalacOptions ++= Seq(
  "-encoding",
  "utf-8",
  "-feature",
  "-unchecked",
  "-deprecation"
) ++
  (scalaBinaryVersion.value match {
    case v if v.startsWith("2.13") =>
      List(
        "-Xlint:strict-unsealed-patmat",
        "-Xlint:-serial",
        // "-Ywarn-unused",
        "-Ymacro-annotations",
        "-Yrangepos",
        "-Werror",
        "-explaintypes",
        "-language:higherKinds",
        "-language:implicitConversions",
        "-Xfatal-warnings",
        "-Wconf:any:error"
      )
    case v if v.startsWith("2.12") =>
      List(
        "-language:higherKinds",
        // "-Ywarn-unused",
        "-Yrangepos",
        "-explaintypes",
        "-language:higherKinds",
        "-language:implicitConversions",
        "-Xfatal-warnings"
      )
    case v if v.startsWith("0.")   =>
      Nil
    case other                     => sys.error(s"Unsupported scala version: $other")
  })

testFrameworks := Seq(new TestFramework("weaver.framework.CatsEffect"))
