package com.openclassroom.go4lunch.ui.map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.openclassroom.go4lunch.R;
import com.openclassroom.go4lunch.database.FirebaseHelper;
import com.openclassroom.go4lunch.models.DataUserConnected;
import com.openclassroom.go4lunch.models.DetailsPlaces;
import com.openclassroom.go4lunch.models.NearbyPlaces;
import com.openclassroom.go4lunch.models.Result;
import com.openclassroom.go4lunch.ui.Go4Lunch;
import com.openclassroom.go4lunch.ui.detaileRestaurant.DetailRestaurantActivity;
import com.openclassroom.go4lunch.utils.Checks;
import com.openclassroom.go4lunch.utils.ConstantString;
import com.openclassroom.go4lunch.utils.RestaurantDetailFormat;
import com.openclassroom.go4lunch.utils.SecurityChecks;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Created by de Meeûs Augustin on 2020-01-20
 **/
public class MapFragment extends Fragment implements OnMapReadyCallback{

    private ListenerRegistration listenerRegistration;
    private MapView mapView;
    private GoogleMap map;
    private List<DataUserConnected> dataUserConnecteds;
    private NearbyPlaces nearbyLocation;
    private Location mLastKnownLocation;
    private HashMap<String, String> markers;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SecurityChecks.checkGooglePlayServices(getContext());
        Bundle bundle = getArguments();
        if(bundle == null){
            nearbyLocation = ((Go4Lunch) Objects.requireNonNull(getActivity())).getNearbyLocations();
            mLastKnownLocation = ((Go4Lunch) getActivity()).getLocation();
        }else{
            nearbyLocation = getArguments().getParcelable(ConstantString.NEARBY_LOCATION);
            mLastKnownLocation = bundle.getParcelable(ConstantString.LOCATION);
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
            mapViewBundle = savedInstanceState.getBundle(ConstantString.MAPVIEW_BUNDLE_KEY);
        }
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
    }

    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            map.setMyLocationEnabled(true);
            setUiMapSettings();
        } catch (SecurityException e)  {
            e.printStackTrace();
        }
    }

    private void setUiMapSettings(){
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.getUiSettings().setMapToolbarEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        if(map != null){
            map.clear();
            getUserChoices(map);
        }
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
        listenerRegistration.remove();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        updateLocationUI();
        displayTheLocateButton();
        getUserChoices(googleMap);
        moveToWhereUserIsLocated();
        map.setOnMarkerClickListener(marker -> {
            int position = RestaurantDetailFormat.getPositionFromPlaceID(((Go4Lunch) Objects.requireNonNull(getActivity())).getNearbyLocations(), markers.get(marker.getId()));
            Intent intent = new Intent(getActivity(), DetailRestaurantActivity.class);
            DetailsPlaces detailPlace = RestaurantDetailFormat.getDetailPlacesFromPlaceID(((Go4Lunch) getActivity()).getDetailsPlaces(),((Go4Lunch) getActivity()).getNearbyLocations().getResults().get(position).getPlaceId());
            intent.putExtra(ConstantString.DETAIL_PLACE, detailPlace);
            Result result = ((Go4Lunch) getActivity()).getNearbyLocations().getResults().get(position);
            intent.putExtra(ConstantString.RESULT, result);
            startActivity(intent);
            return  true;
        });
    }

    private void getUserChoices(GoogleMap map){
        CollectionReference collectionReference = FirebaseHelper.getUserCollection();
        dataUserConnecteds = new ArrayList<>();
        listenerRegistration = collectionReference.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if(e != null) e.printStackTrace();
            dataUserConnecteds.clear();
            if(queryDocumentSnapshots != null){
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    dataUserConnecteds.add(documentSnapshot.toObject(DataUserConnected.class));
                }
                displayTheRestaurantsNearby(map);
            }
        });
    }

    private void displayTheRestaurantsNearby(GoogleMap map){

        Bitmap markerIconRed = BitmapFactory.decodeResource(getResources(), R.drawable.red_marker);
        BitmapDescriptor markerIconDescriptorRed = BitmapDescriptorFactory.fromBitmap(markerIconRed);

        Bitmap markerIconBlue = BitmapFactory.decodeResource(getResources(), R.drawable.blue_marker);
        BitmapDescriptor markerIconDescriptorBlue = BitmapDescriptorFactory.fromBitmap(markerIconBlue);

        for(int i = 0; i < nearbyLocation.getResults().size(); i++){
            boolean found = false;
            for(DataUserConnected dataUserConnected : dataUserConnecteds){
                if(dataUserConnected.getChoosenRestaurantForTheDay() != null && dataUserConnected.getChoosenRestaurantForTheDay().equals(nearbyLocation.getResults().get(i).getPlaceId()) && Checks.checkIfGoodDate(dataUserConnected.getDateOfTheChoosenRestaurant())){
                    MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(nearbyLocation.getResults().get(i).getGeometry().getLocation().getLat(),
                            nearbyLocation.getResults().get(i).getGeometry().getLocation().getLng())).icon(markerIconDescriptorBlue);
                    Marker marker = map.addMarker(markerOptions);
                    markers.put(marker.getId(), nearbyLocation.getResults().get(i).getPlaceId());
                    found = true;
                    break;
                }
            }
            if(!found){
                MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(nearbyLocation.getResults().get(i).getGeometry().getLocation().getLat(),
                        nearbyLocation.getResults().get(i).getGeometry().getLocation().getLng())).icon(markerIconDescriptorRed);
                Marker marker = map.addMarker(markerOptions);
                markers.put(marker.getId(), nearbyLocation.getResults().get(i).getPlaceId());
            }

        }
    }

    private void displayTheLocateButton(){
        View view = getView();
        if(view != null){
            FloatingActionButton locateUseerButton = view.findViewById(R.id.float_button_locate_user);
            locateUseerButton.setOnClickListener(v -> ((Go4Lunch) Objects.requireNonNull(getActivity())).getDeviceLocation());
        }
    }

    private void moveToWhereUserIsLocated(){
        if(getContext() != null){
            map.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_style));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), ConstantString.DEFAULT_ZOOM));
        }
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
