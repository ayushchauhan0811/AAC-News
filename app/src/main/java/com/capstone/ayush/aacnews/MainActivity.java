package com.capstone.ayush.aacnews;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.capstone.ayush.aacnews.adapter.SourcesAdapter;
import com.capstone.ayush.aacnews.data.NewsContract;
import com.facebook.stetho.Stetho;

public class MainActivity extends AppCompatActivity {

    public static String apiKey = "13b146acfa6b4a24826e82d65c2ac92e";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Stetho.initializeWithDefaults(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService();
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
}
