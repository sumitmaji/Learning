package com.sum.udemy.modal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.io.File;
import java.util.List;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Lecture implements Resource {

    @JsonProperty("_class")
    private String type;

    private Course course;

    @JsonProperty("id")
    private int id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("asset")
    private Asset asset;

    @JsonProperty("supplementary_assets")
    private List<SuppliAsset> suppliAssets;

    @Override
    public void save(File file) {

        if ("chapter".equalsIgnoreCase(type)) {
            File directory = new File(String.format("%s/%d-%s", file.getAbsolutePath(), course.getChapCounter(), title.replaceAll("[^\\w\\s]","-")));
            if (!directory.exists()) {
                directory.mkdirs();
                return;
            }
        } else if ("lecture".equalsIgnoreCase(type)) {

            File parent = file;
            if (suppliAssets != null && suppliAssets.size() > 0) {
                File directory = new File(String.format("%s/%d-%s", file.getAbsolutePath(), course.getCounter(), title.replaceAll("[^\\w\\s]","-")));
                if (!directory.exists()) {
                    directory.mkdirs();

                }
                parent = directory;
            }

            if (asset != null){
                asset.setCourse(course);
                asset.setLecture(this);
                asset.save(parent);
            }


            if (suppliAssets != null && suppliAssets.size() > 0) {
                for (SuppliAsset suppliAsset : suppliAssets) {
                    suppliAsset.setCourse(course);
                    suppliAsset.setLecture(this);
                    suppliAsset.save(parent);
                }
            }
        }

    }
}
