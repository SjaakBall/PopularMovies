package com.example.android.popularmovies;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class DetailActivity extends AppCompatActivity {

    private final String LOG_TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "FLOW DetailActivity.onCreate");
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_detail);
        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.v(LOG_TAG, "FLOW DetailActivity.onCreate ORIENTATION_LANDSCAPE");
            // If the screen is now in landscape mode, we can show the
            // dialog in-line with the list so we don't need this activity.
            finish();
            return;
        }

        if (savedInstanceState == null || getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.v(LOG_TAG, "FLOW DetailActivity.onCreate savedInstanceState == null");
            DetailActivityFragment details = new DetailActivityFragment();
            details.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction().add(R.id.details_fragment, details).commit();
        }

    }

}
