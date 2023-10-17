import sbt._
import Keys._
import com.lightbend.paradox.sbt.ParadoxPlugin.autoImport._
import org.apache.pekko.PekkoParadoxPlugin
import org.apache.pekko.PekkoParadoxPlugin.autoImport._

object PekkoSamplePlugin extends sbt.AutoPlugin {
  override def requires = PekkoParadoxPlugin
  override def trigger = allRequirements
  object autoImport {
    val baseUrl = settingKey[String]("")
    val baseProject = settingKey[String]("")
    val templateName = settingKey[String]("")
    val bodyPrefix = settingKey[String]("")
    val bodyTransformation = settingKey[String => String]("")
  }
  import autoImport._

  // disabled display versioning, in other words: hidden unnecessary version.
  paradoxProperties += ("disabled.versioning.display" -> "true")

  val themeSettings = Seq(
    pekkoParadoxGithub := Some("https://github.com/apache/incubator-pekko"))

  val propertiesSettings = Seq(
    Compile / paradoxProperties ++= Map(
      "download_url" -> s"https://example.lightbend.com/v1/download/${templateName.value}"))

  val sourceDirectorySettings = Seq(
    bodyPrefix := s"""${name.value}
                     |=======================
                     |
                     |""".stripMargin,
    // Transform local paths to URL
    bodyTransformation := { case body =>
      val r = """\[([^]]+)\]\(([^)]+)\)""".r
      r.replaceAllIn(body,
        _ match {
          case r(lbl, uri) if !uri.contains("http") => s"""[$lbl](${baseUrl.value}/${baseProject.value}/$uri)"""
          case r(lbl, uri)                          => s"[$lbl]($uri)"
        })
    },
    // Copy README.md file
    Compile / paradox / sourceDirectory := {
      val outDir = (Compile / managedSourceDirectories).value.head / "paradox"
      val outFile = outDir / "index.md"
      val inDir = baseDirectory.value / ".." / ".." / baseProject.value
      val inFile = inDir / "README.md"
      IO.write(outFile, bodyPrefix.value + bodyTransformation.value(IO.read(inFile)))
      if ((inDir / "tutorial").exists) {
        IO.copyDirectory(inDir / "tutorial", outDir / "tutorial")
      }
      outDir
    })

  override def projectSettings: Seq[Setting[_]] =
    themeSettings ++
    propertiesSettings ++
    sourceDirectorySettings ++
    Seq(
      baseUrl := "https://github.com/apache/incubator-pekko-samples/current",
      crossPaths := false,
      templateName := baseProject.value.replaceAll("-sample-", "-samples-"))
}
