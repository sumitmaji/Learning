package com.sum.udemy.modal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Course implements  Resource{

    public int counter = 0;

    private int chapCounter = 0;
    @JsonProperty("count")
    private int count;

    @JsonProperty("id")
    private int id;

    private String title;

    @JsonProperty("results")
    private List<Lecture> lectures;


    @Override
    public void save(File file) {
        File directory = new File(String.format("%s/%s", file.getAbsolutePath(), title.replaceAll("[^\\w\\s]","-")));

        if(!directory.exists()){
            directory.mkdirs();
        }


        File chapter = file;
        for(Lecture lecture: lectures){
            lecture.setCourse(this);
            if("chapter".equalsIgnoreCase(lecture.getType())){
                if(chapter != null){
                    //Delete if directory is empty
                    if(chapter.isDirectory()){
                        List<File> files = Arrays.asList(chapter.listFiles());
                        if(files.size() == 0){
                            chapter.delete();
                        }
                    }
                }
                chapter = new File(String.format("%s/%d-%s", directory.getAbsolutePath(), chapCounter, lecture.getTitle().replaceAll("[^\\w\\s]","-")));
                lecture.save(directory);
                chapCounter++;
            }else{
                lecture.save(chapter);
                counter++;
            }
        }
    }
}
