package com.capstone.ayush.aacnews.widget;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.capstone.ayush.aacnews.NewsFragment;
import com.capstone.ayush.aacnews.R;
import com.capstone.ayush.aacnews.data.NewsContract;

import java.util.concurrent.ExecutionException;


/**
 * Created by kosrat on 7/15/16.
 */
public class DetailWidgetRemoteViewsService extends RemoteViewsService {
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
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(NewsContract.NewsEntry.CONTENT_URI,
                        NEWS_COLUMN,
                        null,
                        null,
                        null);
                Log.e("count",""+data.getCount());
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);

                if (data.moveToPosition(position)) {

                    String title = data.getString(data.getColumnIndex(NewsContract.NewsEntry.COLUMN_TITLE));
                    String url = data.getString(data.getColumnIndex(NewsContract.NewsEntry.COLUMN_IMAGE_URL));
                    int resId = R.drawable.news_image;

                    views.setTextViewText(R.id.news_title, title);

                    Bitmap bitmap = null;

                    try {
                        bitmap = Glide.with(DetailWidgetRemoteViewsService.this)
                                .load(url)
                                .asBitmap()
                                .error(resId)
                                .into(-1,-1).get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                    if(bitmap!=null){
                        views.setImageViewBitmap(R.id.news_photo,bitmap);
                    } else {
                        views.setImageViewResource(R.id.news_photo,resId);
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                        views.setContentDescription(R.id.news_photo,
                                getString(R.string.news_iamge));
                        views.setContentDescription(R.id.news_title,
                                data.getString(data.getColumnIndex(NewsContract.NewsEntry.COLUMN_TITLE)));
                    }

                    final Intent fillInIntent = new Intent();
                    fillInIntent.setAction(DetailWidgetProvider.DETAIL_ACTION);
                    Uri newsUri = NewsContract.NewsEntry.buildNewsUri(data.getLong(data.getColumnIndex(NewsContract.NewsEntry._ID)));
                    fillInIntent.setData(newsUri);
                    views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);

                }
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(data.getColumnIndex(NewsContract.NewsEntry._ID));
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}