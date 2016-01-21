package org.masonapps.robogui;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class CreatePathFragment extends Fragment {

    private PathView pathView;

    public CreatePathFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_path, container, false);
        pathView = (PathView) view.findViewById(R.id.pathView);
        return view;
    }
}
