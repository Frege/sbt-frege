import sbt._
import sbt.Keys._
import sbt.complete.DefaultParsers.spaceDelimited
import sbtcompat.PluginCompat._

object FixtureChecks extends AutoPlugin {
  override def trigger = allRequirements

  object autoImport {
    val checkClassFile = inputKey[Unit]("Check a class file in the configured output directory")
  }
  import autoImport._

  override def projectSettings =
    Seq(Compile, Test).flatMap { configuration =>
      inConfig(configuration)(Seq(
        checkClassFile := Def.uncached {
          val args = spaceDelimited("<exists|absent> <relative class file>").parsed
          require(args.size == 2 && Set("exists", "absent")(args.head), "Expected exists|absent and a class file")
          val output = new File(classDirectory.value, args(1))
          val correct = if (args.head == "exists") output.isFile() else !output.exists()
          assert(correct, s"Expected ${args.head}: $output")
        }
      ))
    }
}
