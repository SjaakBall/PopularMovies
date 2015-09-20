package com.example.android.popularmovies;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private final static String MOVIES_STATE = "movies_state";
    private final static String MOVIE_STATE = "movie_state";

    private ImageAdapter imageAdapter;
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
//        gridView = (GridView) this.getActivity().findViewById(R.id.main_list_gridview);
        if (savedInstanceState != null) {
            movieList = savedInstanceState.getParcelableArrayList(MOVIES_STATE);
//            gridView.setAdapter(imageAdapter);
        } else {
            movieList = new ArrayList<Movie>();
            updateMovies();
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(LOG_TAG, "FLOW MainActivityFragment.onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        gridView = (GridView) rootView.findViewById(R.id.main_list_gridview);
        imageAdapter = new ImageAdapter(this.getActivity());
        if (savedInstanceState != null && savedInstanceState.containsKey(MOVIES_STATE)) {
            movieList = savedInstanceState.getParcelableArrayList(MOVIES_STATE);
        }
        imageAdapter.setMovies(movieList);
        imageAdapter.notifyDataSetChanged();
        gridView.setAdapter(imageAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Movie movie = (Movie) imageAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra(MOVIE_STATE, movie);
                startActivity(intent);
                Toast.makeText(MainActivityFragment.this.getActivity(), "" + position, Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            updateMovies();
            startActivity(new Intent(this.getActivity(), SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateMovies();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.v(LOG_TAG, "FLOW MainActivityFragment.onActivityCreated");

        // Populate list with our static array of titles.
//        setListAdapter(new ArrayAdapter<String>(getActivity(),
//                android.R.layout.simple_list_item_activated_1, Shakespeare.TITLES));

        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        View detailsFrame = getActivity().findViewById(R.id.tb_details_fragment);
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        if (savedInstanceState != null) {
            // Restore last state for checked position.
            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
        }

        if (mDualPane) {
            // Make sure our UI is in the correct state.
            showDetails(mCurCheckPosition);
        }

    }

    /**
     * Helper function to show the details of a selected item, either by
     * displaying a fragment in-place in the current UI, or starting a
     * whole new activity in which it is displayed.
     */
    private void showDetails(int index) {
        mCurCheckPosition = index;

        if (mDualPane) {

            // Check what fragment is currently shown, replace if needed.
            DetailActivityFragment details = (DetailActivityFragment) getFragmentManager().findFragmentById(R.id.tb_details_fragment);
            if (details == null || details.getShownIndex() != index) {
                // Make new fragment to show this selection.
                details = DetailActivityFragment.newInstance(index);

                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (index == 0) {
                    ft.replace(R.id.tb_details_fragment, details);
                }
//                else {
//                    ft.replace(R.id.a_item, details);
//                }
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }

        } else {
            // Otherwise we need to launch a new activity to display
            // the dialog fragment with selected text.
            Intent intent = new Intent();
            intent.setClass(getActivity(), DetailActivity.class);
            intent.putExtra("index", index);
            startActivity(intent);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v(LOG_TAG, "FLOW MainActivityFragment.onSaveInstanceState");
        outState.putInt("curChoice", mCurCheckPosition);
    }

    private void updateMovies() {
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sorting = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));
        Log.v(LOG_TAG, "sorting in updateMovies; " + sorting);
        fetchMoviesTask.execute(sorting);
    }


    /**
     * Created by JanHerman on 12/09/2015.
     */
    public class FetchMoviesTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private final String PERSONAL_API_KEY = getResources().getString(R.string.movie_api_key);
        private List<Movie> movieList;

        @Override
        protected String[] doInBackground(String... params) {

//            Log.v(LOG_TAG, "movie_api_key: " + PERSONAL_API_KEY);
            String sorting = params[0];
            Log.v(LOG_TAG, "sorting on: " + sorting);
            if (sorting == null || sorting.length() == 0) {
                SharedPreferences sharedPrefs =
                        PreferenceManager.getDefaultSharedPreferences(getActivity());
                sorting = sharedPrefs.getString(
                        getString(R.string.pref_sort_most_popular),
                        getString(R.string.pref_sort_default));
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            try {
                final String MOVIESDB_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String QUERY_PARAM = "sort_by";
                final String API_KEY = "api_key";

                Uri builtUri = Uri.parse(MOVIESDB_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, sorting)
                        .appendQueryParameter(API_KEY, PERSONAL_API_KEY)
                        .build();

                URL url = null;
                url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
//                Log.v(LOG_TAG, "moviesJSON: " + moviesJsonStr);
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
            if (results != null) {
                movieList = new ArrayList<Movie>();
                for (int i = 0; i < results.length; i++) {
                    String movieStr = results[i];
                    Log.v(LOG_TAG, "movie: " + movieStr);
                    List<String> list = new ArrayList<String>(Arrays.asList(movieStr.split("-!--")));
                    Movie movie = new Movie(list.get(0), list.get(1), list.get(2), list.get(3), list.get(4));
                    movieList.add(movie);
                }
            }
            imageAdapter = new ImageAdapter(getActivity());
            imageAdapter.setMovies(movieList);
            gridView.setAdapter(imageAdapter);
            imageAdapter.notifyDataSetChanged();
        }

        private String[] getMoviesDataFromJson(String moviesJsonStr) throws JSONException {

            final String RESULTS_LIST = "results";
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
                String original_title = movieJSONObject.getString(ORIGINAL_TITLE) != null && movieJSONObject.getString(ORIGINAL_TITLE).length() > 0 ? movieJSONObject.getString(ORIGINAL_TITLE) : "no original poster value";
                String poster_path = movieJSONObject.getString(POSTER_PATH) != null && movieJSONObject.getString(POSTER_PATH).length() > 0 ? movieJSONObject.getString(POSTER_PATH) : "no poster path value";;
                String overview = movieJSONObject.getString(OVERVIEW) != null && movieJSONObject.getString(OVERVIEW).length() > 0 ? movieJSONObject.getString(OVERVIEW) : "no overview value";;
                String vote_average = movieJSONObject.getString(VOTE_AVERAGE) != null && movieJSONObject.getString(VOTE_AVERAGE).length() > 0 ? movieJSONObject.getString(VOTE_AVERAGE) : "no vote average value";;
                String release_date = movieJSONObject.getString(RELEASE_DATE) != null && movieJSONObject.getString(RELEASE_DATE).length() > 0 ? movieJSONObject.getString(RELEASE_DATE) : "no release date value";;
                resultStrs[i] = original_title + "-!--" + poster_path + "-!--" + overview + "-!--" + vote_average + "-!--" + release_date;
            }

            return resultStrs;
        }

        public List<Movie> getMovieList() {
            return movieList;
        }
    }
}
