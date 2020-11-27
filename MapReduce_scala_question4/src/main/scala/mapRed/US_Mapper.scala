package wordCount

import org.apache.hadoop.io.{IntWritable, LongWritable, Text}
import org.apache.hadoop.mapreduce.Mapper

class US_Mapper extends Mapper[LongWritable, Text, Text, IntWritable] {

  def myRegex(toMatch: String) : Boolean = {
    // val theRegex = ".*(D\\d*).*$".r  // uk
    // val theRegex = ".*(K\\d*).*$".r  // au
    val theRegex = ".*(V\\d+).*$".r  // us

    toMatch match {
      case theRegex(value) => true
      case _ => false
    }
  }

  def getViews(toMatch: String) : String = {
    // val theRegex = "(D\\d*)".r // uk
    // val theRegex = "(K\\d*)".r // au
    val theRegex = "(V\\d+)".r // us

    val pageName = toMatch.split("\\t")(0)
    val filteredViews = theRegex
      .findFirstIn(toMatch.split("\\t")(1))
    val justNumber = """\d+|\D+""".r.findAllIn(filteredViews.get).toList(1)
    //  println(s"${justNumber} = ${filteredViews.getOrElse(0)}")
    pageName + "\t" + justNumber
  }

  override def map(key: LongWritable, value: Text, context: Mapper[LongWritable,Text, Text, IntWritable]#Context): Unit = {
    val line = value.toString

    line
      .split("\\n")
      .filter(_.length > 0)
      .map(line => {
        val (page, visits) = line.split("\\t") match {
          case Array (a, b) => (a, b)
        }
        if (myRegex(visits)) line
      })
      .filter(_ != ())
      .map(line => {
        getViews(line.toString)
      }).toList
      .foreach({ str => {
        val (page, visits) = str.split("\\t") match {
          case Array (a, b) => (a, b)
        }
        context write(new Text(page), new IntWritable(visits.toIntOption.getOrElse(1)))
      }})
  }
}



