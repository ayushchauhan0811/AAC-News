package com.capstone.ayush.aacnews;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.capstone.ayush.aacnews.data.NewsContract;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{


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

    private static final int NEWS_LOADER = 0;

    private ImageView newsImage;
    private TextView newsTitle;
    private TextView newsDesp;
    private TextView newsByline;

    private Uri mNewsUri;


    public DetailFragment() {
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
        View rootView =  inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        mNewsUri = intent.getData();
        newsImage = (ImageView) rootView.findViewById(R.id.news_photo);
        newsTitle = (TextView) rootView.findViewById(R.id.news_title);
        newsDesp = (TextView) rootView.findViewById(R.id.news_description);
        newsByline = (TextView) rootView.findViewById(R.id.article_byline);
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                mNewsUri,
                NEWS_COLUMN,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.moveToFirst()){
            if(data.getString(COL_IMAGE_URL)!=null){
                Glide.with(getContext())
                        .load(data.getString(COL_IMAGE_URL))
                        .into(newsImage);
            } else {
                Glide.with(getContext())
                        .load(R.drawable.news_image)
                        .into(newsImage);
            }

            newsImage.setContentDescription(getResources().getString(R.string.news_image_desp));
            newsTitle.setText(data.getString(COL_TITLE));
            newsTitle.setContentDescription(data.getString(COL_TITLE));
            newsDesp.setText(data.getString(COL_DESCRIPTION));
            newsDesp.setContentDescription("Title" + data.getString(COL_DESCRIPTION));
            String byline = "";
            if(data.getString(COL_PUBLISHED_AT)!=null){
                byline = data.getString(COL_PUBLISHED_AT);
            }
            if (data.getString(COL_AUTHOR)!=null){
                byline = byline + " by " + data.getString(COL_AUTHOR);
            }
            newsByline.setText(byline);
            newsByline.setContentDescription("Published at" + byline);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
