package com.example.tomaszkot.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Review model
 */

public class Review implements Parcelable {
    private String id;
    private String content;
    private String author;
    private String title;

    public Review(Parcel in) {
        id = in.readString();
        author = in.readString();
        content = in.readString();
        title = in.readString();
    }

    public Review(){

    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(author);
        dest.writeString(content);
        dest.writeString(title);
    }

    public String getId() {
        return id;
    }
    public void setId (String id){
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }
    public void setAuthor (String author){
        this.author = author;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content){
        this.content = content;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title){
        this.title = title;
    }
}
