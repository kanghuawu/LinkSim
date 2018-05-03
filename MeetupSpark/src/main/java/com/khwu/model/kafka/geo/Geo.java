package com.khwu.model.kafka.geo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Geo {
    private Long memeberId;
    private Float lon;
    private Float lat;
}
