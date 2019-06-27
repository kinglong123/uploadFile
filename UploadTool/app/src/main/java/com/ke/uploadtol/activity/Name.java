package com.ke.uploadtol.activity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by lanjl on 2019/6/21.
 */
public class Name implements Serializable {
    @JsonProperty("count")
    private int          totalCount;

    @JsonProperty("name")
    private String          name;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
