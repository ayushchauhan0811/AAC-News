package com.capstone.ayush.aacnews.adapter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.capstone.ayush.aacnews.NewsFragment;
import com.capstone.ayush.aacnews.R;
import com.capstone.ayush.aacnews.data.NewsContract;

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
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Context context = mContext;
        Cursor cursor = mCursor;
        cursor.moveToPosition(position);
        if(cursor.getString(NewsFragment.COL_IMAGE_URL)!=null){
            Glide.with(context)
                    .load(cursor.getString(NewsFragment.COL_IMAGE_URL))
                    .into(holder.newsImage);
        } else {
            Glide.with(context)
                    .load(R.drawable.news_image)
                    .into(holder.newsImage);
        }
        holder.newsImage.setContentDescription(context.getString(R.string.news_iamge));
        holder.newsTitle.setText(cursor.getString(NewsFragment.COL_TITLE));
        holder.newsTitle.setContentDescription(cursor.getString(NewsFragment.COL_TITLE));

        holder.newsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = mCursor;
                cursor.moveToPosition(position);
                Uri newsUri = NewsContract.NewsEntry.buildNewsUri(cursor.getLong(NewsFragment.COL_ID));
                ((NewsFragment.Callback)context).onItemSelected(newsUri);
            }
        });

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
        if(mCursor==null)
            return 0;
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
