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
        
        

        ```
