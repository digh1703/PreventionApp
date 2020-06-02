package com.example.preventionapp;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public class BoardContentsListItem implements Parcelable {

    private String title;
    private String nickname;
    private Timestamp date;
    private String contents;
    private long replyNum;
    private long recommendNum;

    public BoardContentsListItem(String title, String nickname, Timestamp date, String contents, Long replyNum, Long recommendNum) {
        this.title = title;
        this.nickname = nickname;
        this.date = date;
        this.contents = contents;
        this.replyNum = replyNum;
        this.recommendNum = recommendNum;
    }

    protected BoardContentsListItem(Parcel in) {
        title = in.readString();
        nickname = in.readString();
        date = in.readParcelable(Timestamp.class.getClassLoader());
        contents = in.readString();
        replyNum = in.readLong();
        recommendNum = in.readLong();
    }

    public static final Creator<BoardContentsListItem> CREATOR = new Creator<BoardContentsListItem>() {
        @Override
        public BoardContentsListItem createFromParcel(Parcel in) {
            return new BoardContentsListItem(in);
        }

        @Override
        public BoardContentsListItem[] newArray(int size) {
            return new BoardContentsListItem[size];
        }
    };

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

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
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



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(nickname);
        dest.writeParcelable(date, flags);
        dest.writeString(contents);
        dest.writeLong(replyNum);
        dest.writeLong(recommendNum);
    }
}
