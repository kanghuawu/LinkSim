# Meetup Streaming Analysis

## Introduction

* Who should I be networking when attending a specific meetup?
* What are trending meetups in each country or state?

## API & Libraries

* meetup api
  * [raw data](http://stream.meetup.com/2/rsvps) can be curl as show below
  * data render in [browser](http://meetup.github.io/stream/rsvpTicker/)

```shell
$ unbuffer curl -s -L http://stream.meetup.com/2/rsvps | jsonpp
{
  "venue": {
    "venue_name": "West Des Moines Public Library",
    "lon": -93.759647,
    "lat": 41.564202,
    "venue_id": 746276
  },
  "visibility": "public",
  "response": "yes",
  "guests": 0,
  "member": {
    "member_id": 180958822,
    "member_name": "Michelle"
  },
  "rsvp_id": 1723590801,
  "mtime": 1523932806644,
  "event": {
    "event_name": "Critical Bible Study with Randy Henderson",
    "event_id": "249388268",
    "time": 1524697200000,
    "event_url": "https:\/\/www.meetup.com\/Iowa-Atheists-and-Freethinkers\/events\/249388268\/"
  },
  "group": {
    "group_topics": [
      {
        "urlkey": "atheist-outreach",
        "topic_name": "Atheist Outreach"
      },
      {
        "urlkey": "discussing-atheism-skepticism-and-secularism",
        "topic_name": "Discussing atheism skepticism and secularism"
      },
      {
        "urlkey": "secularism",
        "topic_name": "Secularism"
      },
      {
        "urlkey": "atheists",
        "topic_name": "Atheist"
      },
      {
        "urlkey": "agnostic",
        "topic_name": "Agnostic"
      },
      {
        "urlkey": "humanism",
        "topic_name": "Humanism"
      },
      {
        "urlkey": "churchandstate",
        "topic_name": "Separation of Church and State"
      }
    ],
    "group_city": "Des Moines",
    "group_country": "us",
    "group_id": 1066657,
    "group_name": "Iowa Atheists and Freethinkers",
    "group_lon": -93.63,
    "group_urlname": "Iowa-Atheists-and-Freethinkers",
    "group_state": "IA",
    "group_lat": 41.6
  }
}
```
