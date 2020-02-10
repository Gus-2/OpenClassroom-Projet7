package com.openclassroom.go4lunch.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.openclassroom.go4lunch.R;
import com.openclassroom.go4lunch.models.Example;
import com.openclassroom.go4lunch.models.Result;
import com.openclassroom.go4lunch.utils.SecurityChecks;


/**
 * Created by de Meeûs Augustin on 2020-01-20
 **/
public class MapFragment extends Fragment implements OnMapReadyCallback{


    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private final int DEFAULT_ZOOM = 18;
    private Bundle bundle;

    private MapView mapView;
    private GoogleMap map;

    private Example nearbyLocation;
    private boolean mLocationGranted;
    private Location mLastKnownLocation;



    private FloatingActionButton locateUseerButton;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SecurityChecks.CheckGooglePlayServices(getContext());

        bundle = getArguments();
        if( getArguments() == null){
            nearbyLocation = ((MainActivity) getActivity()).getNearbyLocations();
            mLocationGranted = ((MainActivity) getActivity()).getLocationGranted();
            mLastKnownLocation = ((MainActivity) getActivity()).getLocation();
        }else{
            nearbyLocation = getArguments().getParcelable("NearbyLocation");
            mLocationGranted = bundle.getBoolean("LocationGranted");
            mLastKnownLocation = bundle.getParcelable("Location");
        }


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, container, false);

        mapView = view.findViewById(R.id.map_view_fragment);
        initGoogleMap(savedInstanceState);
        return view;
    }

    private void initGoogleMap(Bundle savedInstanceState){
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mapView.onCreate(mapViewBundle);

        mapView.getMapAsync(this);
    }

    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (mLocationGranted) {
                map.setMyLocationEnabled(true);
                setUiMapSettings();
            } else {
                map.setMyLocationEnabled(false);

            }


        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public void setUiMapSettings(){
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.getUiSettings().setMapToolbarEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        updateLocationUI();

        displayTheLocateButton();

        displayTheRestaurantsNearby(googleMap);

        moveToWhereUserIsLocated();
    }

    public void displayTheRestaurantsNearby(GoogleMap map){
        Bitmap markerIcon = BitmapFactory.decodeResource(getResources(), R.drawable.red_marker);
        BitmapDescriptor markerIconDescriptor = BitmapDescriptorFactory.fromBitmap(markerIcon);
        for(Result result : nearbyLocation.getResults()){
            map.addMarker(new MarkerOptions().position(new LatLng(result.getGeometry().getLocation().getLat(),
                    result.getGeometry().getLocation().getLng()))).setIcon(markerIconDescriptor);
        }
    }

    public void displayTheLocateButton(){
        locateUseerButton = getView().findViewById(R.id.float_button_locate_user);
        locateUseerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToWhereUserIsLocated();
            }
        });
    }

    public void moveToWhereUserIsLocated(){
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_style));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}
