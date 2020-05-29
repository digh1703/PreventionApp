package com.example.preventionapp;

import com.google.firebase.Timestamp;

public class ReplyContentsListItem {
    private String nickname;
    private Timestamp date;
    private String contents;
    private long recommendNum;

    public ReplyContentsListItem(String nickname, Timestamp date, String contents, long recommendNum) {
        this.nickname = nickname;
        this.date = date;
        this.contents = contents;
        this.recommendNum = recommendNum;
    }

    public String getNickname() {
        return nickname;
    }

    public Timestamp getDate() {
        return date;
    }

    public String getContents() {
        return contents;
    }

    public long getRecommendNum() {
        return recommendNum;
    }
}
