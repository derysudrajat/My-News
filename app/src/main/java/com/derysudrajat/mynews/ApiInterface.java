package com.derysudrajat.mynews;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

interface ApiInterface {
    @GET("everything")
    Call<NewsItems> getNews(@Query("q") String search, @Query("apiKey") String apiKey);

    @GET("top-headlines")
    Call<NewsItems> getNewsHeadlines(@Query("country") String country, @Query("category") String category, @Query("apiKey") String apiKey);
}