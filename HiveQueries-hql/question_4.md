/* HQL after files pre-processed with mapreduce into <K, V> pairs where K = "Page name" and V = views for that hour */

```
        CREATE DATABASE Q4;
        USE Q4;
        
        -- create UK table
        CREATE TABLE UK_3AM_TO_4AM_VIEWS
            (PAGE_NAME STRING,
            VIEW_COUNT_UK INT)
        ROW FORMAT DELIMITED
        FIELDS TERMINATED BY '\t';
        
        LOAD DATA INPATH 'filename' 
            INTO TABLE UK_3AM_TO_4AM_VIEWS;


        -- create US table
        CREATE TABLE US_3AM_TO_4AM_VIEWS
            (PAGE_NAME STRING,
            VIEW_COUNT_US INT)
        ROW FORMAT DELIMITED
        FIELDS TERMINATED BY '\t';

        LOAD DATA INPATH 'filename' 
            INTO TABLE US_3AM_TO_4AM_VIEWS;

        -- create AU table
        CREATE TABLE AU_3AM_TO_4AM_VIEWS
            (PAGE_NAME STRING,
            VIEW_COUNT_AU INT)
        ROW FORMAT DELIMITED
        FIELDS TERMINATED BY '\t';
        
        LOAD DATA INPATH 'filename' 
            INTO TABLE AU_3AM_TO_4AM_VIEWS;

        
        -- JOIN UK AND US TABLES
        CREATE TABLE UK_US_JOIN AS
        SELECT uk.PAGE_NAME, uk.VIEW_COUNT_UK, us.VIEW_COUNT_US, 
            (uk.VIEW_COUNT_UK - us.VIEW_COUNT_US) as DIFFERENCE
        FROM UK_3AM_TO_4AM_VIEWS uk JOIN US_3AM_TO_4AM_VIEWS us
        ON (uk.PAGE_NAME = us.PAGE_NAME)
        ORDER BY DIFFERENCE DESC;
        
        
        -- JOIN AU AND US TABLES
        CREATE TABLE AU_US_JOIN AS
        SELECT au.PAGE_NAME, au.VIEW_COUNT_AU, us.VIEW_COUNT_US, 
            (au.VIEW_COUNT_AU - us.VIEW_COUNT_US) as DIFFERENCE
        FROM AU_3AM_TO_4AM_VIEWS au JOIN US_3AM_TO_4AM_VIEWS us
        ON (AU.PAGE_NAME = us.PAGE_NAME)
        ORDER BY DIFFERENCE DESC;
        
        
        -- My original query
        select * from uk_us_join order by difference desc limit 100;
        
        -- What I went with
        SELECT * from UK_US_JOIN WHERE VIEW_COUNT_US < 100 ORDER by DIFFERENCE desc,VIEW_COUNT_US asc

        ```
        SELECT * from AU_US_JOIN ORDER by DIFFERENCE desc,VIEW_COUNT_US asc limit 25;
