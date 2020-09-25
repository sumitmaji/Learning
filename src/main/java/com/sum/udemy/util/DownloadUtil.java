package com.sum.udemy.util;

import com.sum.udemy.modal.Course;
import com.sum.udemy.modal.Lecture;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.Request;

import java.io.*;


public class DownloadUtil {

    public static void downloadFile(String url, File mediaFile) {
        Config config = new Config();
        config.setMasterUrl(url);
        config.setOauthToken(SystemPropertyUtil.getOauthToken());
        OkHttpClient client = HttpClientUtils.createHttpClient(config);

        Call call = client.newCall(new Request.Builder().url(url).get().build());

        try {
            Response response = call.execute();
            if (response.code() == 200 || response.code() == 201) {

                InputStream inputStream = null;
                try {
                    inputStream = response.body().byteStream();

                    byte[] buff = new byte[1024 * 4];
                    long downloaded = 0;
                    long target = response.body().contentLength();

                    File temp = new File(String.format("/tmp/%s", String.valueOf(Math.random())));
                    OutputStream output = new FileOutputStream(temp);

                    while (true) {
                        int readed = inputStream.read(buff);

                        if (readed == -1) {
                            break;
                        }
                        output.write(buff, 0, readed);
                        //write buff
                        downloaded += readed;

                    }

                    output.flush();
                    output.close();

                    temp.renameTo(mediaFile);
                    temp.delete();
                } catch (IOException ignore) {
                    ignore.printStackTrace();
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            } else {
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void saveHtml(String content, File mediaFile) {

        try {
            File temp = new File(String.format("/tmp/%s", String.valueOf(Math.random())));
            OutputStream output = new FileOutputStream(temp);
            output.write(content.getBytes());
            output.flush();
            output.close();

            temp.renameTo(mediaFile);
            temp.delete();
        } catch (IOException ignore) {
            ignore.printStackTrace();
        } finally {
        }

    }
}
