package com.openclassroom.go4lunch.ui.map;

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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.openclassroom.go4lunch.R;
import com.openclassroom.go4lunch.models.NearbyPlaces;
import com.openclassroom.go4lunch.ui.Go4Lunch;
import com.openclassroom.go4lunch.ui.detaileRestaurant.DetailsRestaurantFragment;
import com.openclassroom.go4lunch.utils.SecurityChecks;

import java.util.HashMap;


/**
 * Created by de Meeûs Augustin on 2020-01-20
 **/
public class MapFragment extends Fragment implements OnMapReadyCallback{


    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private final int DEFAULT_ZOOM = 18;
    private Bundle bundle;

    private MapView mapView;
    private GoogleMap map;

    private NearbyPlaces nearbyLocation;
    private boolean mLocationGranted;
    private Location mLastKnownLocation;
    private HashMap<String, String> markers;


    private FloatingActionButton locateUseerButton;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SecurityChecks.CheckGooglePlayServices(getContext());

        bundle = getArguments();
        if( bundle == null){
            nearbyLocation = ((Go4Lunch) getActivity()).getNearbyLocations();
            mLocationGranted = ((Go4Lunch) getActivity()).getLocationGranted();
            mLastKnownLocation = ((Go4Lunch) getActivity()).getLocation();
        }else{
            nearbyLocation = getArguments().getParcelable("NearbyLocation");
            mLocationGranted = bundle.getBoolean("LocationGranted");
            mLastKnownLocation = bundle.getParcelable("Location");
        }

        markers = new HashMap<>();
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

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Bundle bundle = new Bundle();
                bundle.putString("PlaceID", markers.get(marker.getId()));
                DetailsRestaurantFragment detailsRestaurantFragment = new DetailsRestaurantFragment();
                detailsRestaurantFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, detailsRestaurantFragment).commit();
                return  true;
            }
        });
    }

    public void displayTheRestaurantsNearby(GoogleMap map){
        Bitmap markerIcon = BitmapFactory.decodeResource(getResources(), R.drawable.red_marker);
        BitmapDescriptor markerIconDescriptor = BitmapDescriptorFactory.fromBitmap(markerIcon);
        for(int i = 0; i < nearbyLocation.getResults().size(); i++){
            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(nearbyLocation.getResults().get(i).getGeometry().getLocation().getLat(),
                    nearbyLocation.getResults().get(i).getGeometry().getLocation().getLng())).icon(markerIconDescriptor);
            Marker marker = map.addMarker(markerOptions);
            markers.put(marker.getId(), nearbyLocation.getResults().get(i).getPlaceId());
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
