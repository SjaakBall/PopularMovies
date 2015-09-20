package com.example.android.popularmovies;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class DetailActivity extends AppCompatActivity {

    private final String LOG_TAG = DetailActivity.class.getSimpleName();
    private final String DETAILFRAGMENT_TAG = "DFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "FLOW DetailActivity.onCreate");
        setContentView(R.layout.activity_detail);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // If the screen is now in landscape mode, we can show the
            // dialog in-line with the list so we don't need this activity.
            finish();
            return;
        }

        if (savedInstanceState == null) {
            // During initial setup, plug in the details fragment.
            DetailActivityFragment details = new DetailActivityFragment();
            details.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction().add(R.id.details_fragment, details, DETAILFRAGMENT_TAG).commit();
        }

    }

}
