package com.openclassroom.go4lunch.ui.map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
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
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
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
import com.openclassroom.go4lunch.ui.detailsRestaurant.DetailRestaurantActivity;
import com.openclassroom.go4lunch.utils.Checks;
import com.openclassroom.go4lunch.utils.ConstantString;
import com.openclassroom.go4lunch.utils.RestaurantDetailFormat;
import com.openclassroom.go4lunch.utils.SecurityChecks;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Created by de Meeûs Augustin on 2020-01-20
 **/
public class MapFragment extends Fragment implements OnMapReadyCallback{

    private static final int RESULT_CANCELED = 2;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private ListenerRegistration listenerRegistration;
    private MapView mapView;
    private GoogleMap map;
    private List<DataUserConnected> dataUserConnecteds;
    private NearbyPlaces nearbyLocation;
    private Location mLastKnownLocation;
    private HashMap<String, String> markers;
    private boolean alreadyUpdated = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SecurityChecks.checkGooglePlayServices(getContext());
        Bundle bundle = getArguments();
        setHasOptionsMenu(true);
        if(bundle == null){
            nearbyLocation = ((Go4Lunch) Objects.requireNonNull(getActivity())).getNearbyLocations();
            mLastKnownLocation = ((Go4Lunch) getActivity()).getLocation();
        }else{
            if(getArguments() != null){
                nearbyLocation = getArguments().getParcelable(ConstantString.NEARBY_LOCATION);
                mLastKnownLocation = getArguments().getParcelable(ConstantString.LOCATION);
            }
        }
        markers = new HashMap<>();
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.option_menu_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.search) {
            List<Place.Field> fields = Collections.singletonList(Place.Field.LAT_LNG);
            Location lastKnownLocation = ((Go4Lunch) getActivity()).getLastKnownLocation();
            double xPlus = lastKnownLocation.getLatitude() + ConstantString.ADD_TO_LATITUDE;
            double yPlus = lastKnownLocation.getLongitude() + ConstantString.ADD_TO_LONGITUDE;

            double xLess = lastKnownLocation.getLatitude() - ConstantString.ADD_TO_LATITUDE;
            double yLess = lastKnownLocation.getLongitude() - ConstantString.ADD_TO_LONGITUDE;

            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN, fields)
                    .setTypeFilter(TypeFilter.ESTABLISHMENT)
                    .setLocationRestriction(RectangularBounds.newInstance(
                            new LatLng(xLess, yLess),
                            new LatLng(xPlus, yPlus)))
                    .build(getActivity());
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Go4Lunch.RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                com.openclassroom.go4lunch.models.Location location = new com.openclassroom.go4lunch.models.Location(place.getLatLng().latitude, place.getLatLng().longitude);
                boolean isContained = Checks.isContainedInto(((Go4Lunch)getActivity()).getNearbyLocations(), location);
                if(isContained){
                    alreadyUpdated = true;
                    map.clear();
                    Drawable circleDrawable = getResources().getDrawable(R.drawable.ic_place_yellow_24dp);
                    BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);

                    MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(location.getLat(), location.getLng())).icon(markerIcon);
                    Marker marker = map.addMarker(markerOptions);

                    markers.put(marker.getId(), place.getId());
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(place.getLatLng().latitude, place.getLatLng().longitude), ConstantString.DEFAULT_ZOOM));
                }else{
                    Toast.makeText(getActivity(), R.string.restaurant_outside_area, Toast.LENGTH_LONG).show();
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Toast.makeText(getActivity(), R.string.processing_error, Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getActivity(), R.string.processing_canceled, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, container, false);
        mapView = view.findViewById(R.id.map_view_fragment);
        initGoogleMap(savedInstanceState);
        setHasOptionsMenu(true);
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
        if(!alreadyUpdated && map != null){
            map.clear();
            getUserChoices(map);
        }
        alreadyUpdated = false;
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