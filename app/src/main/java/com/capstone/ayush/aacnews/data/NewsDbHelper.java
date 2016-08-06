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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.capstone.ayush.aacnews.data.NewsContract.NewsEntry;
import com.capstone.ayush.aacnews.data.NewsContract.SourceEntry;

/**
 * Manages a local database for weather data.
 */
public class NewsDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "weather.db";

    public NewsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold locations.  A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude
        final String SQL_CREATE_NEWS_TABLE = "CREATE TABLE " + NewsEntry.TABLE_NAME + " (" +
                NewsEntry._ID + " INTEGER PRIMARY KEY," +
                NewsEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                NewsEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                NewsEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                NewsEntry.COLUMN_URL + " TEXT NOT NULL, " +
                NewsEntry.COLUMN_IMAGE_URL + " TEXT NOT NULL, " +
                NewsEntry.COLUMN_PUBLISHED_AT + " TEXT NOT NULL " +
                " );";

        final String SQL_CREATE_SOURCE_TABLE = "CREATE TABLE " + SourceEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                SourceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SourceEntry.COLUMN_SOURCE_ID + " TEXT NOT NULL, " +
                SourceEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                SourceEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                SourceEntry.COLUMN_URL + " TEXT NOT NULL, " +
                SourceEntry.COLUMN_LOGO_URL + " TEXT NOT NULL, " +
                SourceEntry.COLUMN_SORT_BY + "TEXT NOT NULL " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_NEWS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_SOURCE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NewsEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SourceEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
