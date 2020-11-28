#### Intro

This folder contains the procedure and HQL queries for solving Question 1 dataset only using Hive 3.1.2

#### Data source

The data used for this question is filtered by Wikipedia to what they believe is only human traffic.  The analytics dataset description is available [here](https://dumps.wikimedia.org/other/pageviews/readme.html).  The dataset used for this question was all traffic on the date of October 20, 2020.  The dataset can be retrieved in the shell via the command line utility `wget`.  
```
$ wget https://dumps.wikimedia.org/other/pageviews/2020/2020-10/pageviews-20201020-{00..23}0000.gz
```
The download of 24 files will require 1.2 GB of disk (gzipped)

#### Prepare Hadoop/HDFS

(Hadoop installation instructions available [here](https://hadoop.apache.org/docs/r3.2.1/hadoop-project-dist/hadoop-common/SingleCluster.html))

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
$ hdfs -mkdir /user/user-name
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

### Prepare Hive

(Hive installation instructions available [here](https://cwiki.apache.org/confluence/display/Hive/GettingStarted#GettingStarted-InstallationandConfiguration))

- If desired, create a work directory with `mkdir dir-name` and `cd dir-name`.

1. Initialize a metastore schema with `schematool` using the derby rdbms. (docs: [schematool](https://cwiki.apache.org/confluence/display/Hive/Hive+Schema+Tool#HiveSchemaTool-TheHiveSchemaTool)) 

```
$ schematool -dbType derby -initSchema
```

2. In this example, we will use the `beeline` command line shell with `HiveServer2`.  `beeline` is based on the SQLLine CLI and is a replacement for the now deprecated `HIVECli`.

To test these queries, we can run `beeline` in testing mode:  
```
$ $HIVE_HOME/bin/beeline -u jdbc:hive2://
```

First, let's create an external Hive table, define our schema and load data from the files contained on HDFS in the directory `/user/user-name/input`.  The field separator is whitespace, and we'll DESCRIBE the table to verify.

```
CREATE EXTERNAL TABLE PAGEVIEWS
	(
	DOMAIN_CODE STRING,
	PAGE_TITLE STRING,
	COUNT_VIEWS INT,
	TOTAL_RESPONSE_SIZE INT
	)
	ROW FORMAT DELIMITED
	FIELDS TERMINATED BY ' '
	LOCATION '/user/dannylee/input';

DESCRIBE PAGEVIEWS;
```

Then, to retrieve our top pages by views on English Wikipedia on October 20th, we SUM the views for each page, for all pages in the English Wikipedia domain, group by page_title and sort them descending.
```
SELECT PAGE_TITLE, SUM(COUNT_VIEWS) AS TOTAL_VIEWS
FROM PAGEVIEWS WHERE DOMAIN_CODE="en"
GROUP BY PAGE_TITLE 
SORT BY TOTAL_VIEWS DESC
LIMIT 10;
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

The table PAGEVIEWS describes as:
```
+----------------------+------------+----------+
|       col_name       | data_type  | comment  |
+----------------------+------------+----------+
| domain_code          | string     |          |
| page_title           | string     |          |
| count_views          | int        |          |
| total_response_size  | int        |          |
+----------------------+------------+----------+
```

The results of the query returns:
```
+-------------------+----------+
|    page_title     |  total   |
+-------------------+----------+
| Main_Page         | 2726387  |
| Special:Search    | 910309   |
| Bible             | 148726   |
| -                 | 124890   |
| Jeffrey_Toobin    | 116724   |
| Microsoft_Office  | 71825    |
| Deaths_in_2020    | 62082    |
| F5_Networks       | 61049    |
| Robert_Redford    | 56808    |
| Jeff_Bridges      | 48696    |
+-------------------+----------+
```

If we compare this for our results from our [MapReducer written in Scala](https://github.com/dannyhlee/P1-Wikipedia-Big-Data/tree/main/MapReduce_scala_question1) we see the order is the same.  However, there is a slight disparity in some counts.  This was curious and inexplicable to me.

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

**Hive undercounts compared to MapReduce**  

|Page|Offset|
|---|---|
|Jeff_Bridges|-884|
|Robert_Redford|equal|
|F5_Networks |equal|
|Deaths_In_2020|-1|
|Microsoft_office|-26|
|Jeffrey_Toobin|equal|
|-|equal|
|Bible |equal|
|Special:Search|-52|
|Main_Page|-1,491|

#### Please Remember:

To exit beeline use the 
To shut down Yarn and DFS if you are just testing:
```
$ sbin/stop-dfs.sh
$ sbin/stop-yarn.sh
```
