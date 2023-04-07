import sbtcrossproject.CrossProject

ThisBuild / scalaVersion := props.ProjectScalaVersion
ThisBuild / organization := props.Org
ThisBuild / organizationName := "Kevin's Code"
ThisBuild / crossScalaVersions := props.CrossScalaVersions

ThisBuild / testFrameworks ~=
  (frameworks => (TestFramework("hedgehog.sbt.Framework") +: frameworks).distinct)

ThisBuild / developers := List(
  Developer(
    props.GitHubUsername,
    "Kevin Lee",
    "kevin.code@kevinlee.io",
    url(s"https://github.com/${props.GitHubUsername}"),
  )
)

ThisBuild / homepage := Some(url(s"https://github.com/${props.GitHubUsername}/${props.RepoName}"))
ThisBuild / scmInfo :=
  Some(
    ScmInfo(
      url(s"https://github.com/${props.GitHubUsername}/${props.RepoName}"),
      s"git@github.com:${props.GitHubUsername}/${props.RepoName}.git",
    )
  )
ThisBuild / licenses := props.licenses

ThisBuild / resolvers += props.SonatypeSnapshots

ThisBuild / scalafixDependencies += "com.github.xuwei-k" %% "scalafix-rules" % "0.2.17"

ThisBuild / scalafixConfig := (
  if (scalaVersion.value.startsWith("3"))
    ((ThisBuild / baseDirectory).value / ".scalafix-scala3.conf").some
  else
    ((ThisBuild / baseDirectory).value / ".scalafix-scala2.conf").some
)

ThisBuild / scalafixScalaBinaryVersion := {
  val log        = sLog.value
  val newVersion = if (scalaVersion.value.startsWith("3")) {
    (ThisBuild / scalafixScalaBinaryVersion).value
  } else {
    CrossVersion.binaryScalaVersion(scalaVersion.value)
  }

  log.info(
    s">> Change ThisBuild / scalafixScalaBinaryVersion from ${(ThisBuild / scalafixScalaBinaryVersion).value} to $newVersion"
  )
  newVersion
}

lazy val openai4s = (project in file("."))
  .enablePlugins(DevOopsGitHubReleasePlugin)
  .settings(
    name := prefixedProjectName(""),
    description := "openai4s - OpenAI Client for Scala",
    libraryDependencies :=
      libraryDependenciesPostProcess(isScala3(scalaVersion.value), libraryDependencies.value),
  )
  .settings(noPublish)
  .settings(mavenCentralPublishSettings)
  .aggregate(
    coreJvm,
    coreJs,
    configJvm,
    configJs,
    httpJvm,
    httpJs,
  )

lazy val core = module("core", crossProject(JVMPlatform, JSPlatform))
  .settings(
    libraryDependencies ++= List(
      libs.cats,
      libs.newtype,
      libs.kittens,
    ) ++
      libs.refined ++ libs.extra ++ libs.circeAll ++ libs.hedgehogExtra
  )

lazy val coreJvm = core.jvm
lazy val coreJs  = core.js.settings(jsSettingsForFuture)

lazy val config = module("config", crossProject(JVMPlatform, JSPlatform))
  .settings(
    libraryDependencies ++= List(
      libs.cats,
      libs.newtype,
      libs.kittens,
      libs.typeLevelCaseInsensitive,
      libs.http4sCore,
    ) ++
      libs.refined ++
      libs.extra ++
      libs.pureConfig ++
      libs.hedgehogExtra
  )
  .dependsOn(core)

lazy val configJvm = config.jvm
lazy val configJs  = config.js.settings(jsSettingsForFuture)

lazy val http = module("http", crossProject(JVMPlatform, JSPlatform))
  .settings(
    libraryDependencies ++=
      List(
        libs.cats,
        libs.newtype,
      ) ++ libs.refined ++ libs.circeAll ++ libs.http4s
  )
  .dependsOn(core, config)

lazy val httpJvm = http.jvm
lazy val httpJs  = http.js.settings(jsSettingsForFuture)

