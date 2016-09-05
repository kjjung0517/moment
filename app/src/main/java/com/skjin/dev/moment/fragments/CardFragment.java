package com.skjin.dev.moment.fragments;

import android.app.Fragment;
import android.os.Bundle;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skjin.dev.moment.R;
import com.skjin.dev.moment.adapters.ClusterAdapter;

/**
 * Created by kjjung on 16. 8. 29..
 */
public class CardFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_card, container, false);

        RecyclerView recyclerView = (RecyclerView)rootView.findViewById(R.id.card);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this.getActivity(), 2));

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int imageWidth = metrics.widthPixels / 2;
        recyclerView.setAdapter(new ClusterAdapter(imageWidth));

        return rootView;
    }
}
