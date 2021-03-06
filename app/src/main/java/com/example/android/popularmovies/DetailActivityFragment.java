package com.example.android.popularmovies;

import android.app.Fragment;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.popularmovies.data.MoviesContract;
import com.squareup.picasso.Picasso;

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

public class DetailActivityFragment extends Fragment {
    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    private final static String MOVIE_STATE = "movie_state";
    private final static String FAVORITES_STATE = "favorites_state";
    private Movie movie;
    private static int movieId;
    private LinearLayout mMovieTrailerContainer;
    private LinearLayout mMovieReviewContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "FLOW DetailActivityFragment.onCreate");
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.v(LOG_TAG, "FLOW DetailActivityFragment.onCreate ORIENTATION_LANDSCAPE");
            if (getArguments() != null) {
                movie = getArguments().getParcelable(MOVIE_STATE);
                assert movie != null;
                Log.v(LOG_TAG, "onCreate OriginalTitle From Parcelable: " + movie.getOriginalTitle());
                movieId = movie.getId();
                Log.v(LOG_TAG, "FLOW movieId to create API call: " + movieId);
                startTaskForVideosAndReviews();
            }
        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.v(LOG_TAG, "FLOW DetailActivityFragment.onCreate ORIENTATION_PORTRAIT");
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra(MOVIE_STATE)) {
                Log.v(LOG_TAG, "FLOW DetailActivityFragment.onCreate intent.hasExtra(MOVIE_STATE");
                movie = intent.getParcelableExtra(MOVIE_STATE);
                assert movie != null;
                movieId = movie.getId();
                Log.v(LOG_TAG, "FLOW movieId to create API call: " + movieId);
                startTaskForVideosAndReviews();
            }
            if (getArguments() != null) {
                movie = getArguments().getParcelable(MOVIE_STATE);
                assert movie != null;
                Log.v(LOG_TAG, "onCreate OriginalTitle From Parcelable: " + movie.getOriginalTitle());
                movieId = movie.getId();
                Log.v(LOG_TAG, "FLOW movieId to create API call: " + movieId);
                startTaskForVideosAndReviews();
            }
        }
        setHasOptionsMenu(true);
    }

    public static DetailActivityFragment newInstance(int index, Movie movie) {
        Log.v(LOG_TAG, "FLOW DetailActivityFragment.newInstance");
        DetailActivityFragment f = new DetailActivityFragment();

        Bundle args = new Bundle();
        args.putInt("index", index);
        args.putParcelable(MOVIE_STATE, movie);
        movieId = movie.getId();
        Log.v(LOG_TAG, "FLOW DetailActivityFragment.newInstance movieId to create API call: " + movieId);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(LOG_TAG, "FLOW DetailActivityFragment.onCreateView");

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mMovieTrailerContainer = (LinearLayout) rootView.findViewById(R.id.trailers_container);
        mMovieReviewContainer = (LinearLayout) rootView.findViewById(R.id.reviews_container);

        if (getArguments() != null && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.v(LOG_TAG, "FLOW DetailActivityFragment.onCreateView Configuration.ORIENTATION_LANDSCAPE");
            Log.v(LOG_TAG, "onCreateView OriginalTitle From Parcelable: " + movie.getOriginalTitle());
            populateView(rootView, movie);
        }

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(MOVIE_STATE)) {
            Log.v(LOG_TAG, "FLOW DetailActivityFragment.onCreateView intent.hasExtra(MOVIE_STATE");
            Log.v(LOG_TAG, "FLOW DetailActivityFragment.onCreateView movie.toString(): " + movie.toString());
            populateView(rootView, movie);
        }


        Button button = (Button) rootView.findViewById(R.id.button_favorite);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "View.OnClickListener onClick: ");
                if (movie != null) {
                    Log.v(LOG_TAG, "View.OnClickListener onClick movie is: " + movie.getOriginalTitle());
                    addMovie(movie);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.settings_menu, menu);
    }

    @Override
    public void onResume() {
        Log.v(LOG_TAG, "FLOW DetailActivityFragment.onResume");
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Log.v(LOG_TAG, "FLOW MainActivity.onOptionsItemSelected action_settings");
                startActivity(new Intent(getActivity(), SettingsActivity.class).putExtra(FAVORITES_STATE, doesDatabaseContainMoviesData()));
                return true;

            case R.id.action_play:
                Log.v(LOG_TAG, "FLOW MainActivity.onOptionsItemSelected action_play");
                if (movieId != 0 && movieId > 0) {
                    Cursor videoCursor = getActivity().getContentResolver().query(
                            MoviesContract.VideoEntry.CONTENT_URI,
                            new String[]{MoviesContract.VideoEntry.COLUMN_KEY},
                            MoviesContract.VideoEntry.COLUMN_LOC_KEY + " = ?",
                            new String[]{String.valueOf(movieId)},
                            null);

                    if (videoCursor != null && videoCursor.moveToFirst()) {
                        String key = videoCursor.getString(videoCursor.getColumnIndex(MoviesContract.VideoEntry.COLUMN_KEY));
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
                        startActivity(intent);
                    }
                    assert videoCursor != null;
                    videoCursor.close();
                }
                return true;

            default:

                return super.onOptionsItemSelected(item);

        }
    }

    private void startTaskForVideosAndReviews() {
        Log.v(LOG_TAG, "FLOW DetailActivityFragment.startTaskForVideosAndReviews");
        if (!doesDatabaseContainReviewData(movie.getId()) || !doesDatabaseContainVideoData(movie.getId())) {
            FetchMoviesVideosReviewsTask task = new FetchMoviesVideosReviewsTask();
            task.execute(String.valueOf(movie.getId()));
        } else {
            if (doesDatabaseContainReviewData(movie.getId())) {
                movie.setReviews(getReviewsFromDatabase(movie.getId()));
            }
            if (doesDatabaseContainVideoData(movie.getId())) {
                movie.setVideos(getVideosFromDatabase(movie.getId()));
            }
        }
    }

    private List<Review> getReviewsFromDatabase(int movieId) {
        Cursor reviewCursor = getActivity().getContentResolver().query(
                MoviesContract.ReviewEntry.CONTENT_URI,
                null,
                MoviesContract.ReviewEntry.COLUMN_LOC_KEY + " = ?",
                new String[]{String.valueOf(movieId)},
                null);
        List<Review> reviewList = new ArrayList<Review>();
        try {
            assert reviewCursor != null;
            for (boolean hasItem = reviewCursor.moveToFirst(); hasItem; hasItem = reviewCursor.moveToNext()) {
                Review review = new Review(reviewCursor.getString(reviewCursor.getColumnIndex(MoviesContract.ReviewEntry.COLUMN_LOC_KEY)),
                        reviewCursor.getString(reviewCursor.getColumnIndex(MoviesContract.ReviewEntry._ID)),
                        reviewCursor.getString(reviewCursor.getColumnIndex(MoviesContract.ReviewEntry.COLUMN_AUTHOR)),
                        reviewCursor.getString(reviewCursor.getColumnIndex(MoviesContract.ReviewEntry.COLUMN_CONTENT)),
                        reviewCursor.getString(reviewCursor.getColumnIndex(MoviesContract.ReviewEntry.COLUMN_URL))
                );
                reviewList.add(review);
            }
        } finally {
            assert reviewCursor != null;
            reviewCursor.close();
        }
        return reviewList;
    }

    private List<Video> getVideosFromDatabase(int movieId) {

        Cursor videoCursor = getActivity().getContentResolver().query(
                MoviesContract.VideoEntry.CONTENT_URI,
                null,
                MoviesContract.VideoEntry.COLUMN_LOC_KEY + " = ?",
                new String[]{String.valueOf(movieId)},
                null);
        List<Video> videoList = new ArrayList<Video>();
        try {
            assert videoCursor != null;
            for (boolean hasItem = videoCursor.moveToFirst(); hasItem; hasItem = videoCursor.moveToNext()) {
                Video video = new Video(videoCursor.getString(videoCursor.getColumnIndex(MoviesContract.VideoEntry.COLUMN_LOC_KEY)),
                        videoCursor.getString(videoCursor.getColumnIndex(MoviesContract.VideoEntry._ID)),
                        videoCursor.getString(videoCursor.getColumnIndex(MoviesContract.VideoEntry.COLUMN_KEY)),
                        videoCursor.getString(videoCursor.getColumnIndex(MoviesContract.VideoEntry.COLUMN_NAME)),
                        videoCursor.getString(videoCursor.getColumnIndex(MoviesContract.VideoEntry.COLUMN_SITE)),
                        videoCursor.getString(videoCursor.getColumnIndex(MoviesContract.VideoEntry.COLUMN_SIZE)),
                        videoCursor.getString(videoCursor.getColumnIndex(MoviesContract.VideoEntry.COLUMN_TYPE))
                );
                videoList.add(video);
            }
        } finally {
            assert videoCursor != null;
            videoCursor.close();
        }
        return videoList;
    }

    private boolean doesDatabaseContainMoviesData() {
        Log.v(LOG_TAG, "FLOW DetailActivityFragment.doesDatabaseContainMoviesData");
        boolean favoritesAvailable = false;

        Cursor moviesCursor = getActivity().getContentResolver().query(
                MoviesContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        if (moviesCursor != null && moviesCursor.moveToFirst()) {
            String id = moviesCursor.getString(moviesCursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_ID));
            if (id != null && id.length()>0) {
                favoritesAvailable = true;
            }
        }
        Log.v(LOG_TAG, "FLOW DetailActivityFragment.doesDatabaseContainMoviesData favorites Available: " + favoritesAvailable);

        assert moviesCursor != null;
        moviesCursor.close();

        return favoritesAvailable;
    }

    private boolean doesDatabaseContainVideoData(int movieId) {
        Log.v(LOG_TAG, "FLOW DetailActivityFragment.doesDatabaseContainVideoData");
        boolean videoExists = false;

        Cursor videoCursor = getActivity().getContentResolver().query(
                MoviesContract.VideoEntry.CONTENT_URI,
                new String[]{MoviesContract.VideoEntry.COLUMN_LOC_KEY},
                MoviesContract.VideoEntry.COLUMN_LOC_KEY + " = ?",
                new String[]{String.valueOf(movieId)},
                null);

        if (videoCursor != null && videoCursor.moveToFirst()) {
            long loc_key = videoCursor.getLong(videoCursor.getColumnIndex(MoviesContract.VideoEntry.COLUMN_LOC_KEY));
            if (loc_key > 0) {
                videoExists = true;
            }
        }
        Log.v(LOG_TAG, "FLOW DetailActivityFragment.doesDatabaseContainVideoReviewData video exists: " + videoExists);

        assert videoCursor != null;
        videoCursor.close();

        return videoExists;
    }

    private boolean doesDatabaseContainReviewData(int movieId) {
        Log.v(LOG_TAG, "FLOW DetailActivityFragment.doesDatabaseContainReviewData");
        boolean reviewExists = false;
        Cursor reviewCursor = getActivity().getContentResolver().query(
                MoviesContract.ReviewEntry.CONTENT_URI,
                new String[]{MoviesContract.ReviewEntry.COLUMN_LOC_KEY},
                MoviesContract.ReviewEntry.COLUMN_LOC_KEY + " = ?",
                new String[]{String.valueOf(movieId)},
                null);

        if (reviewCursor != null && reviewCursor.moveToFirst()) {
            long loc_key = reviewCursor.getLong(reviewCursor.getColumnIndex(MoviesContract.ReviewEntry.COLUMN_LOC_KEY));
            if (loc_key > 0) {
                reviewExists = true;
            }
        }
        Log.v(LOG_TAG, "FLOW DetailActivityFragment.doesDatabaseContainVideoReviewData review exists: " + reviewExists);

        assert reviewCursor != null;
        reviewCursor.close();

        return reviewExists;
    }

    private long addMovie(Movie movie) {
        long movieId;
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
            ContentValues movieValues = new ContentValues();

            movieValues.put(MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE, movie.getOriginalTitle());
            movieValues.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
            movieValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
            movieValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
            movieValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
            movieValues.put(MoviesContract.MovieEntry.COLUMN_ID, movie.getId());

            Uri insertedUri = getActivity().getContentResolver().insert(
                    MoviesContract.MovieEntry.buildMoviesUri(movie.getId()),
                    movieValues
            );

            movieId = ContentUris.parseId(insertedUri);
        }

        assert movieCursor != null;
        movieCursor.close();

        return movieId;
    }

    private void populateView(View rootView, Movie movie) {
        Log.v(LOG_TAG, "FLOW DetailActivityFragment.populateView");
        ((TextView) rootView.findViewById(R.id.detail_title)).setText(movie.getOriginalTitle());
        ((TextView) rootView.findViewById(R.id.detail_plot_synopsis)).setText(movie.getOverview());
        ((TextView) rootView.findViewById(R.id.detail_user_rating)).setText(movie.getVoteAverage());
        ((TextView) rootView.findViewById(R.id.detail_release_date)).setText(movie.getReleaseDate());
        ImageView imageView = (ImageView) rootView.findViewById(R.id.detail_imageView);
        Picasso.with(getActivity())
                .load("http://image.tmdb.org/t/p/w185/" + movie.getPosterPath())
                .into(imageView);

        if (movie.getReviews() != null && movie.getReviews().size() > 0) {
            populateReviewListView(movie.getReviews());
        }
        if (movie.getReviews() != null && movie.getReviews().size() > 0) {
            populateVideoListView(movie.getVideos());
        }
    }

    public class FetchMoviesVideosReviewsTask extends AsyncTask<String, Void, String[]> {
        private final String LOG_TAG = FetchMoviesVideosReviewsTask.class.getSimpleName();

        private final String PERSONAL_API_KEY = getResources().getString(R.string.movie_api_key);

        @Override
        protected void onPostExecute(String[] results) {
            super.onPostExecute(results);

            Log.v(LOG_TAG, "FLOW FetchMoviesVideosReviewsTask.onPostExecute");

            List<Video> videoList = null;
            List<Review> reviewList = null;
            if (results != null) {
                videoList = new ArrayList<Video>();
                reviewList = new ArrayList<Review>();
                for (String result : results) {
                    String movieStr = result;
                    if (movieStr.startsWith("|video|")) {
                        movieStr = movieStr.substring(7);
                        List<String> list = new ArrayList<String>(Arrays.asList(movieStr.split("-!--")));
                        Video video = new Video(list.get(0), list.get(1), list.get(2), list.get(3), list.get(4), list.get(5), list.get(6));
                        addVideoToDatabase(video);
                        videoList.add(video);
                    }
                    if (movieStr.startsWith("|review|")) {
                        movieStr = movieStr.substring(8);
                        List<String> list = new ArrayList<String>(Arrays.asList(movieStr.split("-!--")));
                        Review review = new Review(list.get(0), list.get(1), list.get(2), list.get(3), list.get(4));
                        Log.v(LOG_TAG, "review: " + review);
                        addReviewToDatabase(review);
                        reviewList.add(review);
                    }
                }
            }

            if (videoList != null && videoList.size() > 0) {
                movie.setVideos(videoList);
            }
            if (reviewList != null && reviewList.size() > 0) {
                movie.setReviews(reviewList);
            }

            populateReviewListView(reviewList);
            populateVideoListView(videoList);
        }

        private URL buildUriVideos(String movieId) {
            try {
                final String BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String VIDEOS_QUERY_PARAM = "videos";
                final String KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(movieId)
                        .appendPath(VIDEOS_QUERY_PARAM)
                        .appendQueryParameter(KEY_PARAM, PERSONAL_API_KEY)
                        .build();
                Log.e(LOG_TAG, builtUri.toString());
                return new URL(builtUri.toString());
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Error " + e);
                return null;
            }
        }

        private URL buildUriReviews(String movieId) {
            try {
                final String BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String REVIEWS_QUERY_PARAM = "reviews";
                final String KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(movieId)
                        .appendPath(REVIEWS_QUERY_PARAM)
                        .appendQueryParameter(KEY_PARAM, PERSONAL_API_KEY)
                        .build();
                Log.e(LOG_TAG, builtUri.toString());
                return new URL(builtUri.toString());
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Error " + e);
                return null;
            }
        }

        @Override
        protected String[] doInBackground(String... params) {
            Log.v(LOG_TAG, "FLOW FetchMoviesVideosReviewsTask.doInBackground");
            String movieId = params[0];

            String videosJson = getVideosFromAPICall(movieId);
            String reviewsJson = getReviewsFromAPICall(movieId);

            try {
                String[] videos = getVidoesDataFromJson(videosJson);
                String[] reviews = getReviewsDataFromJson(reviewsJson);
                String[] result = new String[videos.length + reviews.length];
                System.arraycopy(videos, 0, result, 0, videos.length);
                System.arraycopy(reviews, 0, result, videos.length, reviews.length);

                return result;
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }
            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        private String getReviewsFromAPICall(String movieId) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String reviewsJsonStr = null;
            URL url;
            try {
                url = new URL(buildUriReviews(movieId).toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                reviewsJsonStr = buffer.toString();
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
            return reviewsJsonStr;
        }

        private String getVideosFromAPICall(String movieId) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String videosJsonStr = null;
            URL url;
            try {
                url = new URL(buildUriVideos(movieId).toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                videosJsonStr = buffer.toString();
            } catch (MalformedURLException mue) {
                Log.e(LOG_TAG, "Error ", mue);
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
            return videosJsonStr;
        }

        private String[] getVidoesDataFromJson(String moviesJsonStr) throws JSONException {
            final String RESULTS_LIST = "results";
            final String ID = "id";
            final String KEY = "key";
            final String NAME = "name";
            final String SITE = "site";
            final String SIZE = "size";
            final String TYPE = "type";
            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(RESULTS_LIST);
            String movieId = moviesJson.getString(ID) != null && moviesJson.getString(ID).length() > 0 ? moviesJson.getString(ID) : "no id value";

            String[] resultStrs = new String[moviesArray.length()];

            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject movieJSONObject = moviesArray.getJSONObject(i);
                String id = movieJSONObject.getString(ID) != null && movieJSONObject.getString(ID).length() > 0 ? movieJSONObject.getString(ID) : "no id value";
                String key = movieJSONObject.getString(KEY) != null && movieJSONObject.getString(KEY).length() > 0 ? movieJSONObject.getString(KEY) : "no key value";
                String name = movieJSONObject.getString(NAME) != null && movieJSONObject.getString(NAME).length() > 0 ? movieJSONObject.getString(NAME) : "no name value";
                String site = movieJSONObject.getString(SITE) != null && movieJSONObject.getString(SITE).length() > 0 ? movieJSONObject.getString(SITE) : "no site value";
                String size = movieJSONObject.getString(SIZE) != null && movieJSONObject.getString(SIZE).length() > 0 ? movieJSONObject.getString(SIZE) : "no size value";
                String type = movieJSONObject.getString(TYPE) != null && movieJSONObject.getString(TYPE).length() > 0 ? movieJSONObject.getString(TYPE) : "no type value";
                resultStrs[i] = "|video|" + movieId + "-!--" + id + "-!--" + key + "-!--" + name + "-!--" + site + "-!--" + size + "-!--" + type;
            }
            return resultStrs;
        }

        private String[] getReviewsDataFromJson(String moviesJsonStr) throws JSONException {
            final String RESULTS_LIST = "results";
            final String ID = "id";
            final String AUTHOR = "author";
            final String CONTENT = "content";
            final String URL = "url";
            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(RESULTS_LIST);
            String movieId = moviesJson.getString(ID) != null && moviesJson.getString(ID).length() > 0 ? moviesJson.getString(ID) : "no id value";

            String[] resultStrs = new String[moviesArray.length()];

            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject movieJSONObject = moviesArray.getJSONObject(i);
                String id = movieJSONObject.getString(ID) != null && movieJSONObject.getString(ID).length() > 0 ? movieJSONObject.getString(ID) : "no id value";
                String author = movieJSONObject.getString(AUTHOR) != null && movieJSONObject.getString(AUTHOR).length() > 0 ? movieJSONObject.getString(AUTHOR) : "no author value";
                String content = movieJSONObject.getString(CONTENT) != null && movieJSONObject.getString(CONTENT).length() > 0 ? movieJSONObject.getString(CONTENT) : "no content value";
                String url = movieJSONObject.getString(URL) != null && movieJSONObject.getString(URL).length() > 0 ? movieJSONObject.getString(URL) : "no url value";
                resultStrs[i] = "|review|" + movieId + "-!--" + id + "-!--" + author + "-!--" + content + "-!--" + url;
            }
            return resultStrs;
        }


    }

    private void populateVideoListView(List<Video> videoList) {

        for (final Video video : videoList) {
//            Log.v(LOG_TAG, "FLOW video " + video.getName());
            View mMovieTrailerItem = LayoutInflater.from(getActivity()).inflate(R.layout.movie_trailer_item, null);

            mMovieTrailerItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playYoutubeTrailerIntent(video.getKey());
                }
            });

            TextView mMovieTrailerTitle = (TextView) mMovieTrailerItem.findViewById(R.id.movie_trailer_text_title);
            mMovieTrailerTitle.setText(video.getName());

            ImageView imageView = (ImageView) mMovieTrailerItem.findViewById(R.id.movie_trailer_image_play);
            Picasso.with(getActivity()).load("http://img.youtube.com/vi/" + video.getKey() + "/0.jpg").into(imageView);

            mMovieTrailerContainer.addView(mMovieTrailerItem);
        }
    }

    private void playYoutubeTrailerIntent(String key) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
        startActivity(intent);
    }

    private void populateReviewListView(List<Review> reviewList) {

        for (final Review review : reviewList) {
//            Log.v(LOG_TAG, "FLOW review " + review.getAuthor());
            View mMovieReviewItem = LayoutInflater.from(getActivity()).inflate(R.layout.movie_review_item, null);

            TextView mMovieTrailerTitle = (TextView) mMovieReviewItem.findViewById(R.id.movie_review_text);
            mMovieTrailerTitle.setText(review.getContent());

            mMovieReviewContainer.addView(mMovieReviewItem);
        }
    }

    private void addReviewToDatabase(Review review) {

        Cursor reviewCursor = getActivity().getContentResolver().query(
                MoviesContract.ReviewEntry.CONTENT_URI,
                new String[]{MoviesContract.ReviewEntry.COLUMN_LOC_KEY},
                MoviesContract.ReviewEntry.COLUMN_LOC_KEY + " = ?",
                new String[]{String.valueOf(review.getMovieId())},
                null);

        if (reviewCursor != null &&
                reviewCursor.moveToFirst() &&
                reviewCursor.getLong(reviewCursor.getColumnIndex(MoviesContract.ReviewEntry.COLUMN_LOC_KEY)) > 0) {
        } else {
            ContentValues reviewValues = new ContentValues();

            reviewValues.put(MoviesContract.ReviewEntry.COLUMN_AUTHOR, review.getAuthor());
            reviewValues.put(MoviesContract.ReviewEntry.COLUMN_CONTENT, review.getContent());
            reviewValues.put(MoviesContract.ReviewEntry.COLUMN_ID, review.getId());
            reviewValues.put(MoviesContract.ReviewEntry.COLUMN_LOC_KEY, review.getMovieId());
            reviewValues.put(MoviesContract.ReviewEntry.COLUMN_URL, review.getUrl());

            Uri insertedUri = getActivity().getContentResolver().insert(
                    MoviesContract.ReviewEntry.CONTENT_URI,
                    reviewValues
            );
            ContentUris.parseId(insertedUri);
        }

        assert reviewCursor != null;
        reviewCursor.close();
    }

    private void addVideoToDatabase(Video video) {

        Cursor videoCursor = getActivity().getContentResolver().query(
                MoviesContract.VideoEntry.CONTENT_URI,
                new String[]{MoviesContract.VideoEntry.COLUMN_LOC_KEY},
                MoviesContract.VideoEntry.COLUMN_LOC_KEY + " = ?",
                new String[]{String.valueOf(video.getMovieId())},
                null);

        if (videoCursor != null &&
                videoCursor.moveToFirst() &&
                videoCursor.getLong(videoCursor.getColumnIndex(MoviesContract.VideoEntry.COLUMN_LOC_KEY)) > 0) {
        } else {
            ContentValues videoValues = new ContentValues();

            videoValues.put(MoviesContract.VideoEntry.COLUMN_KEY, video.getKey());
            videoValues.put(MoviesContract.VideoEntry.COLUMN_NAME, video.getName());
            videoValues.put(MoviesContract.VideoEntry.COLUMN_TYPE, video.getType());
            videoValues.put(MoviesContract.VideoEntry.COLUMN_LOC_KEY, video.getMovieId());
            videoValues.put(MoviesContract.VideoEntry.COLUMN_SITE, video.getSite());
            videoValues.put(MoviesContract.VideoEntry.COLUMN_SIZE, video.getSize());

            Uri insertedUri = getActivity().getContentResolver().insert(
                    MoviesContract.VideoEntry.CONTENT_URI,
                    videoValues
            );
            ContentUris.parseId(insertedUri);
        }

        assert videoCursor != null;
        videoCursor.close();
    }
}