lazy val props =
  new {

    val Org = "io.kevinlee"

    val GitHubUsername = "Kevin-Lee"
    val RepoName       = "openai4s"

    val Scala2Versions = List(
      "2.13.10",
    )
    val Scala2Version  = Scala2Versions.head

    val Scala3Version = "3.1.3"

//    val ProjectScalaVersion = "2.13.10"
    val ProjectScalaVersion = Scala2Version
//    val ProjectScalaVersion = Scala3Version

    lazy val licenses = List("MIT" -> url("http://opensource.org/licenses/MIT"))

    val SonatypeCredentialHost = "s01.oss.sonatype.org"
    val SonatypeRepository     = s"https://$SonatypeCredentialHost/service/local"

    val SonatypeSnapshots = "sonatype-snapshots" at s"https://$SonatypeCredentialHost/content/repositories/snapshots"

    val removeDottyIncompatible: ModuleID => Boolean =
      m =>
        m.name == "ammonite" ||
          m.name == "kind-projector" ||
          m.name == "better-monadic-for" ||
          m.name == "mdoc"

    val CrossScalaVersions =
      (Scala3Version :: Scala2Versions).distinct

    val IncludeTest = "compile->compile;test->test"

    val HedgehogVersion = "0.10.1"

    val HedgehogExtraVersion = "0.3.0"

    val CatsVersion = "2.7.0"

    val CatsEffect2Version       = "2.4.1"
    val CatsEffect2LatestVersion = "2.5.4"
    val CatsEffect3Version       = "3.3.14"

    val ExtrasVersion = "0.38.0"

    val NewtypeVersion = "0.4.4"

    val TypeLevelCaseInsensitiveVersion = "1.3.0"

    val RefinedVersion = "0.10.1"

    val KittensVersion = "3.0.0"

    val Http4sVersion = "0.23.18"

    val PureConfigVersion = "0.17.2"

    val CirceVersion = "0.14.3"

  }

lazy val libs = new {
  lazy val hedgehogCore   = "qa.hedgehog" %% "hedgehog-core"   % props.HedgehogVersion
  lazy val hedgehogRunner = "qa.hedgehog" %% "hedgehog-runner" % props.HedgehogVersion
  lazy val hedgehogSbt    = "qa.hedgehog" %% "hedgehog-sbt"    % props.HedgehogVersion

  lazy val hedgehog: List[ModuleID] =
    List(
      hedgehogCore,
      hedgehogRunner,
      hedgehogSbt,
    ).map(_ % Test)

  lazy val hedgehogExtraCore    = "io.kevinlee" %% "hedgehog-extra-core"    % props.HedgehogExtraVersion
  lazy val hedgehogExtraRefined = "io.kevinlee" %% "hedgehog-extra-refined" % props.HedgehogExtraVersion
  lazy val hedgehogExtra        = List(hedgehogExtraCore, hedgehogExtraRefined).map(_ % Test)

  lazy val newtype = "io.estatico" %% "newtype" % props.NewtypeVersion

  lazy val refined = List(
    "eu.timepit" %% "refined"            % props.RefinedVersion,
    "eu.timepit" %% "refined-cats"       % props.RefinedVersion,
    //    "eu.timepit" %% "refined-eval"            % props.RefinedVersion,
    "eu.timepit" %% "refined-pureconfig" % props.RefinedVersion,
  )

  lazy val kittens = "org.typelevel" %% "kittens" % props.KittensVersion

  lazy val cats = "org.typelevel" %% "cats-core" % props.CatsVersion

  lazy val extra = List(
    "io.kevinlee" %% "extras-render"                 % props.ExtrasVersion,
    "io.kevinlee" %% "extras-render-refined"         % props.ExtrasVersion,
    "io.kevinlee" %% "extras-cats"                   % props.ExtrasVersion,
    "io.kevinlee" %% "extras-circe"                  % props.ExtrasVersion,
    "io.kevinlee" %% "extras-scala-io"               % props.ExtrasVersion,
    "io.kevinlee" %% "extras-hedgehog-ce3"           % props.ExtrasVersion % Test,
    "io.kevinlee" %% "extras-hedgehog-circe"         % props.ExtrasVersion % Test,
    "io.kevinlee" %% "extras-refinement"             % props.ExtrasVersion,
    "io.kevinlee" %% "extras-type-info"              % props.ExtrasVersion,
    "io.kevinlee" %% "extras-testing-tools-cats"     % props.ExtrasVersion % Test,
    "io.kevinlee" %% "extras-testing-tools-effectie" % props.ExtrasVersion % Test,
  )

  lazy val typeLevelCaseInsensitive = "org.typelevel" %% "case-insensitive" % props.TypeLevelCaseInsensitiveVersion

  lazy val circeCore          = "io.circe" %% "circe-core"           % props.CirceVersion
  lazy val circeGeneric       = "io.circe" %% "circe-generic"        % props.CirceVersion
  lazy val circeGenericExtras = "io.circe" %% "circe-generic-extras" % props.CirceVersion
  lazy val circeParser        = "io.circe" %% "circe-parser"         % props.CirceVersion
  lazy val circeLiteral       = "io.circe" %% "circe-literal"        % props.CirceVersion
  lazy val circeRefined       = "io.circe" %% "circe-refined"        % props.CirceVersion
  lazy val circeAll           = List(
    circeCore,
    circeGeneric,
    circeGenericExtras,
    circeRefined,
    circeParser  % Test,
    circeLiteral % Test,
  )

  lazy val http4sCore = "org.http4s" %% "http4s-core" % props.Http4sVersion
  lazy val http4s     = List(
    http4sCore,
    "org.http4s" %% "http4s-ember-client" % props.Http4sVersion,
    "org.http4s" %% "http4s-circe"        % props.Http4sVersion,
    "org.http4s" %% "http4s-dsl"          % props.Http4sVersion,
  )

  lazy val pureConfig = List(
    "com.github.pureconfig" %% "pureconfig"        % props.PureConfigVersion,
    "com.github.pureconfig" %% "pureconfig-core"   % props.PureConfigVersion,
    "com.github.pureconfig" %% "pureconfig-http4s" % props.PureConfigVersion,
    "com.github.pureconfig" %% "pureconfig-ip4s"   % props.PureConfigVersion,
  )

}

