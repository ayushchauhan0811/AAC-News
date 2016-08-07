package com.capstone.ayush.aacnews.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.capstone.ayush.aacnews.NewsFragment;
import com.capstone.ayush.aacnews.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Ayush on 07-08-2016.
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private Cursor mCursor;
    private Context mContext;

    public NewsAdapter(Context mContext, Cursor mCursor) {
        this.mContext = mContext;
        this.mCursor = mCursor;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list, null);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Context context = mContext;
        Cursor cursor = mCursor;
        cursor.moveToPosition(position);
        Picasso.with(context)
                .load(cursor.getString(NewsFragment.COL_IMAGE_URL))
                .into(holder.newsImage);
        holder.newsImage.setContentDescription("News Image");
        holder.newsTitle.setText(cursor.getString(NewsFragment.COL_TITLE));
        holder.newsTitle.setContentDescription(cursor.getString(NewsFragment.COL_TITLE));


    }

    @Override
    public int getItemCount() {
        if ( null == mCursor ) return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor cursor){
        mCursor=cursor;
        notifyDataSetChanged();
    }

    public int getPosition(){
        return mCursor.getPosition();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView newsImage;
        public TextView newsTitle;

        public ViewHolder(View v) {
            super(v);
            newsImage = (ImageView) v.findViewById(R.id.news_photo);
            newsTitle = (TextView) v.findViewById(R.id.news_title);
        }
    }
}
