import org.nlogo.build.NetLogoExtension

enablePlugins(NetLogoExtension)

name       := "nnet"
version    := "1.0.0"
isSnapshot := true

scalaVersion          := "3.7.0"
Compile / scalaSource := baseDirectory.value / "src" / "main"
Test / scalaSource    := baseDirectory.value / "src" / "test"
scalacOptions        ++= Seq("-deprecation", "-unchecked", "-Xfatal-warnings", "-encoding", "us-ascii", "-release", "11")

netLogoClassManager := "org.nlogo.extensions.nnet.NNetExtension"
netLogoVersion      := "7.0.0-2486d1e"
netLogoZipExtras   ++= Seq(baseDirectory.value / "README.md", baseDirectory.value / "example-models")

// Additional dependencies for neural network operations
libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4"
)
