package com.sum.udemy.util;

public class Config {
    private String username;
    private String password;
    private String oauthToken;
    private String masterUrl;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOauthToken() {
        return oauthToken;
    }

    public void setOauthToken(String oauthToken) {
        this.oauthToken = oauthToken;
    }

    public void setMasterUrl(String url) {
        this.masterUrl = url;
    }

    public String getMasterUrl() {
        return masterUrl;
    }
}
