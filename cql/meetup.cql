CREATE KEYSPACE IF NOT EXISTS meetup 
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

CREATE TABLE IF NOT EXISTS meetup.user_by_name (
name text,
id bigint,
PRIMARY KEY ((name), id));

CREATE TABLE IF NOT EXISTS meetup.similar_people (
id_a bigint,
name_a text STATIC,
urlkey_a list<text> STATIC,
id_b bigint,
name_b text,
country_b text,
state_b text,
urlkey_b list<text>,
distance double,
PRIMARY KEY ((id_a), country_b, state_b, distance, id_b));

CREATE TABLE IF NOT EXISTS meetup.tag_by_userid (
id bigint,
tag text,
PRIMARY KEY ((id), tag));

CREATE TABLE IF NOT EXISTS meetup.user (
id bigint,
mtime bigint,
event_id text,
event_name text,
venue_name text,
response text,
tag text,
PRIMARY KEY ((id), ))
WITH CLUSTERING ();

CREATE TABLE IF NOT EXISTS meetup.geo_user (
geo_id bigint,
id bigint,
PRIMARY KEY ((geo_id), tag));

CREATE TABLE IF NOT EXISTS meetup.tag_count (
tag text,
count bigint,
PRIMARY KEY (tag);