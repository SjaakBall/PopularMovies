package com.example.android.popularmovies;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends BaseAdapter {

    private final String LOG_TAG = ImageAdapter.class.getSimpleName();

    private Context mContext;
    private List<Movie> movies;

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

//        Picasso.with(context).load("http://i.imgur.com/DvpvklR.png").into(imageView);
//        http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=3b0a991e0da1fc4f86143d8acfb970bc
//        http://image.tmdb.org/t/p/w185//tbhdm8UJAb4ViCTsulYFL3lxMCd.jpg
        Log.v(LOG_TAG, "movies size = " + (movies != null ? movies.size() : "null"));
        if (movies != null && movies.size() > 0) {
            movieImages = new  String[movies.size()];
            for (int i = 0; i < movies.size();i++) {
                Movie movie = movies.get(i);
                movieImages[i]="http://image.tmdb.org/t/p/w185/" + movie.getPosterPath();
                Log.v(LOG_TAG, "movieposter: http://image.tmdb.org/t/p/w185/" + movie.getPosterPath());
//                Picasso.with(mContext).load("http://image.tmdb.org/t/p/w185/" + movie.getPosterPath()).into(imageView);
//                Picasso.with(this.mContext).setLoggingEnabled(true);
                Picasso.with(this.mContext).load("http://image.tmdb.org/t/p/w185/" + movie.getPosterPath()).into(imageView);
            }
        }


//        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }

    // references to our images
    private Integer[] mThumbIds = {
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7,
            R.drawable.sample_0, R.drawable.sample_1,
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7,
            R.drawable.sample_0, R.drawable.sample_1,
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7,
            R.drawable.sample_0, R.drawable.sample_1,
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7,
            R.drawable.sample_0, R.drawable.sample_1,
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7,
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7
    };

    private String[] movieImages;
//            = {
//            "http://image.tmdb.org/t/p/w185//9gm3lL8JMTTmc3W4BmNMCuRLdL8.jpg",
//            "http://image.tmdb.org/t/p/w185//dCgm7efXDmiABSdWDHBDBx2jwmn.jpg",
//            "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg",
//            "http://image.tmdb.org/t/p/w185//9gm3lL8JMTTmc3W4BmNMCuRLdL8.jpg",
//            "http://image.tmdb.org/t/p/w185//dCgm7efXDmiABSdWDHBDBx2jwmn.jpg",
//            "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg",
//            "http://image.tmdb.org/t/p/w185//9gm3lL8JMTTmc3W4BmNMCuRLdL8.jpg",
//            "http://image.tmdb.org/t/p/w185//dCgm7efXDmiABSdWDHBDBx2jwmn.jpg",
//            "http://image.tmdb.org/t/p/w185//9gm3lL8JMTTmc3W4BmNMCuRLdL8.jpg",
//            "http://image.tmdb.org/t/p/w185//dCgm7efXDmiABSdWDHBDBx2jwmn.jpg",
//            "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg",
//            "http://image.tmdb.org/t/p/w185//9gm3lL8JMTTmc3W4BmNMCuRLdL8.jpg",
//            "http://image.tmdb.org/t/p/w185//9gm3lL8JMTTmc3W4BmNMCuRLdL8.jpg",
//            "http://image.tmdb.org/t/p/w185//dCgm7efXDmiABSdWDHBDBx2jwmn.jpg",
//            "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg",
//            "http://image.tmdb.org/t/p/w185//9gm3lL8JMTTmc3W4BmNMCuRLdL8.jpg",
//            "http://image.tmdb.org/t/p/w185//dCgm7efXDmiABSdWDHBDBx2jwmn.jpg",
//            "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg",
//            "http://image.tmdb.org/t/p/w185//9gm3lL8JMTTmc3W4BmNMCuRLdL8.jpg",
//            "http://image.tmdb.org/t/p/w185//dCgm7efXDmiABSdWDHBDBx2jwmn.jpg",
//            "http://image.tmdb.org/t/p/w185//9gm3lL8JMTTmc3W4BmNMCuRLdL8.jpg",
//            "http://image.tmdb.org/t/p/w185//dCgm7efXDmiABSdWDHBDBx2jwmn.jpg",
//            "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg",
//            "http://image.tmdb.org/t/p/w185//9gm3lL8JMTTmc3W4BmNMCuRLdL8.jpg",
//            "http://image.tmdb.org/t/p/w185//dCgm7efXDmiABSdWDHBDBx2jwmn.jpg",
//            "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg",
//            "http://image.tmdb.org/t/p/w185//9gm3lL8JMTTmc3W4BmNMCuRLdL8.jpg",
//            "http://image.tmdb.org/t/p/w185//dCgm7efXDmiABSdWDHBDBx2jwmn.jpg",
//            "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg",
//            "http://image.tmdb.org/t/p/w185//9gm3lL8JMTTmc3W4BmNMCuRLdL8.jpg",
//            "http://image.tmdb.org/t/p/w185//dCgm7efXDmiABSdWDHBDBx2jwmn.jpg",
//            "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg",
//    };

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public List<Movie> getMovies() {
        return movies;
    }
}
