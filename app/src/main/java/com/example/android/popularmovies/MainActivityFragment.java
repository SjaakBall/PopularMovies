package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    private ImageAdapter imageAdapter;
    private FetchMoviesTask fetchMoviesTask;
    private List<Movie> movieList = null;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null){
//            savedInstanceState.
        }
//        fetchMoviesTask = new FetchMoviesTask(new AsyncResponse() {
//            @Override
//            public void processFinish(Object output) {
//                Log.d("Response:", output.toString());
//                movieList = (List<Movie>) output;
//            }
//        });
//        fetchMoviesTask.execute("");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        fetchMoviesTask = new FetchMoviesTask(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                Log.d("Response:", output.toString());
                movieList = (List<Movie>) output;
            }
        });
        fetchMoviesTask.execute("");

        GridView gridview = (GridView) rootView.findViewById(R.id.gridview);
        imageAdapter = new ImageAdapter(this.getActivity());
        imageAdapter.setMovies(movieList);
        gridview.setAdapter(imageAdapter);
        imageAdapter.notifyDataSetChanged();
//        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//                Toast.makeText(MainActivityFragment.this.getActivity(), "" + position, Toast.LENGTH_SHORT).show();
//            }
//        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
//        updateMovies();
    }

    private void updateMovies() {
        fetchMoviesTask = new FetchMoviesTask(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                Log.v("Response:", output.toString());
            }
        });
        //TODO add sorting
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        String location = prefs.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
        fetchMoviesTask.execute("most_popular");
        List<Movie> movies = fetchMoviesTask.getMovieList();
        if (movies != null) {
            for (Movie movie : movies) {
//                Log.v(LOG_TAG, movie.toString());
            }
        }
    }


    /**
     * Created by JanHerman on 12/09/2015.
     */
//    public class FetchMoviesTask extends AsyncTask<String, Void, String[]> {
//
//        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
//
//        //TODO Remember to remove this key
//        private final static String PERSONAL_API_KEY = "3b0a991e0da1fc4f86143d8acfb970bc";
//
//        private List<Movie> movieList;
//
//        @Override
//        protected String[] doInBackground(String... params) {
//
//            //TODO params getting in use for sort on most popular and highest-rated
//
//            // These two need to be declared outside the try/catch
//            // so that they can be closed in the finally block.
//            HttpURLConnection urlConnection = null;
//            BufferedReader reader = null;
//
//            // Will contain the raw JSON response as a string.
//            String moviesJsonStr = null;
//
//            try {
//                final String MOVIESDB_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
//                final String QUERY_PARAM = "sort_by";
//                final String API_KEY = "api_key";
//
//                Uri builtUri = Uri.parse(MOVIESDB_BASE_URL).buildUpon()
//                        .appendQueryParameter(QUERY_PARAM, "popularity.desc")
//                        .appendQueryParameter(API_KEY, PERSONAL_API_KEY)
//                        .build();
//
////            Log.v(LOG_TAG, "builtUri: " + builtUri);
//                URL url = null;
//                url = new URL(builtUri.toString());
//                // Create the request to OpenWeatherMap, and open the connection
//                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("GET");
//                urlConnection.connect();
//
//                // Read the input stream into a String
//                InputStream inputStream = urlConnection.getInputStream();
//                StringBuffer buffer = new StringBuffer();
//                if (inputStream == null) {
//                    // Nothing to do.
//                    return null;
//                }
//                reader = new BufferedReader(new InputStreamReader(inputStream));
//
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
//                    // But it does make debugging a *lot* easier if you print out the completed
//                    // buffer for debugging.
//                    buffer.append(line + "\n");
//                }
//
//                if (buffer.length() == 0) {
//                    // Stream was empty.  No point in parsing.
//                    return null;
//                }
//                moviesJsonStr = buffer.toString();
//                Log.v(LOG_TAG, "moviesJSON: " + moviesJsonStr);
//            } catch (MalformedURLException mue) {
//                Log.e(LOG_TAG, "Error ", mue);
//            } catch (ProtocolException pe) {
//                Log.e(LOG_TAG, "Error ", pe);
//            } catch (IOException ioe) {
//                Log.e(LOG_TAG, "Error ", ioe);
//            } finally {
//                if (urlConnection != null) {
//                    urlConnection.disconnect();
//                }
//                if (reader != null) {
//                    try {
//                        reader.close();
//                    } catch (final IOException e) {
//                        Log.e(LOG_TAG, "Error closing stream", e);
//                    }
//                }
//            }
//
//            try {
//                return getMoviesDataFromJson(moviesJsonStr);
//            } catch (JSONException e) {
//                Log.e(LOG_TAG, e.getMessage(), e);
//                e.printStackTrace();
//            }
//
//            // This will only happen if there was an error getting or parsing the forecast.
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String[] results) {
//            super.onPostExecute(results);
//            if (results != null) {
//                movieList = new ArrayList<>();
//                for (int i = 0; i < results.length; i++) {
//                    String movieStr = results[i];
////                Log.v(LOG_TAG, "movie: " + movieStr);
//                    String[] arrayMovie = movieStr.split("||");
//                    Log.v(LOG_TAG, "arrayMovie: " + arrayMovie.toString());
//                    Movie movie = new Movie(arrayMovie[0], arrayMovie[1], arrayMovie[2], arrayMovie[3], arrayMovie[4]);
//                    movieList.add(movie);
//                }
//            }
//            imageAdapter.setMovies(movieList);
//        }
//
//        private String[] getMoviesDataFromJson(String moviesJsonStr) throws JSONException {
//
//            final String RESULTS_LIST = "results";
//            final String ORIGINAL_TITLE = "original_title";
//            final String POSTER_PATH = "poster_path";
//            final String OVERVIEW = "overview";
//            final String VOTE_AVERAGE = "vote_average";
//            final String RELEASE_DATE = "release_date";
//
//            JSONObject moviesJson = new JSONObject(moviesJsonStr);
//            JSONArray moviesArray = moviesJson.getJSONArray(RESULTS_LIST);
//
//            String[] resultStrs = new String[moviesArray.length()];
//
//            for (int i = 0; i < moviesArray.length(); i++) {
//
//                JSONObject movieJSONObject = moviesArray.getJSONObject(i);
//                String original_title = movieJSONObject.getString(ORIGINAL_TITLE);
//                String poster_path = movieJSONObject.getString(POSTER_PATH);
//                String overview = movieJSONObject.getString(OVERVIEW);
//                String vote_average = movieJSONObject.getString(VOTE_AVERAGE);
//                String release_date = movieJSONObject.getString(RELEASE_DATE);
//                resultStrs[i] = original_title + "||" + poster_path + "||" + overview + "||" + vote_average + "||" + release_date;
//            }
//
//            return resultStrs;
//        }
//
//        public List<Movie> getMovieList() {
//            return movieList;
//        }
//    }

}
