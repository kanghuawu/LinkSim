package com.khwu.model.cassandra;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLocation {
    private Long id;
    private Float lat;
    private Float lon;

    public static final  String USER_LOCATION = "user_location";
}
