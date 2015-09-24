package com.example.android.popularmovies;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by JanHerman on 17/09/2015.
 */
public class DetailActivityFragment extends Fragment {
    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    private final static String MOVIE_STATE = "movie_state";
    private Movie movie;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.v(LOG_TAG, "FLOW DetailActivityFragment.onCreate ORIENTATION_LANDSCAPE");
            if (getArguments() != null) {
                movie = getArguments().getParcelable(MOVIE_STATE);
                Log.v(LOG_TAG, "onCreate OriginalTitle From Parcelable: " + movie.getOriginalTitle());
            }
            return;
        }

    }

    public static DetailActivityFragment newInstance(int index, Movie movie) {
        Log.v(LOG_TAG, "FLOW DetailActivityFragment.newInstance");
        DetailActivityFragment f = new DetailActivityFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("index", index);
        args.putParcelable(MOVIE_STATE, movie);
        f.setArguments(args);

        return f;
    }

    public int getShownIndex() {
        int index = 0;
        if (getArguments() != null) {
            index = getArguments().getInt("index", 0);
        }
        return index;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(LOG_TAG, "FLOW DetailActivityFragment.onCreateView");

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        if (getArguments() != null) {
            movie = getArguments().getParcelable(MOVIE_STATE);
            Log.v(LOG_TAG, "onCreateView OriginalTitle From Parcelable: " + movie.getOriginalTitle());
            populateView(rootView, movie);
        }


        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(MOVIE_STATE)) {
            Log.v(LOG_TAG, "FLOW DetailActivityFragment.onCreateView intent.hasExtra(MOVIE_STATE");
            movie = intent.getParcelableExtra(MOVIE_STATE);
            Log.v(LOG_TAG, "OriginalTitle: " + movie.getOriginalTitle());
            populateView(rootView, movie);
        }

        return rootView;
    }

    private void populateView(View rootView, Movie movie) {
        ((TextView) rootView.findViewById(R.id.detail_title)).setText(movie.getOriginalTitle());
        ((TextView) rootView.findViewById(R.id.detail_plot_synopsis)).setText(movie.getOverview());
        ((TextView) rootView.findViewById(R.id.detail_user_rating)).setText(movie.getVoteAverage());
        ((TextView) rootView.findViewById(R.id.detail_release_date)).setText(movie.getReleaseDate());
        ImageView imageView = (ImageView) rootView.findViewById(R.id.detail_imageView);
        Picasso.with(getActivity())
                .load("http://image.tmdb.org/t/p/w185/" + movie.getPosterPath())
                .into(imageView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(LOG_TAG, "FLOW DetailActivityFragment.onDestroy");
    }
}
