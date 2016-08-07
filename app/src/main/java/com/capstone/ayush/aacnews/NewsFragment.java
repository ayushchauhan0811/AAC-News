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
import android.view.View;
import android.view.ViewGroup;

import com.capstone.ayush.aacnews.adapter.NewsAdapter;
import com.capstone.ayush.aacnews.data.NewsContract;
import com.capstone.ayush.aacnews.sync.NewsSyncAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private NewsAdapter mAdapter;
    private View rootView;
    private String source;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_news, container, false);
        source = Utility.getSource(getContext());
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_news);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new NewsAdapter(getActivity(),null);
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        updateNews();
        return  rootView;
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
            Log.e("Data size",String.valueOf(data.getCount()));
            mAdapter.swapCursor(data);
            //mRecyclerView.getLayoutManager().scrollToPosition(mPosition);
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
}
