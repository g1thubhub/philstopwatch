package profile.sparkjobs

import java.time.LocalDateTime
import org.apache.spark.sql.SparkSession

object JobStraggler {

  def main(args: Array[String]) {

    val start = LocalDateTime.now()
    println(s"******************\n$start\n******************")


    val letters = ('a' until 'z').map(_.toString)

    val threads = 3 // program simulates a single executor with 3 cores (one local JVM with 3 threads)
    val session = SparkSession.builder.
      master(s"local[$threads]")
      .appName("Profile Straggler")
      .getOrCreate()
    import session.implicits._


    val frequencies = session.createDataset(letters).coalesce(3)
      .map(letter => if (letter == "d") (letter, 245000L) else (letter, 30000L))

    val sum = frequencies.map(pair => {
      var list = List.empty[Long]
      val letter = pair._1
      val frequ = pair._2
      for (i <- 0L until frequ) {
        list = list :+ i // Prepending too fast for example

      }
      (letter, list.sum)
    })

  sum.foreach(println(_))

  }

}
