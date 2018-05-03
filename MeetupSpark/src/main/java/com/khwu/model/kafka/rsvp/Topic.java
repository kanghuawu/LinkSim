package com.khwu.model.kafka.rsvp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Topic implements Serializable {
    @JsonProperty("urlkey")
    private String urlKey;
    @JsonProperty("topic_name")
    private String topicName;
}
