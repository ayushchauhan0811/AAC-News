package com.capstone.ayush.aacnews;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.capstone.ayush.aacnews.sync.NewsSyncAdapter;

public class NewsActivity extends AppCompatActivity implements NewsFragment.Callback{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new NewsFragment())
                    .commit();
        }
        NewsSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public void onItemSelected(Uri newsUri) {
        Intent intent = new Intent(this, DetailActivity.class)
                .setData(newsUri);
        startActivity(intent);
    }
}
