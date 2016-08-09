package com.capstone.ayush.aacnews;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.capstone.ayush.aacnews.adapter.SourcesAdapter;
import com.capstone.ayush.aacnews.data.NewsContract;
import com.facebook.stetho.Stetho;

public class MainActivity extends AppCompatActivity {

    public static String apiKey = "13b146acfa6b4a24826e82d65c2ac92e";
    public static boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Stetho.initializeWithDefaults(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((NewsApplication)getApplication()).startTracking();

        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected){
            startService();
        } else{
            networkToast();
        }
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new SourceFragment())
                    .commit();
        }
    }
    void startService(){
        Intent intent = new Intent(this, SourceService.class);
        startService(intent);
    }
    public void networkToast(){
        Toast.makeText(this, getString(R.string.network_toast), Toast.LENGTH_LONG).show();
    }
}
