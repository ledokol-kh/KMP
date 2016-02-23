package com.kostyabakay.kmp.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kostyabakay.kmp.R;

/**
 * Created by Kostya on 23.02.2016.
 */
public class AboutProjectFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about_project, container, false);
    }
}
