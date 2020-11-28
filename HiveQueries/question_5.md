```April 2005

create database q5; use q5;

-- create vandal table

CREATE TABLE VANDAL
(
wiki_db string,
event_entity string,
event_type string,
event_timestamp string,
event_comment string,
event_user_id bigint,
event_user_text_historical string,
event_user_text string,
event_user_blocks_historical array<string>,
event_user_blocks array<string>,
event_user_groups_historical array<string>,
event_user_groups array<string>,
event_user_is_bot_by_historical array<string>,
event_user_is_bot_by array<string>,
event_user_is_created_by_self boolean,
event_user_is_created_by_system boolean,
event_user_is_created_by_peer boolean,
event_user_is_anonymous boolean,
event_user_registration_timestamp string,
event_user_creation_timestamp string,
event_user_first_edit_timestamp string,
event_user_revision_count bigint,
event_user_seconds_since_previous_revision bigint,
page_id bigint,
page_title_historical string,
page_title string,
page_namespace_historical int,
page_namespace_is_content_historical boolean,
page_namespace int,
page_namespace_is_content boolean,
page_is_redirect boolean,
page_is_deleted boolean,
page_creation_timestamp string,
page_first_edit_timestamp string,
page_revision_count bigint,
page_seconds_since_previous_revision bigint,
user_id bigint,
user_text_historical string,
user_text string,
user_blocks_historical array<string>,
user_blocks array<string>,
user_groups_historical array<string>,
user_groups array<string>,
user_is_bot_by_historical array<string>,
user_is_bot_by array<string>,
user_is_created_by_self boolean,
user_is_created_by_system boolean,
user_is_created_by_peer boolean,
user_is_anonymous boolean,
user_registration_timestamp string,
user_creation_timestamp string,
user_first_edit_timestamp string,
revision_id bigint,
revision_parent_id bigint,
revision_minor_edit boolean,
revision_deleted_parts array<string>,
revision_deleted_parts_are_suppressed boolean,
revision_text_bytes bigint,
revision_text_bytes_diff bigint,
revision_text_sha1 string,
revision_content_model string,
revision_content_format string,
revision_is_deleted_by_page_deletion boolean,
revision_deleted_by_page_deletion_timestamp string,
revision_is_identity_reverted boolean,
revision_first_identity_reverting_revision_id bigint,
revision_seconds_to_identity_revert bigint,
revision_is_identity_revert boolean,
revision_is_from_before_page_creation boolean,
revision_tags array<string>
)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY '\t';


-- Load data into table

LOAD DATA INPATH '/user/dannylee/q5/2020-09.enwiki.2005-04.tsv' 
INTO TABLE VANDAL;


-- Find lines with vandal revisions

select event_timestamp, event_comment, page_seconds_since_previous_revision from vandal where event_comment like "%vandal%" limit 1000;

-- get a total count

select count(*) from vandal where event_comment like "%vandal%";

10341 events

-- create a new table with the vandal revisions
CREATE TABLE V_EVENTS AS
select event_timestamp, event_comment, page_seconds_since_previous_revision 
from vandal where event_comment like "%vandal%";


-- get average of page_seconds_since_previous_revision

select avg(page_seconds_since_previous_revision)
from v_events;

 136916.91981132075 seconds  = 38.032222 hours
 
 
 
Calculate daily views form 2019-11-30 1 days views (42gb)


 -- create Views table

CREATE TABLE VIEWS
(domain string,
page string,
count int,
code string)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ' ';

-- Load data into table

LOAD DATA INPATH '/user/dannylee/input' 
INTO TABLE views;

-- Create new table with English Wikipedia only

CREATE TABLE EN_VIEWS AS
SELECT * FROM VIEWS WHERE DOMAIN='en.z';

-- Check lines in the EN_VIEWS table

SELECT COUNT(*) FROM EN_VIEWS;

 166,043,951 ( = to pages)
 

-- Get the sum of all views

SELECT SUM(count) FROM EN_VIEWS;

2,612,514,647 views / month

divided by 166,043,951 pages

= 15.73387 views per page per month

divided by 30 days

.5244623 views per page per day

divded by 24 hours 

.02185259722 per hour

multiplied by 38.032222 hours between repairs

0.831102 people between edits

Problems with this:
Doesnt account for popularity
Doesnt account for time of day
Time of vandalism check and time of views off by a decade.



```
