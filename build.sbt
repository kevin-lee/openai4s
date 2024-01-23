import sbtcrossproject.CrossProject

ThisBuild / scalaVersion := props.ProjectScalaVersion
ThisBuild / organization := props.Org
ThisBuild / organizationName := "Kevin's Code"
ThisBuild / crossScalaVersions := props.CrossScalaVersions

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
    http4sJvm,
    http4sJs,
    apiJvm,
    apiJs,
  )

lazy val core = module("core", crossProject(JVMPlatform, JSPlatform))
  .settings(
    libraryDependencies ++= List(
      libs.cats,
      libs.kittens(scalaVersion.value),
    ) ++
      (
        if (isScala3(scalaVersion.value))
          libs.tests.hedgehogExtraScala3
        else
          List(libs.newtype) ++ libs.tests.hedgehogExtraScala2
      ) ++
      libs.refined(scalaVersion.value) ++ libs.extras(scalaVersion.value) ++ libs.circeAll(
        scalaVersion.value
      )
  )

lazy val coreJvm = core.jvm
lazy val coreJs  = core.js.settings(jsSettingsForFuture)

lazy val config = module("config", crossProject(JVMPlatform, JSPlatform))
  .settings(
    libraryDependencies ++=
      List(
        libs.cats,
        libs.kittens(scalaVersion.value),
        libs.typeLevelCaseInsensitive,
        libs.http4sCore,
      ) ++
        (
          if (isScala3(scalaVersion.value))
            libs.tests.hedgehogExtraScala3
          else
            List(libs.newtype, libs.pureconfig) ++ libs.tests.hedgehogExtraScala2
        ) ++
        libs.refined(scalaVersion.value) ++
        libs.extras(scalaVersion.value) ++
        libs.pureConfigAll
  )
  .dependsOn(core % props.IncludeTest)

lazy val configJvm = config.jvm
lazy val configJs  = config.js.settings(jsSettingsForFuture)

lazy val http4s = module("http4s", crossProject(JVMPlatform, JSPlatform))
  .settings(
    libraryDependencies ++=
      List(
        libs.cats,
        libs.logback,
        libs.pureconfigCatsEffect3 % Test,
      ) ++
        (if (isScala3(scalaVersion.value)) List.empty else List(libs.newtype)) ++
        libs.refined(scalaVersion.value) ++
        libs.circeAll(scalaVersion.value) ++
        libs.http4s ++
        libs.extras(scalaVersion.value)
  )
  .dependsOn(core, config)

lazy val http4sJvm = http4s.jvm
lazy val http4sJs  = http4s.js.settings(jsSettingsForFuture)

lazy val api = module("api", crossProject(JVMPlatform, JSPlatform))
  .settings(
    libraryDependencies ++=
      List(
        libs.cats,
        libs.logback,
        libs.pureconfigCatsEffect3 % Test,
      ) ++
        (
          if (isScala3(scalaVersion.value))
            libs.tests.hedgehogExtraScala3
          else
            List(libs.newtype) ++ libs.tests.hedgehogExtraScala2
        ) ++
        libs.refined(scalaVersion.value) ++ libs.circeAll(scalaVersion.value)
  )
  .dependsOn(core % props.IncludeTest, config, http4s % props.IncludeTest)

lazy val apiJvm = api.jvm
lazy val apiJs  = api.js.settings(jsSettingsForFuture)

