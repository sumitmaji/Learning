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

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@ToString
public class SuppliAsset implements Resource {

    private static String DOCUMENT = "https://"+ SystemPropertyUtil.getUrl()+"/api-2.0/users/me/subscribed-courses/%d/lectures/" +
            "%d/supplementary-assets/%d/?fields[asset]=download_urls";
    private static String EXTERNAL_URL = "https://"+SystemPropertyUtil.getUrl()+"/api-2.0/users/me/subscribed-courses/%d/" +
            "lectures/%d/supplementary-assets/%d/?fields[asset]=external_url";
    @JsonProperty("id")
    private int id;

    private Course course;
    private Lecture lecture;
    @JsonProperty("asset_type")
    private String type;

    @JsonProperty("title")
    private String title;

    @JsonProperty("filename")
    private String fileName;

    @JsonProperty("download_urls")
    private DownloadUrl downloadUrl;

    @Override
    public void save(File file) {
        if ("File".equalsIgnoreCase(type)) {

            String url = String.format(DOCUMENT, course.getId(), lecture.getId(), this.getId());
            try {
                SuppliAsset asset = new OperationSupport<SuppliAsset>().getData(url, SuppliAsset.class);
                System.out.println(Serialization.prettyPrint(asset));
                if (asset.getDownloadUrl() != null && asset.getDownloadUrl().getFiles() != null)
                    for (UdemyFile uFile : asset.getDownloadUrl().getFiles()) {
                        File tempFile = new File(String.format("%s/%s", file.getAbsolutePath(), this.getFileName().replaceAll("[^\\w\\s.]","-")));
                        if (!tempFile.exists())
                            DownloadUtil.downloadFile(uFile.getFile(), tempFile);
                    }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("ExternalLink".equalsIgnoreCase(type)) {

            String url = String.format(EXTERNAL_URL, course.getId(), lecture.getId(), this.getId());
            try {
                ExternalUrl externalUrl = (new OperationSupport<ExternalUrl>().getData(url, ExternalUrl.class));
                File tempFile = new File(String.format("%s/%d.txt", file.getAbsolutePath(), this.getId()));
                if (!tempFile.exists())
                    DownloadUtil.saveHtml(externalUrl.getUrl(), tempFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
