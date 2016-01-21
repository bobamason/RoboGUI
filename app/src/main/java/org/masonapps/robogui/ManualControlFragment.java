package org.masonapps.robogui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class ManualControlFragment extends Fragment {


    public ManualControlFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_manual_control, container, false);
        final ManualControlView manualControlView = (ManualControlView) view.findViewById(R.id.manualControlView);
        return view;
    }

}
