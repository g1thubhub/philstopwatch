package profile.sparkjobs

object Helper {

  def fatFunctionInner(result: String): String = {
    val newObject = new Object()
    var stringRepresentation = newObject.toString
    stringRepresentation += result
    stringRepresentation
  }

  def minuteSleep(): Int = {
    Thread.sleep(1000)
    1
  }


}
