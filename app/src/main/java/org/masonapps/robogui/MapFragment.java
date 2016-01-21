package org.masonapps.robogui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Bob on 12/31/2015.
 */
public class MapFragment extends Fragment {

    public MapFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        final MapView mapView = (MapView) view.findViewById(R.id.mapView);
        new GenerateMapTask(new WeakReference<>(mapView)).execute();
        return view;
    }
    
    private static class GenerateMapTask extends AsyncTask<Void, Void, ArrayList<RobotState>>{

        private static final int N = 500;
        private WeakReference<MapView> mapViewWeakReference;
        private final Random random;

        private GenerateMapTask(WeakReference<MapView> mapViewWeakReference) {
            this.mapViewWeakReference = mapViewWeakReference;
            random = new Random();
        }
        
        private float randomMovement(){
            return random.nextFloat() * 40f - 20f;
        }
        
        private float randomAngle(boolean fullCircle){
            return fullCircle ? random.nextInt(360) : random.nextInt(36) * 5 - 90;
        }
        
        private float randomReading(){
            if(random.nextFloat() < 0.4f){
                return random.nextInt(120) + 40;
            }else{
                return -1;
            }
        }

        @Override
        protected ArrayList<RobotState> doInBackground(Void... params) {
            ArrayList<RobotState> list = new ArrayList<>();
            for (int i = 0; i < N; i++) {
                list.add(new RobotState(randomMovement(), randomAngle(true), randomReading(), randomAngle(false), i));
            }
            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<RobotState> robotStates) {
            final MapView mapView = mapViewWeakReference.get();
            if(mapView != null){
                mapView.clearMap();
                mapView.addStates(robotStates);
            }else{
                robotStates.clear();
            }
        }
    }
}
