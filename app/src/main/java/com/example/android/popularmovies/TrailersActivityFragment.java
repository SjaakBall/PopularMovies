package com.example.android.popularmovies;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TrailersActivityFragment extends Fragment {
    private static final String KEY_VIDEOS_LIST = "video_state";
    private static final String LOG_TAG = TrailersActivityFragment.class.getSimpleName();

    private List<Video> videoList;

    /**
     * @param videoList
     * @return TrailersActivityFragment instance
     */
    public static TrailersActivityFragment newInstance(List<Video> videoList) {
        Log.v(LOG_TAG, "FLOW TrailersActivityFragment.newInstance");

        TrailersActivityFragment fragment = new TrailersActivityFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(KEY_VIDEOS_LIST, (ArrayList<? extends Parcelable>) videoList);
        fragment.setArguments(args);
        return fragment;
    }

    public TrailersActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            videoList = getArguments().getParcelableArrayList(KEY_VIDEOS_LIST);
            assert videoList != null && videoList.size() > 0;
            Log.v(LOG_TAG, "FLOW TrailersActivityFragment.onCreate with getArguments().getParcelableArrayList");
        }
        Intent intent = getActivity().getIntent();
        if (intent!=null && intent.hasExtra(KEY_VIDEOS_LIST)){
            videoList = intent.getParcelableArrayListExtra(KEY_VIDEOS_LIST);
            assert videoList != null && videoList.size() > 0;
            Log.v(LOG_TAG, "FLOW TrailersActivityFragment.onCreate with intent.getParcelableArrayListExtra");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(LOG_TAG, "FLOW TrailersActivityFragment.onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_trailers, container, false);

        ListView videoListView = (ListView) rootView.findViewById(R.id.videolistview);
        if (videoList != null && videoList.size() > 0){
            final VideoArrayAdapter adapter = new VideoArrayAdapter(getActivity(), videoList);
            videoListView.setAdapter(adapter);
        }

        Button button_trailer = (Button) rootView.findViewById(R.id.button_trailer);
        if (button_trailer != null) {
            final TextView textView = (TextView) rootView.findViewById(R.id.firstLine);
            button_trailer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v(LOG_TAG, "FLOW button_trailer click");
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + textView.getText()));
                    startActivity(intent);
                }
            });
        }

        return rootView;
    }

}