lazy val props =
  new {

    val Org = "io.kevinlee"

    val GitHubUsername = "kevin-lee"
    val RepoName       = "openai4s"

    val Scala2Versions = List(
      "2.13.12"
    )
    val Scala2Version  = Scala2Versions.head

    val Scala3Version = "3.3.1"

//    val ProjectScalaVersion = "2.13.10"
//    val ProjectScalaVersion = Scala2Version
    val ProjectScalaVersion = Scala3Version

    lazy val licenses = List(License.MIT)

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

    val HedgehogExtraVersion = "0.6.0"

    val CatsVersion = "2.10.0"

    val CatsEffect2Version       = "2.4.1"
    val CatsEffect2LatestVersion = "2.5.5"
    val CatsEffect3Version       = "3.5.3"

    val ExtrasVersion = "0.42.0"

    val NewtypeVersion = "0.4.4"

    val Refined4sVersion = "0.13.0"

    val TypeLevelCaseInsensitiveVersion = "1.4.0"

    val RefinedVersion = "0.10.1"

    val RefinedLatestVersion = "0.11.0"

    val KittensVersion             = "3.0.0"
    val KittensForScala3_1_Version = "3.0.0-M4"

    val Http4sVersion       = "0.22.15"
    val Http4sLatestVersion = "0.23.23"

    val PureConfigVersion = "0.17.4"

    val CirceVersion = "0.14.3"

    val CirceLatestVersion = "0.14.5"

    val LogbackVersion = "1.4.11"

  }

