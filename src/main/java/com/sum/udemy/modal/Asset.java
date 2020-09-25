package com.sum.udemy.modal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sum.udemy.util.DownloadUtil;
import com.sum.udemy.util.OperationSupport;
import com.sum.udemy.util.Serialization;
import com.sum.udemy.util.SystemPropertyUtil;
import lombok.Data;
import lombok.ToString;

import java.io.File;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Asset implements Resource {

    private static String VIDEO = "https://"+ SystemPropertyUtil.getUrl()+"/api-2.0/users/me/subscribed-courses/%d/lectures/" +
            "%d/?fields[asset]=stream_urls";
    private static String ARTICLE = "https://"+SystemPropertyUtil.getUrl()+"/api-2.0/assets/%d/?fields[asset]=body";
    @JsonProperty("id")
    private int id;

    private Lecture lecture;
    private Course course;

    @JsonProperty("asset_type")
    private String type;

    @JsonProperty("title")
    private String title;

    @JsonProperty("stream_urls")
    private StreamUrl streamUrl;


    @Override
    public void save(File file) {
        System.out.println("Saving file: " + file.getAbsolutePath() + "/" + title);
        if ("Video".equalsIgnoreCase(type)) {
            String url = String.format(VIDEO, course.getId(), lecture.getId());
            try {
                Lecture videoLecture = (new OperationSupport<Lecture>()).getData(url, Lecture.class);
                System.out.println(Serialization.prettyPrint(videoLecture));
                if (videoLecture.getAsset() != null && videoLecture.getAsset().getStreamUrl() != null) {
                    for (Video video : videoLecture.getAsset().getStreamUrl().getVideos()) {
                        File videoFile = new File(String.format("%s/%d-%s.mp4", file.getAbsolutePath(), course.getCounter(), videoLecture.getTitle().replaceAll("[^\\w\\s]","-")));
                        if (!videoFile.exists())
                            DownloadUtil.downloadFile(video.getFile(), videoFile);
                        break;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("Article".equalsIgnoreCase(type)) {
            String url = String.format(ARTICLE, this.getId());

            try {
                Article article = new OperationSupport<Article>().getData(url, Article.class);
                System.out.println(Serialization.prettyPrint(article));
                File aritcleFile = new File(String.format("%s/%d-%s.html", file.getAbsolutePath(), course.getCounter(), lecture.getTitle().replaceAll("[^\\w\\s.]","-")));
                if (!aritcleFile.exists())
                    DownloadUtil.saveHtml(article.getBody(), aritcleFile);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
