package com.example.android.popularmovies;

import android.app.Fragment;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.data.MoviesContract;
import com.squareup.picasso.Picasso;

public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
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

        Bundle args = new Bundle();
        args.putInt("index", index);
        args.putParcelable(MOVIE_STATE, movie);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(LOG_TAG, "FLOW DetailActivityFragment.onCreateView");

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Button button = (Button) rootView.findViewById(R.id.button_favorite);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "View.OnClickListener onClick: ");
                if (getArguments() != null) {
                    movie = getArguments().getParcelable(MOVIE_STATE);
                    Log.v(LOG_TAG, "View.OnClickListener onClick movie is: " + movie.getOriginalTitle());
                    long movieId = addMovie(movie);
                }
            }
        });

        if (getArguments() != null && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
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

    private long addMovie(Movie movie) {
        long movieId;
        // First, check if the movie with this id exists in the db
        Cursor movieCursor = getActivity().getContentResolver().query(
                MoviesContract.MovieEntry.CONTENT_URI,
                new String[]{MoviesContract.MovieEntry.COLUMN_ID},
                MoviesContract.MovieEntry.COLUMN_ID + " = ?",
                new String[]{String.valueOf(movie.getId())},
                null);

        if (movieCursor != null && movieCursor.moveToFirst()) {
            int movieIdIndex = movieCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_ID);
            movieId = movieCursor.getLong(movieIdIndex);
        } else {
            // Now that the content provider is set up, inserting rows of data is pretty simple.
            // First create a ContentValues object to hold the data you want to insert.
            ContentValues movieValues = new ContentValues();

            // Then add the data, along with the corresponding name of the data type,
            // so the content provider knows what kind of value is being inserted.
            movieValues.put(MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE, movie.getOriginalTitle());
            movieValues.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
            movieValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
            movieValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
            movieValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
            movieValues.put(MoviesContract.MovieEntry.COLUMN_ID, movie.getId());

            // Finally, insert movie data into the database.
            Uri insertedUri = getActivity().getContentResolver().insert(
                    MoviesContract.MovieEntry.buildMoviesUri(movie.getId()),
                    movieValues
            );

            // The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
            movieId = ContentUris.parseId(insertedUri);
        }

        movieCursor.close();
        // Wait, that worked?  Yes!
        return movieId;
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

    // Attach loader to the movies database query
    // run when loader is initialized
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                MoviesContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    // Set the cursor in our CursorAdapter once the Cursor is loaded
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //TODO
//        mFlavorAdapter.swapCursor(data);
    }

    // reset CursorAdapter on Loader Reset
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //TODO
//        mFlavorAdapter.swapCursor(null);
    }
}
