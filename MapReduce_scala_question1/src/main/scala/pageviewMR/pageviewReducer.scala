package pageviewMR

import java.lang

import org.apache.hadoop.io.{IntWritable, Text}
import org.apache.hadoop.mapreduce.Reducer

class pageviewReducer extends Reducer[Text, IntWritable, Text, IntWritable] {

  override def reduce(key: Text, values: lang.Iterable[IntWritable], context: Reducer[Text, IntWritable, Text, IntWritable]#Context): Unit = {
    var count = 0

//    values.forEach(count += _.get())
    values.forEach(count += _.get())

    context.write(key, new IntWritable(count))
  }
}
