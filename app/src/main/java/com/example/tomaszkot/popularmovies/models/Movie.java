package com.example.tomaszkot.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Movie data
 */

public class Movie implements Parcelable {

    private String TITLE_KEY = "original_title";
    private String RELEASE_KEY = "release_date";
    private String POSTER_KEY = "poster_path";
    private String VOTE_KEY = "vote_average";
    private String PLOT_KEY = "overview";
    private int ID_KEY;

    public Movie(){
    }

    // ---------------------------------------------------------------------- Constructor for Parcel
    private Movie(Parcel in){
        TITLE_KEY = in.readString();
        RELEASE_KEY = in.readString();
        POSTER_KEY = in.readString();
        VOTE_KEY = in.readString();
        PLOT_KEY = in.readString();
        ID_KEY = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // ----------------------------------------------------------------------------- Write to Parcel
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(TITLE_KEY);
        dest.writeString(RELEASE_KEY);
        dest.writeString(POSTER_KEY);
        dest.writeString(VOTE_KEY);
        dest.writeString(PLOT_KEY);
        dest.writeInt(ID_KEY);

    }

    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getTitle() {
        return TITLE_KEY;
    }
    public void setTitle (String title){
        this.TITLE_KEY = title;
    }

    public String getReleaseDate() {
        return RELEASE_KEY;
    }
    public void setReleaseDate(String releaseDate){
        this.RELEASE_KEY = releaseDate;
    }

    public String getPosterPath() {
        return POSTER_KEY;
    }
    public void setPosterPath (String posterPath){
        this.POSTER_KEY = posterPath;
    }

    public String getVoteAverage() {
        return VOTE_KEY;
    }
    public void setVoteAverage (String voteAverage){
        this.VOTE_KEY = voteAverage;
    }

    public String getPlotSynopsis() {
        return PLOT_KEY;
    }
    public void setPlotSynopsis (String plotSynopsis){
        this.PLOT_KEY = plotSynopsis;
    }

    public int getID(){
        return ID_KEY;
    }
    public void setID (int id){
        this.ID_KEY = id;
    }
}
