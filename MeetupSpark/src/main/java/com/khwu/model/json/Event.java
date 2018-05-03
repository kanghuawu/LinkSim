package com.khwu.model.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Event implements Serializable {
    @JsonProperty("event_id")
    private String eventId;
    @JsonProperty("event_name")
    private String eventName;
    @JsonProperty("event_url")
    private String eventUrl;
    private Long time;
}
