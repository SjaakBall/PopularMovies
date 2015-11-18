package com.example.android.popularmovies;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
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

public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    private final static String MOVIE_STATE = "movie_state";
    private Movie movie;
    private static int movieId;
    private ListView reviewListView;
    private ListView videoListView;

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

        if (getArguments() != null && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            movie = getArguments().getParcelable(MOVIE_STATE);
            assert movie != null;
            Log.v(LOG_TAG, "onCreateView OriginalTitle From Parcelable: " + movie.getOriginalTitle());
//            startTaskForVideosAndReviews();
            populateView(rootView, movie);
        }

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(MOVIE_STATE)) {
            Log.v(LOG_TAG, "FLOW DetailActivityFragment.onCreateView intent.hasExtra(MOVIE_STATE");
//            movie = intent.getParcelableExtra(MOVIE_STATE);
//            assert movie != null;
//            startTaskForVideosAndReviews();
            Log.v(LOG_TAG, "FLOW DetailActivityFragment.onCreateView movie.toString(): " + movie.toString());
            populateView(rootView, movie);
        }


        Button button = (Button) rootView.findViewById(R.id.button_favorite);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "View.OnClickListener onClick: ");
                if (movie != null) {
//                    movie = getArguments().getParcelable(MOVIE_STATE);
                    Log.v(LOG_TAG, "View.OnClickListener onClick movie is: " + movie.getOriginalTitle());
                    long movieId = addMovie(movie);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        Log.v(LOG_TAG, "FLOW DetailActivityFragment.onResume");
        super.onResume();
        //TODO
        //call task and within task populate view
    }

    private void startTaskForVideosAndReviews() {
        Log.v(LOG_TAG, "FLOW DetailActivityFragment.startTaskForVideosAndReviews");
        //get movie videos and reviews
        //first check if available in database
        //else get from api with asynctask
        FetchMoviesVideosReviewsTask task = new FetchMoviesVideosReviewsTask();
        task.execute(String.valueOf(movie.getId()));

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

        assert movieCursor != null;
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

        reviewListView = (ListView) rootView.findViewById(R.id.reviewlistview);
        videoListView = (ListView) rootView.findViewById(R.id.videolistview);

        if (movie.getReviews() != null && movie.getReviews().size() > 0) {
            populateReviewListView(movie.getReviews());
        }
        if (movie.getReviews() != null && movie.getReviews().size() > 0) {
            populateVideoListView(movie.getVideos());
        }
//        Log.v(LOG_TAG, "FLOW movie.getVideos().size()" + movie.getVideos().size());
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
                for (int i = 0; i < results.length; i++) {
                    String movieStr = results[i];
                    if (movieStr.startsWith("|video|")) {
                        movieStr = movieStr.substring(7);
                        List<String> list = new ArrayList<String>(Arrays.asList(movieStr.split("-!--")));
                        Video video = new Video(list.get(0), list.get(1), list.get(2), list.get(3), list.get(4), list.get(5), list.get(6));
//                        Log.v(LOG_TAG, "video; " + video);
                        addVideoToDatabase(video);
                        videoList.add(video);
                    }
                    if (movieStr.startsWith("|review|")) {
                        movieStr = movieStr.substring(8);
                        List<String> list = new ArrayList<String>(Arrays.asList(movieStr.split("-!--")));
                        Review review = new Review(list.get(0), list.get(1), list.get(2), list.get(3), list.get(4));
//                        Log.v(LOG_TAG, "review: " + review);
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

            String videosJson = getVideos(movieId);
            String reviewsJson = getReviews(movieId);

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

        private String getReviews(String movieId) {
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

        private String getVideos(String movieId) {
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
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                videosJsonStr = buffer.toString();
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
            return videosJsonStr;
        }

        private String[] getVidoesDataFromJson(String moviesJsonStr) throws JSONException {
//            {
//                "id": 211672,
//                    "results": [
//                {
//                    "id": "54badb64c3a3684046001c73",
//                        "iso_639_1": "en",
//                        "key": "eisKxhjBnZ0",
//                        "name": "Minions Official Trailer #1 (2015) - Despicable Me Prequel HD",
//                        "site": "YouTube",
//                        "size": 720,
//                        "type": "Trailer"
//                },
//                {
//                    "id": "54e01475c3a36855c7002767",
//                        "iso_639_1": "en",
//                        "key": "Myv_Z8CReDU",
//                        "name": "Trailer 3",
//                        "site": "YouTube",
//                        "size": 720,
//                        "type": "Trailer"
//                }
//                ]
//            }
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
//                Log.v(LOG_TAG, "video resultStrs[i]" + resultStrs[i]);
            }
            return resultStrs;
        }

        private String[] getReviewsDataFromJson(String moviesJsonStr) throws JSONException {
//            {
//                "id": 211672,
//                    "page": 1,
//                    "results": [
//                {
//                    "id": "55a58e46c3a3682bb2000065",
//                        "author": "Andres Gomez",
//                        "content": "The minions are a nice idea and the animation and London recreation is really good, but that's about it.\r\n\r\nThe script is boring and the jokes not really funny.",
//                        "url": "http://j.mp/1HJpF2h"
//                },
//                {
//                    "id": "55e108c89251416c0b0006dd",
//                        "author": "movizonline.com",
//                        "content": "a nice idea and the animation.the new thing in animation field.a movie that every one should like an kid or old man.",
//                        "url": "http://j.mp/1NETbhK"
//                }
//                ],
//                "total_pages": 1,
//                    "total_results": 2
//            }
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
//                Log.v(LOG_TAG, "review resultStrs[i]" + resultStrs[i]);
            }
            return resultStrs;
        }


    }

    private void populateVideoListView(List<Video> videoList) {
        final VideoArrayAdapter adapter = new VideoArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, videoList);
        videoListView.setAdapter(adapter);
    }

    private void populateReviewListView(List<Review> reviewList) {
        final ReviewArrayAdapter adapter = new ReviewArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, reviewList);
        reviewListView.setAdapter(adapter);
    }

    private void addReviewToDatabase(Review review) {
        long reviewId;
        // First, check if the movie with this id exists in the db
        Cursor reviewCursor = getActivity().getContentResolver().query(
                MoviesContract.ReviewEntry.CONTENT_URI,
                new String[]{MoviesContract.ReviewEntry.COLUMN_LOC_KEY},
                MoviesContract.ReviewEntry.COLUMN_LOC_KEY + " = ?",
                new String[]{String.valueOf(review.getMovieId())},
                null);

        if (reviewCursor != null && reviewCursor.moveToFirst()) {
            int reviewIdIndex = reviewCursor.getColumnIndex(MoviesContract.ReviewEntry._ID);
            if (reviewIdIndex > 0) {
                reviewId = reviewCursor.getLong(reviewIdIndex);
            }
            //video exist so do nothing
        } else {
            // Now that the content provider is set up, inserting rows of data is pretty simple.
            // First create a ContentValues object to hold the data you want to insert.
            ContentValues reviewValues = new ContentValues();

            // Then add the data, along with the corresponding name of the data type,
            // so the content provider knows what kind of value is being inserted.
            reviewValues.put(MoviesContract.ReviewEntry.COLUMN_AUTHOR, review.getAuthor());
            reviewValues.put(MoviesContract.ReviewEntry.COLUMN_CONTENT, review.getContent());
            reviewValues.put(MoviesContract.ReviewEntry.COLUMN_ID, review.getId());
            reviewValues.put(MoviesContract.ReviewEntry.COLUMN_LOC_KEY, review.getMovieId());

            // Finally, insert movie data into the database.
            Uri insertedUri = getActivity().getContentResolver().insert(
                    MoviesContract.ReviewEntry.CONTENT_URI,
                    reviewValues
            );

            // The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
            reviewId = ContentUris.parseId(insertedUri);
        }

        assert reviewCursor != null;
        reviewCursor.close();
        // Wait, that worked?  Yes!
//        return reviewId;
    }

    private void addVideoToDatabase(Video video) {
        long videoId;
        // First, check if the movie with this id exists in the db
        Cursor videoCursor = getActivity().getContentResolver().query(
                MoviesContract.VideoEntry.CONTENT_URI,
                new String[]{MoviesContract.VideoEntry.COLUMN_LOC_KEY},
                MoviesContract.VideoEntry.COLUMN_LOC_KEY + " = ?",
                new String[]{String.valueOf(video.getMovieId())},
                null);
//        Cursor videoCursor = getActivity().getContentResolver().query(
//                MoviesContract.VideoEntry.CONTENT_URI,
//                null,
//                null,
//                null,
//                null);

        if (videoCursor != null && videoCursor.moveToFirst()) {
            int videoIdIndex = videoCursor.getColumnIndex(MoviesContract.VideoEntry._ID);
            if (videoIdIndex > 0 ) {
                videoId = videoCursor.getLong(videoIdIndex);
            }
            //video exist so do nothing
        } else {
            // Now that the content provider is set up, inserting rows of data is pretty simple.
            // First create a ContentValues object to hold the data you want to insert.
            ContentValues videoValues = new ContentValues();

            // Then add the data, along with the corresponding name of the data type,
            // so the content provider knows what kind of value is being inserted.
            videoValues.put(MoviesContract.VideoEntry.COLUMN_KEY, video.getKey());
            videoValues.put(MoviesContract.VideoEntry.COLUMN_NAME, video.getName());
            videoValues.put(MoviesContract.VideoEntry.COLUMN_TYPE, video.getType());
            videoValues.put(MoviesContract.VideoEntry.COLUMN_LOC_KEY, video.getMovieId());

            // Finally, insert movie data into the database.
            Uri insertedUri = getActivity().getContentResolver().insert(
                    MoviesContract.VideoEntry.CONTENT_URI,
                    videoValues
            );

            // The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
            videoId = ContentUris.parseId(insertedUri);
        }

        assert videoCursor != null;
        videoCursor.close();
        // Wait, that worked?  Yes!
//        return videoId;
    }
}
