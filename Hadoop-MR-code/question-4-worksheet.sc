
import scala.io.Source
import java.nio.file.Paths

import scala.io.Source
import java.nio.file.Paths

import org.apache.hadoop.io.IntWritable

import scala.collection.IterableOnce.iterableOnceExtensionMethods
println(Paths.get(".").toAbsolutePath)

val filename = "C:\\tmp\\part-1"

def ukRegex(toMatch: String) : Boolean = {
  val ukRegex = ".*(D\\d*).*$".r

  toMatch match {
    case ukRegex(value) => true
    case _ => false
  }
}

def ukGetViews(toMatch: String) : String = {
  val ukRegex = "(D\\d*)".r

  val pageName = toMatch.split("\\t")(0)
  val filteredViews = ukRegex
    .findFirstIn(toMatch.split("\\t")(1))
  val justNumber = """\d+|\D+""".r.findAllIn(filteredViews.get).toList(1)
//  println(s"${justNumber} = ${filteredViews.getOrElse(0)}")
  pageName + "\t" + justNumber
}

//  val auRegex = ".*(K\\d*).*$".r
//  val usRegex = ".*(V\\d*).*$".r
//
//def flat(list: List[Any]): List[Any] = list flatten {
//  case i: List[Any] => flat(i)
//  case e => List(e)
//}

// add UTF-8 encoding to resolve UnmappableCharacterException
// due to foreign characters in file.
val results = Source.fromFile(filename)("UTF-8")
  .getLines
  .map(line => {
    val (page, visits) = line.split("\\t") match {
      case Array (a, b) => (a, b)
    }
    if (ukRegex(visits)) line
})
  .filter(_ != ())
  .map(line => {
    ukGetViews(line.toString)
  }).toList

val processedResults = results.foreach(line => {
  println(line)
})


//processedResults.foreach(println)



//
//val flatResults = flat(results)
//
//println("got flat results")
//
//flatResults.foreach(println)
//
println("done!")

//  .flatMap(aTuple => context.write(new Text(aTuple._1), new IntWritable(aTuple._2.toIntOption.getOrElse(1))))


