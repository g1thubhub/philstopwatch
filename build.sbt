name := "philstopwatch"

version := "0.1"

scalaVersion := "2.11.11"

resolvers ++= Seq(
  "CoreNLP Models" at "http://knowitall.cs.washington.edu/maven2/",
  "bintray-sbt-plugins" at "http://dl.bintray.com/sbt/sbt-plugin-releases"
)

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.4.0"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.4.0"
libraryDependencies += "ch.cern.sparkmeasure" %% "spark-measure" % "0.13"
// NLP:
libraryDependencies += "edu.stanford.nlp" % "stanford-corenlp" % "3.9.2"
libraryDependencies += "edu.stanford.nlp" % "stanford-corenlp" % "3.9.2" classifier "models"

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}