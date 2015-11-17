package com.example.android.popularmovies;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.HashMap;
import java.util.List;

/**
 * Created by JanHerman on 16/11/2015.
 */
public class VideoArrayAdapter extends ArrayAdapter<Video> {

    private HashMap<Video, Integer> mIdMap = new HashMap<Video, Integer>();

    public VideoArrayAdapter(Context context, int resource, List<Video> objects) {
        super(context, resource, objects);
        for (int i = 0; i < objects.size(); ++i) {
            mIdMap.put(objects.get(i), i);
        }
    }

    @Override
    public long getItemId(int position) {
        Video item = getItem(position);
        return mIdMap.get(item);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
