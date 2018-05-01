package com.khwu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Reservation implements Serializable {
    private Event event;
    private Group group;
    private Venue venue;
    private String visibility;
    private String response;
    private int guests;
    private Member member;
    @JsonProperty("rsvp_id")
    private long rsvpID;
    private long mtime;
}
