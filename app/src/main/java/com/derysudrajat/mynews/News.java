package com.derysudrajat.mynews;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class News implements Parcelable {
    @SerializedName("title")
    private String title;
    @SerializedName("source")
    private Source source;
    @SerializedName("urlToImage")
    private String img;
    @SerializedName("url")
    private String url;


    String getTitle() {
        return title;
    }

    Source getSource() {
        return source;
    }

    String getImg() {
        return img;
    }

    String getUrl() {
        return url;
    }

    public static class Source implements Parcelable {
        @SerializedName("name")
        private String name;

        public static final Creator<Source> CREATOR = new Creator<Source>() {
            @Override
            public Source createFromParcel(Parcel in) {
                return new Source(in);
            }

            @Override
            public Source[] newArray(int size) {
                return new Source[size];
            }
        };

        String getName() {
            return name;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.name);
        }

        Source(Parcel in) {
            this.name = in.readString();
        }

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeParcelable(this.source, flags);
        dest.writeString(this.img);
        dest.writeString(this.url);
    }

    private News(Parcel in) {
        this.title = in.readString();
        this.source = in.readParcelable(Source.class.getClassLoader());
        this.img = in.readString();
        this.url = in.readString();
    }

    public static final Parcelable.Creator<News> CREATOR = new Parcelable.Creator<News>() {
        @Override
        public News createFromParcel(Parcel source) {
            return new News(source);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };
}