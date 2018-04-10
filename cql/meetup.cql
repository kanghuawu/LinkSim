CREATE KEYSPACE IF NOT EXISTS meetup_analysis 
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

CREATE TABLE IF NOT EXISTS meetup_analysis.world_stat_by_date (
mdate date,
group_country text,
group_city text,
event_name text,
event_id uuid,
event_time timestamp,
venue_name text,
count int,
event_url text,
PRIMARY KEY ((mdate), group_country, event_id));


CREATE TABLE IF NOT EXISTS meetup_analysis.us_stat_by_date (
mdate date,
group_state text,
group_city text,
event_name text,
event_id uuid,
event_time timestamp,
venue_name text,
count int,
event_url text,
PRIMARY KEY ((mdate), group_state, event_id));