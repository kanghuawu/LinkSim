# Meetup Streaming Analysis

## API & Libraries

* meetup api
  * [raw data](http://stream.meetup.com/2/rsvps) can be curl as show below
  * data render in [browser](http://meetup.github.io/stream/rsvpTicker/)

* [SparkStream-for-meetup](https://github.com/gautham20/SparkStream-for-meetup)

```shell
$ curl -s -L http://stream.meetup.com/2/rsvps | jsonpp

{
  "venue": {
    "venue_name": "Please Register via the Link Below To Receive the Address",
    "lon": 0,
    "lat": 0,
    "venue_id": 25603330
  },
  # skip details ...
    "group_city": "Del Mar",
    "group_country": "us",
    "group_id": 1711159,
    "group_name": "San Diego Investment Club",
    "group_lon": -117.23,
    "group_urlname": "San-Diego-Investment-Club-SDIC",
    "group_state": "CA",
    "group_lat": 32.96
  }
}
{
  "venue": {
    "venue_name": "Bar Italia",
    "lon": 151.156815,
    "lat": -33.881168,
    "venue_id": 1734521
  },
  # skip details ...
    "group_city": "Sydney",
    "group_country": "au",
    "group_id": 26510493,
    "group_name": "Sunday coffee  50's +",
    "group_lon": 151.21,
    "group_urlname": "Sunday-coffee-50s",
    "group_lat": -33.87
  }
}
# new incoming stream reservations...
```
