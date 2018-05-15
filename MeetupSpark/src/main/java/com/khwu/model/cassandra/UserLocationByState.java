package com.khwu.model.cassandra;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserLocationByState {
    private String state;
    private Long id;
    private Float lat;
    private Float lon;
}
