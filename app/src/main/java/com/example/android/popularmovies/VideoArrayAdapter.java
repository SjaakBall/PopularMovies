package com.example.android.popularmovies;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
//        TextView textView = (TextView) rowView.findViewById(R.id.secondLine);
        TextView textView1 = (TextView) rowView.findViewById(R.id.firstLine);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        Button button  = (Button) rowView.findViewById(R.id.button_trailer);
//        Picasso.with(context).load("http://img.youtube.com/vi/" + values.get(position).getKey() + "/0.jpg").into(button);
        button.setText(values.get(position).getName());

//        textView1.setText(values.get(position).getName());
        textView1.setText("http://img.youtube.com/vi/" + values.get(position).getKey()+"/0.jpg");
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

    public class MyButton extends Button implements Target {

        public MyButton(Context context) {
            super(context);
        }

        public MyButton(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public MyButton(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public MyButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            setBackgroundDrawable(new BitmapDrawable(bitmap));
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    }
}
