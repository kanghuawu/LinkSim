package com.khwu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Group implements Serializable {
    @JsonProperty("group_city")
    private String groupCity;
    @JsonProperty("group_country")
    private String groupCountry;
    @JsonProperty("group_id")
    private Long groupId;
    @JsonProperty("group_name")
    private String groupName;
    @JsonProperty("group_lon")
    private Float groupLon;
    @JsonProperty("group_lat")
    private Float groupLat;
    @JsonProperty("group_urlname")
    private String groupUrlname;
    @JsonProperty("group_state")
    private String groupState;
    @JsonProperty("group_topics")
    private List<Topic> groupTopics;
}
