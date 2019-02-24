package profile.sparkjobs

import java.time.LocalDateTime
import java.util.Properties
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation
import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP}
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.{SparkSession}


// Prevents Exception in thread "main" org.apache.spark.SparkException: Task not serializable
//  Caused by: java.io.NotSerializableException: edu.stanford.nlp.pipeline.StanfordCoreNLP
class DistribbutedStanfordCoreNLP extends Serializable {
  val props = new Properties()
  props.setProperty("annotators", "tokenize,ssplit,pos,lemma,parse")
  lazy val pipeline = new StanfordCoreNLP(props)
}


object JobHeckler {

  def main(args: Array[String]) = {

    val threads = 3 // program simulates a single executor with 3 cores (one local JVM with 3 threads)
    implicit val session = SparkSession.builder.
      master(s"local[$threads]")
      .appName("Profile Heckler")
      .getOrCreate()
    import session.implicits._

    val start = LocalDateTime.now()

    var strings = List.empty[String]
    for (_ <- (0 until 20000)) {
      strings = "The small red car turned very quickly around the corner." :: strings
      strings = "The quick brown fox jumps over the lazy dog." :: strings
      strings = "This is supposed to be a nonsensial sentence but in the context of this app it does make sense after all." :: strings
    }

    val stringsDS = session.createDataset(strings).coalesce(3)

    val pipelineWrapper = new DistribbutedStanfordCoreNLP()
    val pipelineBroadcast: Broadcast[DistribbutedStanfordCoreNLP] = session.sparkContext.broadcast(pipelineWrapper)

    // One StanfordCoreNLP object per record:
    val parsedStrings1 = stringsDS.map(string => {
      val props = new Properties()
      props.setProperty("annotators", "tokenize,ssplit,pos,lemma,parse")
      val annotation = new Annotation(string)
      val pipeline = new StanfordCoreNLP(props)
      pipeline.annotate(annotation)
      val parseTree = annotation.get(classOf[SentencesAnnotation]).get(0).get(classOf[TreeAnnotation])
      parseTree.toString
    })


    // One StanfordCoreNLP object per partition = 3 overall:
    val parsedStrings2 = stringsDS.mapPartitions(iterator => {
      val props = new Properties()
      props.setProperty("annotators", "tokenize,ssplit,pos,lemma,parse")
      lazy val pipeline = new StanfordCoreNLP(props)
      iterator.map(string => {
        val annotation = new Annotation(string)
        pipeline.annotate(annotation)
        val parseTree = annotation.get(classOf[SentencesAnnotation]).get(0).get(classOf[TreeAnnotation])
        parseTree.toString
      })
    })


    // One StanfordCoreNLP object per executor = one overall:
    val parsedStrings3 = stringsDS.map(string => {
      val annotation = new Annotation(string)
      pipelineBroadcast.value.pipeline.annotate(annotation)
      val parseTree = annotation.get(classOf[SentencesAnnotation]).get(0).get(classOf[TreeAnnotation])
      parseTree.toString
    })

    println(parsedStrings1.count())
    //    println(parsedStrings2.count())
    //    println(parsedStrings3.count())

    val end = LocalDateTime.now()
    println(s"******************\n$start\n******************")
    println(s"******************\n$end\n******************")

  }

}
