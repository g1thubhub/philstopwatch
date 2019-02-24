package profile.sparkjobs

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.apache.spark.sql.{Dataset, SparkSession}

object JobFatso {

  val dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

  // creates many objects, should take a few minutes to complete
  def fatFunctionOuter(string: String) = {
    var result = ""
    for (i <- 0 until 500000) {
      result = Helper.fatFunctionInner(result)
      if (i % 90000 == 0)
        result = result.substring(0, 16)
    }
    string + "@@" + result
  }

  def main(args: Array[String]) {

    val start = LocalDateTime.now()
    println(s"******************\n$start\n******************")

    // Initialization:
    val threads = 3 // program simulates a single executor with 3 cores (one local JVM with 3 threads)
    val session = SparkSession.builder.
      master(s"local[$threads]")
      .appName("Profile Fatso")
      .getOrCreate()
    import session.implicits._
    val lines: Dataset[String] = session.createDataset(Seq[String]("a", "b", "c"))


    val result = lines.map(fatFunctionOuter)
    println("@@@" + result.collect().size)
    session.stop()
    val end = LocalDateTime.now()
    println(s"******************\n$start\n******************")
    println(s"******************\n$end\n******************")
  }

}
