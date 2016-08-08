package com.capstone.ayush.aacnews;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.capstone.ayush.aacnews.adapter.NewsAdapter;
import com.capstone.ayush.aacnews.data.NewsContract;
import com.capstone.ayush.aacnews.sync.NewsSyncAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private NewsAdapter mAdapter;
    private View rootView;
    private String source;

    private String[] mSortKeys;
    private boolean mTop,mLatest,mPopular;

    private static final int NEWS_LOADER = 0;

    public static final String NEWS_COLUMN[] = {
        NewsContract.NewsEntry._ID,
        NewsContract.NewsEntry.COLUMN_SOURCE,
        NewsContract.NewsEntry.COLUMN_AUTHOR,
        NewsContract.NewsEntry.COLUMN_DESCRIPTION,
        NewsContract.NewsEntry.COLUMN_TITLE,
        NewsContract.NewsEntry.COLUMN_URL,
        NewsContract.NewsEntry.COLUMN_IMAGE_URL,
        NewsContract.NewsEntry.COLUMN_PUBLISHED_AT
    };

    public static final int COL_ID = 0;
    public static final int COL_SOURCE = 1;
    public static final int COL_AUTHOR = 2;
    public static final int COL_DESCRIPTION = 3;
    public static final int COL_TITLE = 4;
    public static final int COL_URL = 5;
    public static final int COL_IMAGE_URL = 6;
    public static final int COL_PUBLISHED_AT = 7;

    public static int mPosition=0;
    private final String POSITION="position";
    private static final String SELECTED_KEY = "selected_position";

    public NewsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(NEWS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_news, container, false);

        mSortKeys = Utility.getOrder(getActivity()).split(",");
        for (String mSortKey : mSortKeys) {
            Log.e("Sort By",mSortKey);
            if (mSortKey.equals(getResources().getString(R.string.top_tag))) {
                mTop = true;
            } else if (mSortKey.equals(getResources().getString(R.string.popular_tag))) {
                mPopular = true;
            } else if (mSortKey.equals(getResources().getString(R.string.latest_tag))) {
                mLatest = true;
            }
        }

        AdView mAdView = (AdView) rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

        source = Utility.getSource(getContext());
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_news);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new NewsAdapter(getActivity(),null);
        mRecyclerView.setAdapter(mAdapter);
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        updateNews();
        return  rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.news_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_top && mTop){
            Utility.setSortBy(getContext(),getResources().getString(R.string.top_tag));
            updateNews();
        } else if(id == R.id.action_popular && mPopular){
            Utility.setSortBy(getContext(),getResources().getString(R.string.popular_tag));
            updateNews();
        } else if(id == R.id.action_latest && mLatest){
            Utility.setSortBy(getContext(),getResources().getString(R.string.latest_tag));
            updateNews();
        } else {
            Toast.makeText(getContext(), R.string.sortBy_not_available,Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(NEWS_LOADER,null,this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(POSITION,mPosition);
        Log.v(SourceFragment.class.getSimpleName(),mPosition+"saved");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri newsUri = NewsContract.NewsEntry.CONTENT_URI;
        return new CursorLoader(getActivity(),
                newsUri,
                NEWS_COLUMN,
                NewsContract.NewsEntry.COLUMN_SOURCE + " = ? ",
                new String[]{source},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.moveToFirst()){
            mAdapter.swapCursor(data);
            mRecyclerView.getLayoutManager().scrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onPause() {
        super.onPause();
        mPosition = mAdapter.getPosition();
    }

    public void updateNews(){
        getLoaderManager().restartLoader(NEWS_LOADER,null,this);
        NewsSyncAdapter.syncImmediately(getActivity());
    }

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri newsUri);
    }
}
