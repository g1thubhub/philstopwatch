package profile.sparkjobs

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.apache.spark.sql.{Dataset, SparkSession}

object JobSlacker {

  val dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

  def slacking(string: String) = {
    var counts = 0
    for (_ <- 0 until 600) {
      counts += Helper.minuteSleep()
    }
    counts
  }

  def main(args: Array[String]) {

    val start = LocalDateTime.now()
    println(s"******************\n$start\n******************")

    // Initialization:
    val threads = 3 // program simulates a single executor with 3 cores (one local JVM with 3 threads)
    val session = SparkSession.builder
      .master(s"local[$threads]")
      .appName("Profile Slacker")
      .getOrCreate()
    import session.implicits._
    val lines: Dataset[String] = session.createDataset(Seq[String]("a", "b", "c"))

    val result = lines.map(slacking)
    println("@@@" + result.collect().size)

    session.stop()

    val end = LocalDateTime.now()
    println(s"******************\n$start\n******************")
    println(s"******************\n$end\n******************")
  }


}