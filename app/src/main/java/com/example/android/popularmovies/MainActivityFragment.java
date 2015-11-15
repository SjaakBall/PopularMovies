package com.example.android.popularmovies;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.android.popularmovies.data.MoviesContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.provider.BaseColumns._ID;
import static com.example.android.popularmovies.data.MoviesContract.MovieEntry.COLUMN_ID;
import static com.example.android.popularmovies.data.MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE;
import static com.example.android.popularmovies.data.MoviesContract.MovieEntry.COLUMN_OVERVIEW;
import static com.example.android.popularmovies.data.MoviesContract.MovieEntry.COLUMN_POSTER_PATH;
import static com.example.android.popularmovies.data.MoviesContract.MovieEntry.COLUMN_RELEASE_DATE;
import static com.example.android.popularmovies.data.MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE;

public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private final static String MOVIES_STATE = "movies_state";
    private final static String MOVIE_STATE = "movie_state";

    private ImageAdapter imageAdapter;
    private MoviesAdapter moviesAdapter;
    private GridView gridView;
    private List<Movie> movieList = null;
    boolean mDualPane;
    int mCurCheckPosition = 0;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "FLOW MainActivityFragment.onCreate");
        if (savedInstanceState != null) {
            movieList = savedInstanceState.getParcelableArrayList(MOVIES_STATE);
        } else {
            movieList = new ArrayList<>();
//            updateMovies();
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(LOG_TAG, "FLOW MainActivityFragment.onStart");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        Log.v(LOG_TAG, "FLOW MainActivityFragment.onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        gridView = (GridView) rootView.findViewById(R.id.main_list_gridview);

        populateGridView();

        if (savedInstanceState != null && savedInstanceState.containsKey(MOVIES_STATE)) {
            movieList = savedInstanceState.getParcelableArrayList(MOVIES_STATE);
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                Movie movie = null;
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String sorting = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));
                if (sorting.equals("favorites")) {
                    Cursor movieCursor = getActivity().getContentResolver().query(
                            MoviesContract.MovieEntry.CONTENT_URI,
                            MOVIES_SUMMARY_PROJECTION,
                            MoviesContract.MovieEntry._ID + " = ?",
                            new String[]{String.valueOf(position + 1)},
                            null);
                    //int id, String originalTitle, String posterPath, String overview, String voteAverage, String releaseDate
                    if (movieCursor != null && movieCursor.moveToFirst()) {
                        int movieIdIndex = movieCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_ID);
                        int originalTitleIndex = movieCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE);
                        int posterPathIndex = movieCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POSTER_PATH);
                        int overviewIndex = movieCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_OVERVIEW);
                        int voteAverageIndex = movieCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE);
                        int releaseDateIndex = movieCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE);
                        movie = new Movie(
                                movieCursor.getInt(movieIdIndex),
                                movieCursor.getString(originalTitleIndex),
                                movieCursor.getString(posterPathIndex),
                                movieCursor.getString(overviewIndex),
                                movieCursor.getString(voteAverageIndex),
                                movieCursor.getString(releaseDateIndex)
                        );
                        Log.v(LOG_TAG, "FLOW setOnItemClickListener movie.toString()" + movie.toString());
                    }


                } else {
                    movie = (Movie) imageAdapter.getItem(position);
                }
                //which frame is shown
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    Log.v(LOG_TAG, "FLOW MainActivityFragment.onCreateView onItemClick landscape");
                    DetailActivityFragment details = DetailActivityFragment.newInstance(position, movie);
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.tb_details_fragment, details);
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    fragmentTransaction.commit();
                } else {
                    Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra(MOVIE_STATE, movie);
                    startActivity(intent);
                    Log.v(LOG_TAG, "FLOW MainActivityFragment.onCreateView onItemClick portrait");
                    Toast.makeText(MainActivityFragment.this.getActivity(), "" + position, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    private void populateGridView() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sorting = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));
        if (sorting.equals("favorites")) {
            Log.v(LOG_TAG, "FLOW populateGridView sorting favorites ");
            Cursor cursor =
                    getActivity().getContentResolver().query(MoviesContract.MovieEntry.CONTENT_URI,
                            new String[]{_ID, COLUMN_POSTER_PATH},
                            null,
                            null,
            null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    // Gets the value from the column.
                    Log.v(LOG_TAG, "FLOW cursor.getInt " + cursor.getColumnName(0));
                    Log.v(LOG_TAG, "FLOW cursor.getInt " + cursor.getColumnName(1));
                    Log.v(LOG_TAG, "FLOW cursor.getInt " + cursor.getString(0));
                    Log.v(LOG_TAG, "FLOW cursor.getInt " + cursor.getString(1));
                }
            }

            moviesAdapter = new MoviesAdapter(getActivity(), cursor, 0, 0);
            gridView.setAdapter(moviesAdapter);
            moviesAdapter.notifyDataSetChanged();
        } else {
            Log.v(LOG_TAG, "FLOW populateGridView sorting NOT favorites  movieList.size():" + movieList.size());
            imageAdapter = new ImageAdapter(this.getActivity());
            imageAdapter.setMovies(movieList);
            imageAdapter.notifyDataSetChanged();
            gridView.setAdapter(imageAdapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this.getActivity(), SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(LOG_TAG, "FLOW MainActivityFragment.onResume");
        updateMovies();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(LOG_TAG, "FLOW MainActivityFragment.onPause");
    }

    // These are the Contacts rows that we will retrieve.
    static final String[] MOVIES_SUMMARY_PROJECTION = new String[]{
            COLUMN_ID,
            COLUMN_ORIGINAL_TITLE,
            COLUMN_POSTER_PATH,
            COLUMN_OVERVIEW,
            COLUMN_VOTE_AVERAGE,
            COLUMN_RELEASE_DATE,
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.v(LOG_TAG, "FLOW MainActivityFragment.onActivityCreated");

        View detailsFrame = getActivity().findViewById(R.id.tb_details_fragment);
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        if (savedInstanceState != null) {
            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
        }

////        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
////        String sorting = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));
////        if (sorting.equals("favorites")) {
//
//        Cursor cursor =
//                getActivity().getContentResolver().query(MoviesContract.MovieEntry.CONTENT_URI,
//                        new String[]{_ID, COLUMN_POSTER_PATH},
//                        null,
//                        null,
//                        null);
//
//        if (cursor != null) {
//    /*
//     * Moves to the next row in the cursor. Before the first movement in the cursor, the
//     * "row pointer" is -1, and if you try to retrieve data at that position you will get an
//     * exception.
//     */
//            while (cursor.moveToNext()) {
//
//                // Gets the value from the column.
//                Log.v(LOG_TAG, "FLOW cursor.getInt " + cursor.getColumnName(0));
//                Log.v(LOG_TAG, "FLOW cursor.getInt " + cursor.getColumnName(1));
//                Log.v(LOG_TAG, "FLOW cursor.getInt " + cursor.getString(0));
//                Log.v(LOG_TAG, "FLOW cursor.getInt " + cursor.getString(1));
//            }
//        }
//        // Create an empty adapter we will use to display the loaded data.
////        moviesAdapter = new SimpleCursorAdapter(
////                getActivity(),
////                android.R.layout.simple_list_item_2,
////                cursor,
////                new String[]{COLUMN_POSTER_PATH},
////                new int[]{android.R.id.text1},
////                0);
//
//        moviesAdapter = new MoviesAdapter(getActivity(), cursor, 0, 0);
//        }
        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);

    }

    private void showDetails(int index) {
        mCurCheckPosition = index;
        Log.v(LOG_TAG, "FLOW MainActivityFragment.showDetails & index : " + index);

        if (mDualPane) {

            DetailActivityFragment details = (DetailActivityFragment) getFragmentManager().findFragmentById(R.id.details_fragment_container);
            Log.v(LOG_TAG, "FLOW MainActivityFragment.showDetails mDualPane details: " + details);
            if (details == null) {
                Log.v(LOG_TAG, "FLOW MainActivityFragment.showDetails mDualPane: details == null || details.getShownIndex() != index");
                // Make new fragment to show this selection.
                details = DetailActivityFragment.newInstance(index, imageAdapter.getMovies().get(index));

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (index == 0) {
                    ft.replace(R.id.tb_details_fragment, details);
                }
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }

        } else {
            Log.v(LOG_TAG, "FLOW MainActivityFragment.showDetails NOT mDualPane");
            Intent intent = new Intent();
            intent.setClass(getActivity(), DetailActivity.class);
            intent.putExtra("index", index);
            intent.putExtra(MOVIE_STATE, imageAdapter.getMovies().get(index));
            startActivity(intent);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v(LOG_TAG, "FLOW MainActivityFragment.onSaveInstanceState");
        outState.putInt("curChoice", mCurCheckPosition);
        outState.putParcelableArrayList(MOVIES_STATE, (ArrayList<? extends Parcelable>) movieList);
    }

    private void updateMovies() {
        Log.v(LOG_TAG, "FLOW MainActivityFragment.updateMovies");
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sorting = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));
        if (!sorting.equals("favorites")) {
            fetchMoviesTask.execute(sorting);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), MoviesContract.MovieEntry.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (moviesAdapter != null) {
            moviesAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (moviesAdapter != null) {
            moviesAdapter.swapCursor(null);
        }
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private final String PERSONAL_API_KEY = getResources().getString(R.string.movie_api_key);

        @Override
        protected String[] doInBackground(String... params) {

            String sorting = params[0];
            if (sorting == null || sorting.length() == 0) {
                SharedPreferences sharedPrefs =
                        PreferenceManager.getDefaultSharedPreferences(getActivity());
                sorting = sharedPrefs.getString(
                        getString(R.string.pref_sort_most_popular),
                        getString(R.string.pref_sort_default));
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String moviesJsonStr = null;

            try {
                final String MOVIESDB_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String QUERY_PARAM = "sort_by";
                final String API_KEY = "api_key";

                Uri builtUri = Uri.parse(MOVIESDB_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, sorting)
                        .appendQueryParameter(API_KEY, PERSONAL_API_KEY)
                        .build();

                URL url;
                url = new URL(builtUri.toString());
                Log.v(LOG_TAG, "builtUri.toString(): " + builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                moviesJsonStr = buffer.toString();
            } catch (MalformedURLException mue) {
                Log.e(LOG_TAG, "Error ", mue);
            } catch (ProtocolException pe) {
                Log.e(LOG_TAG, "Error ", pe);
            } catch (IOException ioe) {
                Log.e(LOG_TAG, "Error ", ioe);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMoviesDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(String[] results) {
            super.onPostExecute(results);

            Log.v(LOG_TAG, "FLOW FetchMoviesTask.onPostExecute");
            if (results != null) {
                movieList = new ArrayList<Movie>();
                for (int i = 0; i < results.length; i++) {
                    String movieStr = results[i];
                    List<String> list = new ArrayList<String>(Arrays.asList(movieStr.split("-!--")));
                    Movie movie = new Movie(Integer.parseInt(list.get(0)), list.get(1), list.get(2), list.get(3), list.get(4), list.get(5));
                    movieList.add(movie);
                }
            }
            populateGridView();
//            imageAdapter = new ImageAdapter(getActivity());
//            imageAdapter.setMovies(movieList);
//            gridView.setAdapter(imageAdapter);
//            imageAdapter.notifyDataSetChanged();
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
                    && movieList != null && movieList.size() > 0) {
                showDetails(mCurCheckPosition);
            }
        }

        private String[] getMoviesDataFromJson(String moviesJsonStr) throws JSONException {

            final String RESULTS_LIST = "results";
            final String ID = "id";
            final String ORIGINAL_TITLE = "original_title";
            final String POSTER_PATH = "poster_path";
            final String OVERVIEW = "overview";
            final String VOTE_AVERAGE = "vote_average";
            final String RELEASE_DATE = "release_date";

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(RESULTS_LIST);

            String[] resultStrs = new String[moviesArray.length()];

            for (int i = 0; i < moviesArray.length(); i++) {

                JSONObject movieJSONObject = moviesArray.getJSONObject(i);
                String id = movieJSONObject.getString(ID) != null && movieJSONObject.getString(ID).length() > 0 ? movieJSONObject.getString(ID) : "no id value";
                String original_title = movieJSONObject.getString(ORIGINAL_TITLE) != null && movieJSONObject.getString(ORIGINAL_TITLE).length() > 0 ? movieJSONObject.getString(ORIGINAL_TITLE) : "no original poster value";
                String poster_path = movieJSONObject.getString(POSTER_PATH) != null && movieJSONObject.getString(POSTER_PATH).length() > 0 ? movieJSONObject.getString(POSTER_PATH) : "no poster path value";
                String overview = movieJSONObject.getString(OVERVIEW) != null && movieJSONObject.getString(OVERVIEW).length() > 0 ? movieJSONObject.getString(OVERVIEW) : "no overview value";
                String vote_average = movieJSONObject.getString(VOTE_AVERAGE) != null && movieJSONObject.getString(VOTE_AVERAGE).length() > 0 ? movieJSONObject.getString(VOTE_AVERAGE) : "no vote average value";
                String release_date = movieJSONObject.getString(RELEASE_DATE) != null && movieJSONObject.getString(RELEASE_DATE).length() > 0 ? movieJSONObject.getString(RELEASE_DATE) : "no release date value";
                resultStrs[i] = id + "-!--" + original_title + "-!--" + poster_path + "-!--" + overview + "-!--" + vote_average + "-!--" + release_date;
            }

            return resultStrs;
        }
    }
}
