package io.github.linxiaocong.sjtubbs.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.linxiaocong.sjtubbs.R;

/**
 * Created by linxiaocong on 2014/10/22.
 */
public class NewTopicFragment extends Fragment {

    public static Fragment newInstance() { return new NewTopicFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_topic, container, false);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_new_topic, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