lazy val mavenCentralPublishSettings: SettingsDefinition = List(
  /* Publish to Maven Central { */
  sonatypeCredentialHost := props.SonatypeCredentialHost,
  sonatypeRepository := props.SonatypeRepository,
  /* } Publish to Maven Central */
)

// scalafmt: off
def prefixedProjectName(name: String) = s"${props.RepoName}${if (name.isEmpty) "" else s"-$name"}"
// scalafmt: on

def isScala3(scalaVersion: String): Boolean = scalaVersion.startsWith("3")

def libraryDependenciesPostProcess(
  isDotty: Boolean,
  libraries: Seq[ModuleID],
): Seq[ModuleID] =
  if (isDotty)
    libraries.filterNot(props.removeDottyIncompatible)
  else
    libraries

def module(projectName: String, crossProject: CrossProject.Builder): CrossProject = {
  val prefixedName = prefixedProjectName(projectName)
  crossProject
    .in(file(s"modules/$prefixedName"))
    .settings(
      name := prefixedName,
      fork := true,
      semanticdbEnabled := true,
      semanticdbVersion := scalafixSemanticdb.revision,
      scalafixConfig := (
        if (scalaVersion.value.startsWith("3"))
          ((ThisBuild / baseDirectory).value / ".scalafix-scala3.conf").some
        else
          ((ThisBuild / baseDirectory).value / ".scalafix-scala2.conf").some
      ),
      scalacOptions ++= (if (isScala3(scalaVersion.value)) List.empty else List("-Xsource:3")),
      scalacOptions ~= (ops => ops.filter(_ != "UTF-8")),
      libraryDependencies ++= libs.hedgehog,
      wartremoverErrors ++= Warts.allBut(Wart.Any, Wart.Nothing, Wart.ImplicitConversion, Wart.ImplicitParameter),
      Compile / console / scalacOptions :=
        (console / scalacOptions)
          .value
          .filterNot(option => option.contains("wartremover") || option.contains("import")),
      Test / console / scalacOptions :=
        (console / scalacOptions)
          .value
          .filterNot(option => option.contains("wartremover") || option.contains("import")),
      /* } WartRemover and scalacOptions */
      testFrameworks ++= (testFrameworks.value ++ Seq(TestFramework("hedgehog.sbt.Framework"))).distinct,
      licenses := props.licenses,
      /* coverage { */
      coverageHighlighting := (CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, 10)) | Some((2, 11)) =>
          false
        case _ =>
          true
      }),
      /* } coverage */
    )
    .settings(mavenCentralPublishSettings)
}

lazy val jsSettingsForFuture: SettingsDefinition = List(
  Test / fork := false,
  Test / scalacOptions ++= (if (scalaVersion.value.startsWith("3")) List.empty
                            else List("-P:scalajs:nowarnGlobalExecutionContext")),
  Test / compile / scalacOptions ++= (if (scalaVersion.value.startsWith("3")) List.empty
                                      else List("-P:scalajs:nowarnGlobalExecutionContext")),
)
