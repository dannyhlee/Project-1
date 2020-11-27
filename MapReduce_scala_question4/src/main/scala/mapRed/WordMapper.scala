package wordCount

import org.apache.hadoop.io.{IntWritable, LongWritable, Text}
import org.apache.hadoop.mapreduce.Mapper

class WordMapper extends Mapper[LongWritable, Text, Text, IntWritable] {

  //  val auRegex = ".*(K\\d*).*$".r
  //  val usRegex = ".*(V\\d*).*$".r

  def ukRegex(toMatch: String): Boolean = {
    val ukRegex = ".*(D\\d*).*$".r

    toMatch match {
      case ukRegex(value) => true
      case _ => false
    }
  }

  def ukGetViews(toMatch: String): String = {
    val ukRegex = "(D\\d*)".r

    val pageName = toMatch.split("\\t")(0)
    val filteredViews = ukRegex
      .findFirstIn(toMatch.split("\\t")(1))
    val justNumber = """\d+|\D+""".r.findAllIn(filteredViews.get).toList(1)
    //  println(s"${justNumber} = ${filteredViews.getOrElse(0)}")
    pageName + "\t" + justNumber
  }

  override def map(key: LongWritable, value: Text, context: Mapper[LongWritable, Text, Text, IntWritable]#Context): Unit = {
    val line = value.toString

    line
      .split("\\n")
      .filter(_.length > 0)
      .map(line => {
        val (page, visits) = line.split("\\t") match {
          case Array(a, b) => (a, b)
        }
        if (ukRegex(visits)) line
      })
      .filter(_ != ())
      .map(line => {
        ukGetViews(line.toString)
      }).toList
      .foreach({ str => {
        val (page, visits) = str.split("\\t") match {
          case Array(a, b) => (a, b)
        }
        context write(new Text(page), new IntWritable(visits.toIntOption.getOrElse(1)))
      }
      })
  }
}
