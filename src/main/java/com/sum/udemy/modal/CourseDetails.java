package com.sum.udemy.modal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@ToString
public class CourseDetails {

    @JsonProperty("id")
    private int id;

    @JsonProperty("title")
    private String title;
}
