package com.derysudrajat.mynews;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class NewsItems {

    @SerializedName("articles")
    private List<News> newsList;

    NewsItems(List<News> newsList) {
        this.newsList = newsList;
    }

    List<News> getNewsList() {
        return newsList;
    }

}