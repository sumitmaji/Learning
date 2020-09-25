package com.sum.udemy.modal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@ToString
public class DownloadUrl {

    @JsonProperty("File")
    private List<UdemyFile> files;
}
