package com.capstone.ayush.aacnews;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.capstone.ayush.aacnews.adapter.SourcesAdapter;
import com.capstone.ayush.aacnews.data.NewsContract;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SourceFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{


    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private SourcesAdapter mAdapter;
    private View rootView;

    private static final int SOURCE_LOADER = 0;

    public static final String[] Source_COLUMNS = {
        NewsContract.SourceEntry._ID,
        NewsContract.SourceEntry.COLUMN_SOURCE_ID,
        NewsContract.SourceEntry.COLUMN_NAME,
        NewsContract.SourceEntry.COLUMN_DESCRIPTION,
        NewsContract.SourceEntry.COLUMN_URL,
        NewsContract.SourceEntry.COLUMN_LOGO_URL,
        NewsContract.SourceEntry.COLUMN_SORT_BY
    };

    public static final int COL_ID = 0;
    public static final int COL_SOURCE_ID = 1;
    public static final int COL_NAME = 2;
    public static final int COL_DESCRIPTION = 3;
    public static final int COL_URL = 4;
    public static final int COL_LOGO_URL = 5;
    public static final int COL_SORT_BY = 6;

    public static int mPosition;
    private final String POSITION="position";
    private static final String SELECTED_KEY = "selected_position";

    public SourceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(SOURCE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_source, container, false);
        AdView mAdView = (AdView) rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_sources);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new SourcesAdapter(getActivity(),null);
        mRecyclerView.setAdapter(mAdapter);
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(SOURCE_LOADER,null,this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(POSITION,mPosition);
        Log.v(SourceFragment.class.getSimpleName(),mPosition+"saved");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri sourceUri = NewsContract.SourceEntry.CONTENT_URI;
        return new CursorLoader(getActivity(),
                sourceUri,
                Source_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        mRecyclerView.getLayoutManager().scrollToPosition(mPosition);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
