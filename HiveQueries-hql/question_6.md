```
CREATE TABLE MEDIA 
(
base_name string,
total_response_size int,
total int,
original int,
transcoded_audio int,
blank1 string,
blank2 string,
transcoded_image_0_199 int,
transcoded_image_200_399 int,
transcoded_image_400_599 int,
transcoded_image_600_799 int,
transcoded_image_800_999 int,
transcoded_image_1000 int,
transcoded_image int,
blank3 string,
blank4 string,
transcoded_movie int,
transcoded_movie_0_239 int,
transcoded_movie_240_479 int,
transcoded_movie_480 int,
blank5 string,
blank6 string,
referrer_internal int,
referrer_external int,
referrer_unknown int
)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY '\t';

LOAD DATA INPATH '/user/dannylee/q6/' INTO TABLE MEDIA;


-- Get the largest files

select base_name, total_response_size, total 
from media 
order by total_response_size desc, total desc 
limit 3;



```
