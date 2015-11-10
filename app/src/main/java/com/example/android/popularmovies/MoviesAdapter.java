package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

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

//        View customView=null;
//        LayoutInflater inflater = LayoutInflater.from(context);
//        customView = inflater.inflate(R.layout.grid_item_imageview, parent, false);
//        Picasso.with(context).load("http://image.tmdb.org/t/p/w185/" + cursor.getString(1)).into((ImageView) customView);
//        customView.setTag(parent);
//        return customView;

        ImageView imageView = new ImageView(mContext);
        imageView.setAdjustViewBounds(true);
        imageView.setPadding(0, 0, 0, 0);

        Picasso.with(context).load("http://image.tmdb.org/t/p/w185/" + cursor.getString(1)).into((ImageView) imageView);
        ViewHolder viewHolder = new ViewHolder(imageView);
        imageView.setTag(viewHolder);

        return imageView;


//        int layoutId = -1;
//        layoutId = R.layout.grid_item_imageview;
//        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
//        Picasso.with(context).load("http://image.tmdb.org/t/p/w185/" + cursor.getString(1)).into((ImageView) view);
//        ViewHolder viewHolder = new ViewHolder(view);
//        view.setTag(viewHolder);
//
//        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        Picasso.with(context).load("http://image.tmdb.org/t/p/w185/" + cursor.getString(1)).into((ImageView) view);

//        ViewHolder viewHolder = (ViewHolder) view.getTag();
        Log.v(LOG_TAG, "FLOW MoviesAdapter cursor.getString(1): " + cursor.getString(1));
//        Picasso.with(this.mContext).load("http://image.tmdb.org/t/p/w185/" + cursor.getString(1)).into(viewHolder.imageView);
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
