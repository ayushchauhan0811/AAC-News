package com.capstone.ayush.aacnews.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.util.Log;
import android.widget.Toast;

import com.capstone.ayush.aacnews.MainActivity;
import com.capstone.ayush.aacnews.R;
import com.capstone.ayush.aacnews.Utility;
import com.capstone.ayush.aacnews.data.NewsContract;
import com.capstone.ayush.aacnews.news.Articles;
import com.capstone.ayush.aacnews.news.NewsResult;
import com.capstone.ayush.aacnews.news.NewsResultAPI;
import com.capstone.ayush.aacnews.source.SourceResult;
import com.capstone.ayush.aacnews.source.SourceResultAPI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Vector;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewsSyncAdapter extends AbstractThreadedSyncAdapter implements Callback<NewsResult> {
    public final String LOG_TAG = NewsSyncAdapter.class.getSimpleName();
    // Interval at which to sync with the weather, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 60;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    public static final String ACTION_DATA_UPDATED = "com.capstone.ayush.aacnews.ACTION_DATA_UPDATED";

    private String source,sortBy;

    private Gson gson;
    private Retrofit retrofit;

    public NewsSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onResponse(Call<NewsResult> call, Response<NewsResult> response) {
        int code = response.code();
        if(code == 200){
            NewsResult newsResult = response.body();
            getNews(newsResult);
        } else{
            Log.e("Response",response.message());
            Toast.makeText(getContext(), "Did not work: " + String.valueOf(code), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFailure(Call<NewsResult> call, Throwable t) {
        Toast.makeText(getContext(), "Nope", Toast.LENGTH_LONG).show();
        Log.e("Throwable ",t.toString());
    }


    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl(NewsResultAPI.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        source = Utility.getSource(getContext());
        sortBy = Utility.getSortBy(getContext());

        NewsResultAPI newsResultAPI = retrofit.create(NewsResultAPI.class);
        Call<NewsResult> call = newsResultAPI.getNews(source, sortBy, MainActivity.apiKey);
        //asynchronous call
        //Log.e(LOG_TAG, "call = " + call.request().url().toString());
        call.enqueue(this);

        return;
    }


    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        NewsSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    void getNews(NewsResult newsResult){
        List<Articles> articlesList = newsResult.getArticles();
        Vector<ContentValues> cVVector = new Vector<ContentValues>();

        for(int i=0;i<articlesList.size();i++){
            String author = articlesList.get(i).getAuthor();
            String description = articlesList.get(i).getDescription();
            String title = articlesList.get(i).getTitle();
            String url = articlesList.get(i).getUrl();
            String imageUrl = articlesList.get(i).getUrlToImage();
            String publishedAt = articlesList.get(i).getPublishedAt();

            if(publishedAt!=null)
                publishedAt = publishedAt.substring(0,10);

            ContentValues contentValues = new ContentValues();
            contentValues.put(NewsContract.NewsEntry.COLUMN_SOURCE,source);
            contentValues.put(NewsContract.NewsEntry.COLUMN_AUTHOR,author);
            contentValues.put(NewsContract.NewsEntry.COLUMN_DESCRIPTION,description);
            contentValues.put(NewsContract.NewsEntry.COLUMN_TITLE,title);
            contentValues.put(NewsContract.NewsEntry.COLUMN_URL,url);
            contentValues.put(NewsContract.NewsEntry.COLUMN_IMAGE_URL,imageUrl);
            contentValues.put(NewsContract.NewsEntry.COLUMN_PUBLISHED_AT,publishedAt);

            cVVector.add(contentValues);
        }

        int inserted = 0;
        // add to database
        if ( cVVector.size() > 0 ) {
            // Student: call bulkInsert to add the weatherEntries to the database here
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            Cursor c = getContext().getContentResolver().query(
                    NewsContract.NewsEntry.CONTENT_URI,
                    null,
                    NewsContract.NewsEntry.COLUMN_SOURCE + " = ? ",
                    new String[]{source},
                    null
            );
            if (c != null) {
                getContext().getContentResolver().delete(
                        NewsContract.NewsEntry.CONTENT_URI,
                        NewsContract.NewsEntry.COLUMN_SOURCE + " = ? ",
                        new String[]{source}
                );
                c.close();
            }
            inserted = getContext().getContentResolver().bulkInsert(
                    NewsContract.NewsEntry.CONTENT_URI,
                    cvArray
            );
            updateWidgets();
        }
    }

    private void updateWidgets() {
        Context context = getContext();
        // Setting the package ensures that only components in our app will receive the broadcast
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED)
                .setPackage(context.getPackageName());
        context.sendBroadcast(dataUpdatedIntent);
    }

}