package com.capstone.ayush.aacnews.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.capstone.ayush.aacnews.MainActivity;
import com.capstone.ayush.aacnews.NewsActivity;
import com.capstone.ayush.aacnews.R;
import com.capstone.ayush.aacnews.SourceFragment;
import com.capstone.ayush.aacnews.Utility;
import com.squareup.picasso.Picasso;

/**
 * Created by Ayush on 06-08-2016.
 */
public class SourcesAdapter extends RecyclerView.Adapter<SourcesAdapter.ViewHolder> {
    private String baseURL = "http://image.tmdb.org/t/p/w185";
    private Cursor mCursor;
    private Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView sourceLogo;

        public ViewHolder(View v) {
            super(v);
            sourceLogo = (ImageView) v.findViewById(R.id.source_photo);
        }
    }

    public SourcesAdapter(Context context, Cursor c) {
        mContext = context;
        mCursor = c;
    }

    @Override
    public SourcesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.socurce_list, null);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Context context = mContext;
        Cursor cursor = mCursor;
        cursor.moveToPosition(position);
        Picasso.with(context)
                .load(cursor.getString(SourceFragment.COL_LOGO_URL))
                .into(holder.sourceLogo);


        holder.sourceLogo.setContentDescription(cursor.getString(SourceFragment.COL_NAME));

        holder.sourceLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = mCursor;
                cursor.moveToPosition(position);
                String[] sortByKeys = cursor.getString(SourceFragment.COL_SORT_BY).split(",");
                String mSortBykey = sortByKeys[0];
                SourceFragment.mPosition = position;
                Utility.setSource(context,cursor.getString(SourceFragment.COL_SOURCE_ID));
                Utility.setSortBy(context,mSortBykey);
                Log.e("Default Sort By",mSortBykey);
                Intent intent = new Intent(context, NewsActivity.class);
                intent.putExtra("sortBykeys",sortByKeys);
                context.startActivity(intent);
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
        //Log.e(MoviesAdapter.class.getSimpleName(),"notifying dataset changes");
        notifyDataSetChanged();
    }

    public int getPosition(){
        return mCursor.getPosition();
    }

}
