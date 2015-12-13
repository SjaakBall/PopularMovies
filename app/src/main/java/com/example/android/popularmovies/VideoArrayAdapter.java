package com.example.android.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

/**
 * Created by JanHerman on 16/11/2015.
 */
public class VideoArrayAdapter extends ArrayAdapter<Video> {

    private HashMap<Video, Integer> mIdMap = new HashMap<Video, Integer>();

//    public VideoArrayAdapter(Context context, int resource, List<Video> objects) {
//        super(context, resource, objects);
//        for (int i = 0; i < objects.size(); ++i) {
//            mIdMap.put(objects.get(i), i);
//        }
//    }

    private final Context context;
    private final List<Video> values;

    public VideoArrayAdapter(Context context, List<Video> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.movie_trailer_item, parent, false);
//        TextView textView = (TextView) rowView.findViewById(R.id.secondLine);
//        TextView textView1 = (TextView) rowView.findViewById(R.id.firstLine);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
//        textView1.setText(values.get(position).getName());
//        textView.setText("http://img.youtube.com/vi/" + values.get(position).getKey()+"/0.jpg");
        Picasso.with(context).load("http://img.youtube.com/vi/" + values.get(position).getKey() + "/0.jpg").into(imageView);

        return rowView;


//        return super.getView(position, convertView, parent);
    }

//    @Override
//    public long getItemId(int position) {
//        Video item = getItem(position);
//        return mIdMap.get(item);
//    }
//
//    @Override
//    public boolean hasStableIds() {
//        return true;
//    }
}
