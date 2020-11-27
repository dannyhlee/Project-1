#### Intro

This folder contains the source code for the Hadoop mapreduce program (Scala) for Question 1 of Project 1.  To replicate the results follow these instructions.

#### Data source

The data used for this question is filtered by Wikipedia to what they believe is only human traffic.  The analytics dataset description is available [here](https://dumps.wikimedia.org/other/pageviews/readme.html).  The dataset used for this question was all traffic on the date of October 20, 2020.  The dataset can be retrieved in the shell via the command line utility `wget`.  
```
$ wget https://dumps.wikimedia.org/other/pageviews/2020/2020-10/pageviews-20201020-{00..23}0000.gz
```
The download of 24 files will require 1.2 GB of disk (gzipped)

#### Prepare Hadoop/HDFS

1. Format the filesystem:  
```
$ bin/hdfs namenode -format
```

2. Start NameNode and DataNode:  
```
$ sbin/start-dfs.sh
```

3. Verify HDFS is running at [localhost:9870](http://localhost:9870).

4. Create your user directories:
```
$ hdfs -mkdir /user
$ hdfs -mkdir /user/<username>
```

5. Create your `input` directory and upload your files to HDFS:
```
$ hdfs dfs -mkdir input
$ hdfs dfs -put pageviews-20201020-* 
```

6. Start YARN resource manager:
```
$ sbin/start-yarn.sh
```

7. Verify Yarn ResourceManager is running working at [localhost:8088](localhost:8088).

### Run the Jar
You will need the [sbt-assembly](https://github.com/sbt/sbt-assembly) plugin to build the jar.  It is already listed in the `build/plugins.sbt` file, so if you open the repository in an editor like IntelliJ, it will load this for you and all you have to do is open the `sbt` shell and run the command:
```
> assembly
```

When the jar finishes compiling, you will find your artifact in the `target/scala-2.13/` directory.

Usage: pageviewMR <input dir> <inter dir> <output dir>

To run the jar in hadoop, specify an input directory on HDFS, and intermediary directory and an output directory.  The output directory must not exist or else you will receive a warning and the Hadoop will exit.

```
hadoop jar pageviewmr_2.13-0.1.jar input middle output
```

#### Input and Results:

The 24 input files have hourly data of views for each of Wikipedia's projects.  The file is a `tsv` which has 4 columns.  The four columns are (in order):
Wikipedia project name (ex: en for English), the title of page, the number of requests, and finally, the size of content returned (possibly deprecated).

```
en Dungeons_&_Dragons_campaign_settings 22 0
en Dungeons_&_Dragons_controversies 11 0
en Dungeons_&_Dragons_gameplay 5 0
en Dungeons_&_Dragons_iconic_characters 2 0
en Dungeons_&_Dragons_in_popular_culture 10 0
```

The program use the intermediary directory to store the output of the first mapper, so that the second mapper can read it in.  The results are a binary file (`SequenceFileOutputFormat`).  The first mapper read the input files, split them on whitespace and uses Scala pattern matching to find lines which corresponded to the **en** Wikipedia project (English language Wikipedia) and extract them, before transforming them into two-tuples consisting of the *page title (String)* and the *number of requests (Int)* and being collected.  This two-tuple was passed through the second mapper to reverse the K-V pairs, before being written to the context, in preparation for reduction.  After being reduced the output appears as such:

```
285     DUNGEONS_&_DRAGONS_CAMPAIGN_SETTINGS
285     DANGEROUS_LIES_(2020_FILM)
285     EDEN_BROLIN
285     EL_NIÑO–SOUTHERN_OSCILLATION
285     BURNOUT_PARADISE
285     BROOKS_LAICH
285     COMMANDER_KEEN
285     BIONTECH
```

Finally, to answer Question 1, which was:
>Which English wikipedia article got the most traffic on October 20?

We retrieve the output file with an HDFS `-get` command and `tail` the file:
```
$ hdfs dfs -get output/part-r-00000
2020-11-27 18:52:51,412 INFO sasl.SaslDataTransferClient: SASL encryption trust check: localHostTrusted = false, remoteHostTrusted = false
$ tail part-r-00000 
49580   JEFF_BRIDGES
56808   ROBERT_REDFORD
61049   F5_NETWORKS
62083   DEATHS_IN_2020
71851   MICROSOFT_OFFICE
116724  JEFFREY_TOOBIN
124890  -
148726  BIBLE
910361  SPECIAL:SEARCH
2727878 MAIN_PAGE
```

To verify these results, I used Hive and that procedure and results are available in the repository under the `/HiveQueries` directory.

#### Please Remember:
To shut down Yarn and DFS if you are just testing:
```
$ sbin/stop-dfs.sh
$ sbin/stop-yarn.sh
```
