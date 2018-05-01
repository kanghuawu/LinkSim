CREATE KEYSPACE IF NOT EXISTS meetup 
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

CREATE TABLE IF NOT EXISTS meetup.world_stat_by_date (
mdate date,
group_country text,
group_city text,
total int,
event_id text,
event_name text,
event_time timestamp,
venue_name text,
count int,
event_url text,
lon float,
lat float,
topics set<text>,
PRIMARY KEY ((mdate), group_country, total, event_id))
WITH CLUSTERING ORDER BY (group_country ASC, total DESC);


CREATE TABLE IF NOT EXISTS meetup.us_stat_by_date (
mdate date,
group_state text,
total int,
group_city text,
event_name text,
event_id text,
event_time timestamp,
venue_name text,
count int,
event_url text,
lon float,
lat float,
topics set<text>,
PRIMARY KEY ((mdate), group_state, total, event_id))
WITH CLUSTERING ORDER BY (group_state ASC, total DESC);

CREATE TABLE IF NOT EXISTS meetup.similar_people (
name_a text,
id_a bigint,
name_b text,
id_b bigint,
distance double,
PRIMARY KEY ((name_a), id_a, distance, name_b, id_b));