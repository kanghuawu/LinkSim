package com.khwu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Venue implements Serializable {
    @JsonProperty("venue_name")
    private String venueName;
    private Float lon;
    private Float lat;
    @JsonProperty("venue_id")
    private Long venueId;
}
