package com.capstone.ayush.aacnews.widget;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.Build;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.capstone.ayush.aacnews.NewsFragment;
import com.capstone.ayush.aacnews.R;
import com.capstone.ayush.aacnews.data.NewsContract;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;


/**
 * Created by kosrat on 7/15/16.
 */
public class DetailWidgetRemoteViewsService extends RemoteViewsService {
    private Target loadtarget;
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
                        NewsFragment.NEWS_COLUMN,
                        null,
                        null,
                        null);
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
                final RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);

                if (data.moveToPosition(position)) {

                    String title = data.getString(data.getColumnIndex(NewsContract.NewsEntry.COLUMN_TITLE));
                    String url = data.getString(data.getColumnIndex(NewsContract.NewsEntry.COLUMN_IMAGE_URL));
                    final int resId = R.drawable.news_image;
                    views.setTextViewText(R.id.news_title, title);

                    if (loadtarget == null) loadtarget = new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            // do something with the Bitmap
                            views.setImageViewBitmap(R.id.news_photo,bitmap);
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {
                            views.setImageViewResource(R.id.news_photo,resId);
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    };

                    Picasso.with(DetailWidgetRemoteViewsService.this).load(url).into(loadtarget);


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                        views.setContentDescription(R.id.news_photo,
                                getString(R.string.news_iamge));
                        views.setContentDescription(R.id.news_title,
                                data.getString(data.getColumnIndex(NewsContract.NewsEntry.COLUMN_TITLE)));
                    }

                    final Intent fillInIntent = new Intent();
                    fillInIntent.setAction(DetailWidgetProvider.DETAIL_ACTION);
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