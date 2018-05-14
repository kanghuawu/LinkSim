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
name text STATIC,
country text STATIC,
state text STATIC,
tag text,
PRIMARY KEY ((id), tag));

CREATE TABLE IF NOT EXISTS meetup.user_location (
id bigint,
lat float,
lon float,
PRIMARY KEY (id));

CREATE TABLE IF NOT EXISTS meetup.tag_count (
tag text,
count bigint,
PRIMARY KEY (tag);