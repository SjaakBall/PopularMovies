package com.example.android.popularmovies;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.HashMap;
import java.util.List;

/**
 * Created by JanHerman on 16/11/2015.
 */
public class ReviewArrayAdapter extends ArrayAdapter<Review> {

    private HashMap<Review, Integer> mIdMap = new HashMap<Review, Integer>();

    public ReviewArrayAdapter(Context context, int resource, List<Review> objects) {
        super(context, resource, objects);
        for (int i = 0; i < objects.size(); ++i) {
            mIdMap.put(objects.get(i), i);
        }
    }

    @Override
    public long getItemId(int position) {
        Review item = getItem(position);
        return mIdMap.get(item);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
