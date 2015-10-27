package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

/**
 * Created by JanHerman on 26/10/2015.
 */
public class MoviesAdapter extends CursorAdapter {

    private static final String LOG_TAG = MoviesAdapter.class.getSimpleName();
    private Context mContext;
    private static int sLoaderID;

    public MoviesAdapter(Context context, Cursor c, int flags, int loaderID) {
        super(context, c, flags);
        Log.d(LOG_TAG, "MoviesAdapter");
        mContext = context;
        sLoaderID = loaderID;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }
}
