CREATE KEYSPACE IF NOT EXISTS meetup_analysis 
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 3};

CREATE TABLE IF NOT EXISTS meetup_analysis.world_stat_by_date (
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


CREATE TABLE IF NOT EXISTS meetup_analysis.us_stat_by_date (
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

CREATE TABLE IF NOT EXISTS meetup_analysis.similar_people (
idA int,
nameA text,
idB int,
nameB text,
distance double,
PRIMARY KEY ((idA), distance, nameB))
WITH CLUSTERING ORDER BY (distance ASC, nameB DESC);