package com.khwu.model.cassandra;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserByName implements Serializable {
    private String name;
    private Long id;

    public static final  String USER_BY_NAME = "user_by_name";
}
