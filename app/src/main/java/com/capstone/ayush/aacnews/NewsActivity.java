package com.capstone.ayush.aacnews;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.capstone.ayush.aacnews.sync.NewsSyncAdapter;

public class NewsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Bundle bundle = getIntent().getExtras();
        if (savedInstanceState == null) {
            NewsFragment newsFragment = new NewsFragment();
            newsFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, newsFragment)
                    .commit();
        }
        NewsSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.news_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_top){

        } else if(id == R.id.action_popular){

        } else if(id == R.id.action_latest){

        }
        return super.onOptionsItemSelected(item);
    }
}
