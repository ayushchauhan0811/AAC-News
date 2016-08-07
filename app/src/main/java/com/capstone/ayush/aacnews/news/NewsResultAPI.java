package com.capstone.ayush.aacnews.news;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Ayush on 06-08-2016.
 */
public interface NewsResultAPI {
    String ENDPOINT = "https://newsapi.org";

    @GET("/v1/articles?")
    Call<NewsResult> getNews(@Query("source") String source,
                             @Query("sortBy") String sortBy,
                             @Query("apiKey") String apiKey);


}
