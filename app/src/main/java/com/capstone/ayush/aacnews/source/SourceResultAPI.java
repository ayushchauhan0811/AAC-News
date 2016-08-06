package com.capstone.ayush.aacnews.source;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Ayush on 05-08-2016.
 */
public interface SourceResultAPI {
    String ENDPOINT = " https://newsapi.org/v1/sources";

    @GET("/sources")
    Call<SourceResult> getSources();
}
