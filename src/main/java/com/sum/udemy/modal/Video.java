package com.sum.udemy.modal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Video {
    @JsonProperty("label")
    private String label;

    @JsonProperty("file")
    private String file;

    @JsonProperty("type")
    private String type;
}
