package com.example.preventionapp;

public class BoardContentsListItem {
    private String title;
    private String nickname;
    private String date;
    private String contents;
    private long replyNum;
    private long recommendNum;

    public BoardContentsListItem(String title, String nickname, String date, String contents, Long replyNum, Long recommendNum) {
        this.title = title;
        this.nickname = nickname;
        this.date = date;
        this.contents = contents;
        this.replyNum = replyNum;
        this.recommendNum = recommendNum;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public long getReplyNum() {
        return replyNum;
    }

    public void setReplyNum(int replyNum) {
        this.replyNum = replyNum;
    }

    public long getRecommendNum() {
        return recommendNum;
    }

    public void setRecommendNum(int recommendNum) {
        this.recommendNum = recommendNum;
    }

}
