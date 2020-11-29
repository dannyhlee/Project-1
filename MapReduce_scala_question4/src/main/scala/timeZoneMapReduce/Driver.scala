package timeZoneMapReduce

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.{IntWritable, Text}
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.mapreduce.lib.input.{FileInputFormat, TextInputFormat}
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat

object Driver extends App {

  if (args.length != 2) {
    println("Usage: WordDriver <input dir> <output dir>")
    System.exit(-1)
  }

  val conf : Configuration = new Configuration();
  val job = Job.getInstance(conf, "tz-mapReduce")

  job.setInputFormatClass(classOf[TextInputFormat])
  job.setJarByClass(Driver.getClass)

  FileInputFormat.addInputPath(job, new Path(args(0)))
  FileOutputFormat.setOutputPath(job, new Path(args(1)))

  job.setMapperClass(classOf[Mapper_AU])
//  job.setMapperClass(classOf[Mapper_UK])
//  job.setMapperClass(classOf[Mapper_US])
  job.setReducerClass(classOf[tzReducer])

  job.setOutputKeyClass(classOf[Text])
  job.setOutputValueClass(classOf[IntWritable])

  val success = job.waitForCompletion(true)
  System.exit(if (success) 0 else 1)

}
