package com.khwu.model.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Member implements Serializable {
    @JsonProperty("member_id")
    private Long memberId;
    private String photo;
    @JsonProperty("member_name")
    private String memberName;
    @JsonProperty("other_services")
    private Map<String, Map<String, String>> otherServices;
}
