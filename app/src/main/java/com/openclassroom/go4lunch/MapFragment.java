package com.openclassroom.go4lunch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by de Meeûs Augustin on 2020-01-20
 **/
public class MapFragment extends Fragment implements OnMapReadyCallback{


    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private final int DEFAULT_ZOOM = 18;

    // Responsible for fetching the current location of the device
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlacesClient placesClient;
    private MapView mapView;
    private GoogleMap map;
    private boolean mLocationPermissionGranted;

    public final static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION =1;
    private LatLng mDefaultLocation;
    private Location mLastKnownLocation;
    public String returnedReponse = "";
    private FloatingActionButton locateUseerButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        Places.initialize(getActivity(), getResources().getString(R.string.map_key));
        placesClient = Places.createClient(getActivity());
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

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(false);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }

        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                mFusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            map.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_style));
                            mLastKnownLocation = task.getResult();
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));

                            getNearbyLocation(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());

                        } else {

                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            Log.d("mLastKnowLocation", "" + false);
                        }
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
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

        // Do other setup activities here too, as described elsewhere in this tutorial.

        getLocationPermission();
        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        locateUseerButton = getView().findViewById(R.id.float_button_locate_user);
        locateUseerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();
            }
        });



    }

    public void getNearbyLocation(double lat, double lon){
        // Instantiate the RequestQueue.

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url ="https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + lat
                + "," + lon + "&radius=1500&type=restaurant&key="
                + getResources().getString(R.string.map_key);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        returnedReponse = response;
                        Log.d("JSON", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray results = jsonObject.getJSONArray("results");
                            for(int i = 0; i < results.length(); i++){
                                double lat = (Double) results.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").get("lat");
                                double lng = (Double) results.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").get("lng");


                                MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lng));
                                Bitmap bMap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.red_marker);

                                marker.icon(BitmapDescriptorFactory.fromBitmap(getResizedBitmap(bMap, (int) (bMap.getWidth()*1.2), (int) (bMap.getHeight()*1.2))));

                                map.addMarker(marker);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Jsons", error.toString());
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);


    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
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
