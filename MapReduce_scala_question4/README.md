# Project1_question_4

This directory holds the project for a MapReduce program written in Scala to filter Wikipedia page views and extract specific time periods correlating to the early morning hours of 3-4 AM in 3 time zones (UTC 0, +6, -6) which relate to 3 cities (UK, Perth Australia, CST-Chicago/Dallas).  The information I was seeking was pages being viewed at early morning hours in these locations that were more popular than in the other time zones.  

## Data Files

The data files were from November 2019, and the format was Wikipedia's Pagecounts-ez format (deprecated in September 2020).  The files for the month are divided by day and can be downloaded using the command:
``` 
$ wget https://dumps.wikimedia.org/other/pagecounts-ez/merged/2019/2019-11/pagecounts-2019-11-{01..30}.bz2
````

The total size of the downloads is very large.  Each bzip2 file is approx 450 mb.  The whole month is about 13.5 gb bzipped, uncompressed the data is ~40 gb.

Example of the data in this file format:
```
en.z	 Anne_Tyler 	110 	A1B2C2D3E4F1G4H3I6K1L1M6N2O6P9Q7R13S10T6U11V4W2X6
```
The first column is the Wikipedia site, the second is the page title, the third is the monthly total and the fourth column represent the hourly views.  Specifically, for the fourth column, the letters represent an hour of the day, so A = hour 0 (12:00 AM), B = hour 1 (1:00 AM) and so on.  The sequence A1 represents 1 view at midnight (hour 0).  B2 represents 2 views at 1 am.  P9 represents 9 views at 2pm.  And so on.

## The code

The Driver.scala sets up the mapreduce job's configuration and sets the classes of various parts of mapreduce, including the input formats, the mapper and reducer classes and the output formats.  Each of the 3 mappers (Mapper_AU, Mapper_UK, Mapper_US) was run along with the `tzreducer` to extract all the page views for the month for the specific time zone identifier we are looking for.

AU: letter K (corresponds to 3-4 AM at UTC -8, Perth)  
UK: letter D (corresponds to 3-4 AM at UTC 0, all UK)  
US: letter V (corresponds to 3-4 AM at UTC -6, CST, Dallas/Chicago)  

The output is sent the reducer as <K-V> pairs where the key is the page and the value is the number of visits.
  
 ## Running the code
 
 Make sure yarn and hdfs are running:
 ```
 $ $HADOOP_HOME/sbin/start-dfs.sh
 
 $ $HADOOP_HOME/sbin/start-yarn.sh
 
 ```
 
Upload the input files to a directory on hdfs, in this example /input2/
```
$ hdfs dfs -put ./download/* input2
```

Create a "fat" jar using the "assembly" plugin.  To do this, start the `sbt shell` and type `assembly`.
```
[info] set current project to timeZoneMapReduce (in build file:/D:/Git/revature/Project-1/MapReduce_scala_question4/)
[IJ]sbt:timeZoneMapReduce>
[IJ]sbt:timeZoneMapReduce> assembly
[info] Strategy 'discard' was applied to 435 files (Run the task at debug level to see details)
[info] Strategy 'first' was applied to 2 files (Run the task at debug level to see details)
[success] Total time: 20 s, completed Nov 28, 2020 8:37:07 PM
[IJ]sbt:timeZoneMapReduce>
```

Then run the command in the shell using:
```
$ hadoop jar target/scala-2.13/timeZoneMapReduce.jar input2 output2
```

The output will show up on HDFS in the /home/user-name/output2 directory.

After processing the files, the output was loaded into Hive tables and HQL queries were run. Follow this [link](https://github.com/dannyhlee/P1-Wikipedia-Big-Data/blob/main/HiveQueries/question_4.md) to see the queries.
