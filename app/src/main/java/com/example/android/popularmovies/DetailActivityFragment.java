package com.example.android.popularmovies;

import android.app.Fragment;
import android.content.Intent;
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
    private final String DETAILFRAGMENT_TAG = "DFTAG";
    private final String MOVIE_STATE = "movie_state";
    private int index;

    /**
     * Create a new instance of DetailsFragment, initialized to
     * show the text at 'index'.
     */
    public static DetailActivityFragment newInstance(int index) {
        Log.v(LOG_TAG, "FLOW DetailActivityFragment.newInstance");
        DetailActivityFragment f = new DetailActivityFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("index", index);
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

//        if (container == null) {
//            // We have different layouts, and in one of them this
//            // fragment's containing frame doesn't exist.  The fragment
//            // may still be created from its saved state, but there is
//            // no reason to try to create its view hierarchy because it
//            // won't be displayed.  Note this is not needed -- we could
//            // just run the code below, where we would create and return
//            // the view hierarchy; it would just never be used.
//            return null;
//        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(MOVIE_STATE)) {
            Movie movie = intent.getParcelableExtra(MOVIE_STATE);
            Log.v(LOG_TAG, "OriginalTitle: " + movie.getOriginalTitle());
            ((TextView) rootView.findViewById(R.id.detail_title)).setText(movie.getOriginalTitle());
            ((TextView) rootView.findViewById(R.id.detail_plot_synopsis)).setText(movie.getOverview());
            ((TextView) rootView.findViewById(R.id.detail_user_rating)).setText(movie.getVoteAverage());
            ((TextView) rootView.findViewById(R.id.detail_release_date)).setText(movie.getReleaseDate());
            ImageView imageView = (ImageView) rootView.findViewById(R.id.detail_imageView);
            Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185/" + movie.getPosterPath()).into(imageView);
        }

        return rootView;
    }
}
