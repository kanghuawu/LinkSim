package com.cmpe280.meetup.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cassandra.core.Ordering;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;


import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Table(value = "world_stat_by_date")
@AllArgsConstructor
@NoArgsConstructor
public class World implements Serializable {

    @PrimaryKeyColumn(name = "mdate", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private LocalDate mdate;

    @PrimaryKeyColumn(name = "group_country", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private String groupCountry;

    @PrimaryKeyColumn(name = "total", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private int total;

    @PrimaryKeyColumn(name = "event_id", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
    private String eventID;

    @Column("group_city")
    private String groupCity;

    @Column("event_name")
    private String eventName;

    @Column("event_time")
    private LocalDateTime eventTime;

    @Column("venue_name")
    private String venueName;

    @Column("count")
    private int count;

    @Column("event_url")
    private String eventUrl;

    @Column("lon")
    private Float lon;

    @Column("lat")
    private Float lat;

    @Column("topics")
    private Set<String> topics;
}

/*
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
WITH CLUSTERING ORDER BY (count DESC);
 */