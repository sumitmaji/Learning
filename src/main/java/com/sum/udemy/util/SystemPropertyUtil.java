package com.sum.udemy.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SystemPropertyUtil {

    private static InputStream inputStream;
    private static Properties props;
    static{
        System.out.println("its not initialized");
        inputStream = SystemPropertyUtil.class.getResourceAsStream("/.env");
        props = new Properties();
        try {
            props.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getOauthToken(){
        return (String) props.get("auth.oauth");
    }

    public static String getUrl(){
        return (String)props.get("auth.url");
    }
}
