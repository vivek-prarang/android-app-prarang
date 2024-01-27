package com.riversanskiriti.prarang;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class TourFragment extends Fragment {

    private int imageId;
    private String title, content;

    private TextView titleTextView, contentTextView;
    private ImageView imageView;

    public TourFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tour, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        imageId = bundle.getInt("imageid");
        title = bundle.getString("title");
        content = bundle.getString("content");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        titleTextView = (TextView) getView().findViewById(R.id.titleTextView);
        contentTextView = (TextView) getView().findViewById(R.id.contentTextView);
        imageView = (ImageView) getView().findViewById(R.id.imageView);

        titleTextView.setText(title);
        contentTextView.setText(content);
        imageView.setImageResource(imageId);
    }
}
