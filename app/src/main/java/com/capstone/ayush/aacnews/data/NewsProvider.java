/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.capstone.ayush.aacnews.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

public class NewsProvider extends ContentProvider {

    private static final String LOG_TAG = NewsProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = getBuildUriMatcher();
    private NewsDbHelper mOpenHelper;

    //Integer constants for the Uri
    private static final int NEWS = 100;
    private static final int NEWS_WITH_ID = 200;
    private static final int SOURCE = 300;
    private static final int SOURCE_WITH_ID = 400;

    @Override
    public boolean onCreate() {
        mOpenHelper = new NewsDbHelper(getContext());
        return true;
    }


    private  static UriMatcher getBuildUriMatcher() {
        // Build a Urimatcher by adding a specific code the return on a match
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = NewsContract.CONTENT_AUTHORITY;
        //add a code for each of uri you want
        matcher.addURI(authority, NewsContract.NewsEntry.TABLE_NAME, NEWS);
        matcher.addURI(authority, NewsContract.NewsEntry.TABLE_NAME + "/#", NEWS_WITH_ID);
        matcher.addURI(authority, NewsContract.SourceEntry.TABLE_NAME, SOURCE);
        matcher.addURI(authority, NewsContract.SourceEntry.TABLE_NAME + "/#", SOURCE_WITH_ID);
        return matcher;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case NEWS: {
                return NewsContract.NewsEntry.CONTENT_TYPE;
            }
            case NEWS_WITH_ID: {
                return NewsContract.NewsEntry.CONTENT_ITEM_TYPE;
            }
            case SOURCE: {
                return NewsContract.SourceEntry.CONTENT_TYPE;
            }
            case SOURCE_WITH_ID: {
                return NewsContract.SourceEntry.CONTENT_ITEM_TYPE;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            //All movies selected
            case NEWS: {
                retCursor = db.query(
                        NewsContract.NewsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            //All favorite movie selected
            case SOURCE: {
                retCursor = db.query(
                        NewsContract.SourceEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // Individual Movie based on Id
            case NEWS_WITH_ID: {
                retCursor = db.query(
                        NewsContract.NewsEntry.TABLE_NAME,
                        projection,
                        NewsContract.NewsEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // Individual favorite movie based on Id
            case SOURCE_WITH_ID: {
                retCursor = db.query(
                        NewsContract.SourceEntry.TABLE_NAME,
                        projection,
                        NewsContract.SourceEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri retUri;
        switch (sUriMatcher.match(uri)) {
            case NEWS_WITH_ID:{
                //returns the row id of the newly inserted row.
                Cursor cursor = db.query(
                        NewsContract.NewsEntry.TABLE_NAME,
                        null,
                        NewsContract.NewsEntry._ID  + " = ? ",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        null
                );
                if (cursor == null) {
                    long _id = db.insert(NewsContract.NewsEntry.TABLE_NAME, null, values);
                    //insert unless it is already contained in the database.
                    if (_id > 0) {
                        retUri = NewsContract.NewsEntry.buildNewsUri(_id);
                    } else {
                        throw new android.database.SQLException("Failed to insert row into: " + uri);
                    }
                } else {
                    retUri = NewsContract.NewsEntry
                            .buildNewsUri(cursor.getLong(0));
                }
                break;
            }
            case SOURCE_WITH_ID: {
                //returns the row id of the newly inserted row.
                Cursor cursor = db.query(
                        NewsContract.SourceEntry.TABLE_NAME,
                        null,
                        NewsContract.SourceEntry._ID + " = ? ",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        null
                );
                if (cursor == null) {
                    long _id = db.insert(NewsContract.SourceEntry.TABLE_NAME, null, values);
                    if (_id > 0 ) {
                        retUri = NewsContract.SourceEntry.buildSourceUri(_id);
                    } else {
                        throw new android.database.SQLException("Failed to insert row into: " + uri);
                    }
                }  else {
                    retUri = NewsContract.SourceEntry
                            .buildSourceUri(cursor.getLong(0));
                }
                break;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        //By default cursor adapter objects will get notifications from notifyChange()
        getContext().getContentResolver().notifyChange(uri, null);
        return retUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int numDeleted;
        switch (sUriMatcher.match(uri)) {
            case NEWS:{
                //returns the number of rows deleted
                numDeleted = db.delete(
                        NewsContract.NewsEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            }
            case SOURCE:{
                //returns the number of rows deleted
                numDeleted = db.delete(
                        NewsContract.SourceEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            }
            case NEWS_WITH_ID:{
                numDeleted = db.delete(
                        NewsContract.NewsEntry.TABLE_NAME,
                        NewsContract.NewsEntry._ID + " = ? ",
                        new String[] {String.valueOf(ContentUris.parseId(uri))}
                );
                break;
            }
            case SOURCE_WITH_ID:{
                numDeleted = db.delete(
                        NewsContract.SourceEntry.TABLE_NAME,
                        NewsContract.SourceEntry._ID + " = ? ",
                        new String[] {String.valueOf(ContentUris.parseId(uri))}
                );
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        if (numDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int numUpdated;
        switch (sUriMatcher.match(uri)) {
            case NEWS:{
                numUpdated = db.update(
                        NewsContract.NewsEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;
            }
            case SOURCE:{
                numUpdated = db.update(
                        NewsContract.SourceEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;
            }
            case NEWS_WITH_ID: {
                numUpdated = db.update(
                        NewsContract.NewsEntry.TABLE_NAME,
                        values,
                        NewsContract.NewsEntry._ID + " = ? ",
                        new String[] {String.valueOf(ContentUris.parseId(uri))}
                );
                break;
            }
            case SOURCE_WITH_ID: {
                numUpdated = db.update(
                        NewsContract.SourceEntry.TABLE_NAME,
                        values,
                        NewsContract.NewsEntry._ID + " = ? ",
                        new String[] {String.valueOf(ContentUris.parseId(uri))}
                );
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }

        }
        if (numUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int numInserted = 0;
        switch (sUriMatcher.match(uri)) {
            case NEWS: {
                //allows multiple transactions
                db.beginTransaction();
                try {
                    for (ContentValues value: values) {
                        long _id = db.insert(
                                NewsContract.NewsEntry.TABLE_NAME,
                                null,
                                value
                        );
                        if (_id != -1) {
                            numInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            }
            case SOURCE: {
                //allows multiple transaction
                db.beginTransaction();
                try {
                    for (ContentValues value: values) {
                        long _id = db.insert(
                                NewsContract.SourceEntry.TABLE_NAME,
                                null,
                                value
                        );
                        if (_id != -1) {
                            numInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            }
            default:{
                return super.bulkInsert(uri, values);
            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return numInserted;
    }
}