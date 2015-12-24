package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MoviesDBHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = MoviesDBHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 01;

    public MoviesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                MoviesContract.MovieEntry.TABLE_MOVIES + "(" +
                MoviesContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.MovieEntry.COLUMN_ID + " INTEGER NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL " +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);

        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " +
                MoviesContract.ReviewEntry.TABLE_REVIEW + " (" +
                MoviesContract.ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.ReviewEntry.COLUMN_ID + " INTEGER NOT NULL, " +
                MoviesContract.ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL,  " +
                MoviesContract.ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +
                MoviesContract.ReviewEntry.COLUMN_URL + " TEXT NOT NULL, " +
                MoviesContract.ReviewEntry.COLUMN_LOC_KEY + " INTEGER NOT NULL, " +
                " FOREIGN KEY (" + MoviesContract.ReviewEntry.COLUMN_LOC_KEY + ") REFERENCES " +
                MoviesContract.MovieEntry.TABLE_MOVIES + "(" + MoviesContract.MovieEntry._ID + ")," +
                " UNIQUE (" + MoviesContract.ReviewEntry.COLUMN_LOC_KEY + ") ON CONFLICT REPLACE" +
                ")";

        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);

        final String SQL_CREATE_VIDEO_TABLE = "CREATE TABLE " +
                MoviesContract.VideoEntry.TABLE_VIDEO + " (" +
                MoviesContract.VideoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.VideoEntry.COLUMN_KEY + " TEXT NOT NULL UNIQUE ,  " +
                MoviesContract.VideoEntry.COLUMN_NAME + " TEXT NOT NULL ,  " +
                MoviesContract.VideoEntry.COLUMN_TYPE + " TEXT NOT NULL,   " +
                MoviesContract.VideoEntry.COLUMN_SITE + " TEXT NOT NULL,   " +
                MoviesContract.VideoEntry.COLUMN_SIZE + " TEXT NOT NULL,   " +
                MoviesContract.VideoEntry.COLUMN_LOC_KEY + " INTEGER NOT NULL, " +
                " FOREIGN KEY (" + MoviesContract.VideoEntry.COLUMN_LOC_KEY + ") REFERENCES " +
                MoviesContract.MovieEntry.TABLE_MOVIES + "(" + MoviesContract.MovieEntry._ID + ")," +
                " UNIQUE (" + MoviesContract.VideoEntry.COLUMN_LOC_KEY + ") ON CONFLICT REPLACE" +
                ")";

        sqLiteDatabase.execSQL(SQL_CREATE_VIDEO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ". OLD DATA WILL BE DESTROYED");
        // Drop the table
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MovieEntry.TABLE_MOVIES);
        sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + MoviesContract.MovieEntry.TABLE_MOVIES + "'");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.ReviewEntry.TABLE_REVIEW);
        sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + MoviesContract.ReviewEntry.TABLE_REVIEW + "'");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.VideoEntry.TABLE_VIDEO);
        sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + MoviesContract.VideoEntry.TABLE_VIDEO + "'");

        // re-create database
        onCreate(sqLiteDatabase);
    }
}