lazy val libs = new {

  lazy val newtype = "io.estatico" %% "newtype" % props.NewtypeVersion

  def refined4s(scalaVersion: String) =
    if (isScala3(scalaVersion))
      List(
        "io.kevinlee"    %% "refined4s-core"                  % props.Refined4sVersion,
        "io.kevinlee"    %% "refined4s-cats"                  % props.Refined4sVersion,
        "io.kevinlee"    %% "refined4s-circe"                 % props.Refined4sVersion,
        "io.kevinlee"    %% "refined4s-pureconfig"            % props.Refined4sVersion,
        "io.kevinlee"    %% "refined4s-doobie-ce3"            % props.Refined4sVersion,
        "io.kevinlee"    %% "refined4s-extras-render"         % props.Refined4sVersion,
        "io.kevinlee"    %% "refined4s-refined-compat-scala3" % props.Refined4sVersion,
      )
    else
      List("io.kevinlee" %% "refined4s-refined-compat-scala2" % props.Refined4sVersion)

  def refined(scalaVersion: String): List[ModuleID] =
    (if (isScala3(scalaVersion))
       List.empty
     else
       List(
         "eu.timepit" %% "refined"            % props.RefinedVersion,
         "eu.timepit" %% "refined-cats"       % props.RefinedVersion,
         //    "eu.timepit" %% "refined-eval"            % props.RefinedVersion,
         "eu.timepit" %% "refined-pureconfig" % props.RefinedVersion,
       )) ++ refined4s(scalaVersion)

  def kittens(scalaVersion: String) = {
    val version =
      if (scalaVersion.startsWith("3.1")) props.KittensForScala3_1_Version
      else
        props.KittensVersion

    "org.typelevel" %% "kittens" % version
  }

  lazy val cats = "org.typelevel" %% "cats-core" % props.CatsVersion

  def extras(scalaVersion: String): List[ModuleID] = {
    List(
      "io.kevinlee" %% "extras-render"                 % props.ExtrasVersion,
      "io.kevinlee" %% "extras-cats"                   % props.ExtrasVersion,
      "io.kevinlee" %% "extras-circe"                  % props.ExtrasVersion,
      "io.kevinlee" %% "extras-scala-io"               % props.ExtrasVersion,
      "io.kevinlee" %% "extras-hedgehog-ce3"           % props.ExtrasVersion % Test,
      "io.kevinlee" %% "extras-hedgehog-circe"         % props.ExtrasVersion % Test,
      "io.kevinlee" %% "extras-type-info"              % props.ExtrasVersion,
      "io.kevinlee" %% "extras-fs2-v3-text"            % props.ExtrasVersion,
      "io.kevinlee" %% "extras-testing-tools-cats"     % props.ExtrasVersion % Test,
      "io.kevinlee" %% "extras-testing-tools-effectie" % props.ExtrasVersion % Test,
    ) ++ (if (isScala3(scalaVersion)) List.empty
          else
            List(
              "io.kevinlee" %% "extras-refinement"     % props.ExtrasVersion,
              "io.kevinlee" %% "extras-render-refined" % props.ExtrasVersion,
            ))
  }
  lazy val typeLevelCaseInsensitive = "org.typelevel" %% "case-insensitive" % props.TypeLevelCaseInsensitiveVersion

  lazy val circeCore          = "io.circe" %% "circe-core"
  lazy val circeGeneric       = "io.circe" %% "circe-generic"
  lazy val circeGenericExtras = "io.circe" %% "circe-generic-extras"
  lazy val circeParser        = "io.circe" %% "circe-parser"
  lazy val circeLiteral       = "io.circe" %% "circe-literal"
  lazy val circeRefined       = "io.circe" %% "circe-refined"

  def circeAll(scalaVersion: String): List[ModuleID] = {
    val circeVersion = if (isScala3(scalaVersion)) props.CirceLatestVersion else props.CirceVersion

    List(
      circeCore    % circeVersion,
      circeGeneric % circeVersion,
      circeParser  % circeVersion % Test,
      circeLiteral % circeVersion % Test,
    ) ++ (if (isScala3(scalaVersion)) List.empty
          else
            List(
              circeGenericExtras % circeVersion,
              circeRefined       % circeVersion,
            ))
  }

  lazy val http4sCore = "org.http4s" %% "http4s-core" % props.Http4sLatestVersion
  lazy val http4s     = List(
    http4sCore,
    "org.http4s" %% "http4s-ember-client" % props.Http4sLatestVersion,
    "org.http4s" %% "http4s-circe"        % props.Http4sLatestVersion,
    "org.http4s" %% "http4s-dsl"          % props.Http4sLatestVersion,
  )

  lazy val pureConfigAll = List(
    "com.github.pureconfig" %% "pureconfig-core"   % props.PureConfigVersion,
    "com.github.pureconfig" %% "pureconfig-http4s" % props.PureConfigVersion,
    "com.github.pureconfig" %% "pureconfig-ip4s"   % props.PureConfigVersion,
  )

  lazy val pureconfig = "com.github.pureconfig" %% "pureconfig" % props.PureConfigVersion

  lazy val pureconfigCatsEffect3 =
    "com.github.pureconfig" %% "pureconfig-cats-effect" % props.PureConfigVersion

  lazy val logback = "ch.qos.logback" % "logback-classic" % props.LogbackVersion

  lazy val tests = new {
    lazy val hedgehogCore   = "qa.hedgehog" %% "hedgehog-core"   % props.HedgehogVersion % Test
    lazy val hedgehogRunner = "qa.hedgehog" %% "hedgehog-runner" % props.HedgehogVersion % Test
    lazy val hedgehogSbt    = "qa.hedgehog" %% "hedgehog-sbt"    % props.HedgehogVersion % Test

    lazy val hedgehog: List[ModuleID] =
      List(
        hedgehogCore,
        hedgehogRunner,
        hedgehogSbt,
      )

    lazy val hedgehogExtraCore      = "io.kevinlee" %% "hedgehog-extra-core"      % props.HedgehogExtraVersion % Test
    lazy val hedgehogExtraRefined   = "io.kevinlee" %% "hedgehog-extra-refined"   % props.HedgehogExtraVersion % Test
    lazy val hedgehogExtraRefined4s = "io.kevinlee" %% "hedgehog-extra-refined4s" % props.HedgehogExtraVersion % Test

    lazy val hedgehogExtraScala2 = List(
      hedgehogExtraCore,
      hedgehogExtraRefined,
    )

    lazy val hedgehogExtraScala3 = List(
      hedgehogExtraCore,
      hedgehogExtraRefined4s,
    )

  }

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
      scalacOptions ~= (_.map {
        case "UTF-8" => "utf8"
        case s => s
      }),
      libraryDependencies ++= libs.tests.hedgehog,
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
      licenses := props.licenses,
      /* coverage { */
      coverageHighlighting := (CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, 10)) | Some((2, 11)) =>
          false
        case _ =>
          true
      }),
      /* } coverage */

      scalacOptions ~= (_.filterNot(_.startsWith("-language"))),
      scalacOptions ++= List(
        "-language:dynamics",
        "-language:existentials",
        "-language:higherKinds",
        "-language:reflectiveCalls",
        "-language:experimental.macros",
        "-language:implicitConversions",
      ),
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
