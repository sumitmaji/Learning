package com.sum.udemy.modal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubscribedCourses {


    @JsonProperty("count")
    private int count;

    @JsonProperty
    private String next;

    @JsonProperty("previous")
    private String previous;

    @JsonProperty("results")
    private List<CourseDetails> courseDetails;

}


