package com.example.preventionapp;

import java.io.Serializable;

//데이터 분류 용도
public class NewsData implements Serializable {//Serializable: 데이터를 직렬화

    private String title;
    private String urlToImage;
    private String content; //임의로 바꾸지 못하게 private로 설정
    private String url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
