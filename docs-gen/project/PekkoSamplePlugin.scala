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
  override def projectSettings: Seq[Setting[_]] = Seq(
    baseUrl := "https://github.com/apache/incubator-pekko-samples/tree/main",
    crossPaths := false,
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
    },
    paradoxProperties += ("download_url" -> s"https://example.lightbend.com/v1/download/${templateName.value}"),
    bodyPrefix := s"""${name.value}
                     |=======================
                     |
                     |""".stripMargin,
    bodyTransformation := { case body =>
      val r = """\[([^]]+)\]\(([^)]+)\)""".r
      r.replaceAllIn(body,
        _ match {
          case r(lbl, uri) if !uri.contains("http") => s"""[$lbl](${baseUrl.value}/${baseProject.value}/$uri)"""
          case r(lbl, uri)                          => s"[$lbl]($uri)"
        })
    },
    templateName := baseProject.value.replaceAll("-sample-", "-samples-"))
}
