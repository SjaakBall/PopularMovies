package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.example.android.popularmovies.data.MoviesContract;
import com.squareup.picasso.Picasso;

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

        String poster_path = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POSTER_PATH));

        ImageView imageView = new ImageView(mContext);
        imageView.setAdjustViewBounds(true);
        imageView.setPadding(0, 0, 0, 0);

        Picasso.with(context).load("http://image.tmdb.org/t/p/w185/" + poster_path).into((ImageView) imageView);
        ViewHolder viewHolder = new ViewHolder(imageView);
        imageView.setTag(viewHolder);

        return imageView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String poster_path = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POSTER_PATH));

        Picasso.with(context).load("http://image.tmdb.org/t/p/w185/" + poster_path).into((ImageView) view);
    }

    private class ViewHolder {
        public final ImageView imageView;

        public ViewHolder(View view) {
            imageView = new ImageView(mContext);
            imageView.setAdjustViewBounds(true);
            imageView.setPadding(0, 0, 0, 0);
        }
    }
}
