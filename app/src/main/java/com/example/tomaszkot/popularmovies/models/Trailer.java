package com.example.tomaszkot.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Trailer model
 */

public class Trailer implements Parcelable {
    private String ID = "id";
    private String ISO639 = "iso_639_1";
    private String ISO3166 = "iso_3166_1";
    private String KEY = "key";
    private String NAME = "name";
    private String SITE = "site";
    private String SIZE = "size";
    private String TYPE = "type";

    public Trailer(){
    }


    // ---------------------------------------------------------------------- Constructor for Parcel
    private Trailer(Parcel in) {
        ID = in.readString();
        ISO639 = in.readString();
        ISO3166 = in.readString();
        KEY = in.readString();
        NAME = in.readString();
        SITE = in.readString();
        SIZE = in.readString();
        TYPE = in.readString();
    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    // ----------------------------------------------------------------------------- Write to Parcel
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ID);
        dest.writeString(ISO639);
        dest.writeString(ISO3166);
        dest.writeString(KEY);
        dest.writeString(NAME);
        dest.writeString(SITE);
        dest.writeString(SIZE);
        dest.writeString(TYPE);
    }

    public String getId() {
        return ID;
    }
    public void setId (String id){
        this.ID = id;
    }

    @SuppressWarnings("unused")
    public String getISO639() {
        return ISO639;
    }
    @SuppressWarnings("unused")
    public void setISO639 (String ISO639){
        this.ISO639 = ISO639;
    }

    @SuppressWarnings("unused")
    public String getISO3166() {
        return ISO3166;
    }
    @SuppressWarnings("unused")
    public void setISO3166 (String ISO3166){
        this.ISO3166 = ISO3166;
    }

    public String getKey() {
        return KEY;
    }
    public void setKey (String key){
        this.KEY = key;
    }

    public String getName() {
        return NAME;
    }
    public void setName (String name){
        this.NAME = name;
    }

    @SuppressWarnings("unused")
    public String getSite() {
        return SITE;
    }
    public void setSite (String site){
        this.SITE = site;
    }

    @SuppressWarnings("unused")
    public String getSize() {
        return SIZE;
    }
    @SuppressWarnings("unused")
    public void setSize (String size){
        this.SIZE = size;
    }

    @SuppressWarnings("unused")
    public String getType() {
        return TYPE;
    }
    public void setTYPE (String type){
        this.TYPE = type;
    }
}
