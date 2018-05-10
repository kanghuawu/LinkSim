package com.khwu.model.cassandra;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagByUserId implements Serializable  {
    private Long id;
    private String name;
    private String country;
    private String state;
    private String tag;
}
