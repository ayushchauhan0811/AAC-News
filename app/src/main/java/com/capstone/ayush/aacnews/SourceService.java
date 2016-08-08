package com.capstone.ayush.aacnews;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.capstone.ayush.aacnews.data.NewsContract;
import com.capstone.ayush.aacnews.source.Source;
import com.capstone.ayush.aacnews.source.SourceResult;
import com.capstone.ayush.aacnews.source.SourceResultAPI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.Vector;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Ayush on 05-08-2016.
 */
public class SourceService extends IntentService implements Callback<SourceResult>{

    private Gson gson;
    private Retrofit retrofit;
    private String LOG_TAG = SourceService.class.getSimpleName();


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     **/
    public SourceService() {
        super(SourceService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl(SourceResultAPI.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        SourceResultAPI sourceResultAPI = retrofit.create(SourceResultAPI.class);
        Call<SourceResult> call = sourceResultAPI.getSources();
        //asynchronous call
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<SourceResult> call, Response<SourceResult> response) {
        int code = response.code();
        if(code == 200){
            SourceResult sourceResult = response.body();
            getSource(sourceResult);
        }
        else {
            Toast.makeText(this, "Did not work: " + String.valueOf(code), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFailure(Call<SourceResult> call, Throwable t) {
        Toast.makeText(this, "Nope", Toast.LENGTH_LONG).show();
        Log.e("Throwable ",t.toString());
    }

    void getSource(SourceResult sourceResult){
        List<Source> sources = sourceResult.getSources();
        Vector<ContentValues> cVVector = new Vector<ContentValues>();
        for(int i=0;i<sources.size();i++){
            String id = sources.get(i).getId();
            String name = sources.get(i).getName();
            String description = sources.get(i).getDescription();
            String url = sources.get(i).getUrl();
            String logoUrl = sources.get(i).getUrlsToLogos().getSmall();
            String order = "";
            List<String> sortByOrder = sources.get(i).getSortBysAvailable();
            for(int j=0;j<sortByOrder.size();j++){
                order=order + sortByOrder.get(j) + ",";
            }
            ContentValues sourceValue = new ContentValues();
            sourceValue.put(NewsContract.SourceEntry.COLUMN_SOURCE_ID,id);
            sourceValue.put(NewsContract.SourceEntry.COLUMN_NAME,name);
            sourceValue.put(NewsContract.SourceEntry.COLUMN_DESCRIPTION,description);
            sourceValue.put(NewsContract.SourceEntry.COLUMN_URL,url);
            sourceValue.put(NewsContract.SourceEntry.COLUMN_LOGO_URL,logoUrl);
            sourceValue.put(NewsContract.SourceEntry.COLUMN_SORT_BY,order);

            cVVector.add(sourceValue);
        }
        int inserted = 0;
        // add to database
        if ( cVVector.size() > 0 ) {
            // Student: call bulkInsert to add the weatherEntries to the database here
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            Cursor c = this.getContentResolver().query(
                    NewsContract.SourceEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );
            if (c != null) {
                this.getContentResolver().delete(
                        NewsContract.SourceEntry.CONTENT_URI,
                        null,
                        null
                );
                c.close();
            }
            inserted = this.getContentResolver().bulkInsert(
                    NewsContract.SourceEntry.CONTENT_URI,
                    cvArray
            );
        }
    }
}
