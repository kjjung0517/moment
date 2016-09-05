package com.skjin.dev.moment.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skjin.dev.moment.R;

/**
 * Created by kjjung on 16. 8. 29..
 */
public class BlankFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        return inflater.inflate(R.layout.fragment_blank, container, false);
    }
}
