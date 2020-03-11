package com.openclassroom.go4lunch.ui.listRestaurant;

import android.content.Intent;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
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
import com.openclassroom.go4lunch.utils.ConstantString;
import com.openclassroom.go4lunch.utils.RestaurantDetailFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by de Meeûs Augustin on 2020-01-20
 **/
public class RestaurantFragment extends Fragment implements MyRestaurantAdapter.OnRestaurantListener{

    private static final int RESULT_CANCELED = 2;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private NearbyPlaces nearbyPlaces;
    private ArrayList<DetailsPlaces> detailsPlaces;
    private ListenerRegistration listenerRegistration;
    private List<DataUserConnected> dataUserConnecteds;
    private Location lastKnownLocation;
    private RecyclerView.Adapter mAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.restaurants_fragment, container, false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        RecyclerView recyclerView = rootView.findViewById(R.id.rv_restaurants);
        if(getArguments() != null){
            nearbyPlaces = getArguments().getParcelable(ConstantString.NEARBY_PLACES);
            detailsPlaces = getArguments().getParcelableArrayList(ConstantString.DETAILS_PLACES);
            lastKnownLocation = getArguments().getParcelable(ConstantString.LOCATION);
        }
        dataUserConnecteds = new ArrayList<>();
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new MyRestaurantAdapter(lastKnownLocation, nearbyPlaces, detailsPlaces, dataUserConnecteds, getActivity(), this);
        recyclerView.setAdapter(mAdapter);
        setHasOptionsMenu(true);
        return rootView;

    }

    private void downloadRestaurantsAndDisplayIt(RecyclerView.Adapter mAdapter){
        CollectionReference collectionReference = FirebaseHelper.getUserCollection();
        listenerRegistration = collectionReference.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if(e != null) e.printStackTrace();
            dataUserConnecteds.clear();
            if(queryDocumentSnapshots != null){
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    dataUserConnecteds.add(documentSnapshot.toObject(DataUserConnected.class));
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.option_menu_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.search) {
            double xPlus = lastKnownLocation.getLatitude() + 0.010000;
            double yPlus = lastKnownLocation.getLongitude() + 0.100000;

            double xLess = lastKnownLocation.getLatitude() - 0.010000;
            double yLess = lastKnownLocation.getLongitude() - 0.100000;

            List<Place.Field> fields = Arrays.asList(Place.Field.ID);

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Go4Lunch.RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                DetailsPlaces detailPlace = RestaurantDetailFormat.getDetailPlacesFromPlaceID(detailsPlaces, place.getId());
                if(detailPlace != null){
                    Intent intent = new Intent(getActivity(), DetailRestaurantActivity.class);
                    intent.putExtra(ConstantString.DETAIL_PLACE, detailPlace);
                    Result result = ((Go4Lunch) getActivity()).getNearbyLocations().getResults().get(RestaurantDetailFormat.getPositionFromPlaceID(nearbyPlaces, detailPlace.getResult().getPlaceId()));
                    intent.putExtra(ConstantString.RESULT, result);
                    startActivity(intent);
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


    @Override
    public void onResume() {
        super.onResume();
        downloadRestaurantsAndDisplayIt(mAdapter);
    }

    @Override
    public void onRestaurantClick(int position) {
        Intent intent = new Intent(getActivity(), DetailRestaurantActivity.class);
        DetailsPlaces detailPlace = RestaurantDetailFormat.getDetailPlacesFromPlaceID(detailsPlaces, nearbyPlaces.getResults().get(position).getPlaceId());
        intent.putExtra(ConstantString.DETAIL_PLACE, detailPlace);
        Result result = ((Go4Lunch) getActivity()).getNearbyLocations().getResults().get(position);
        intent.putExtra(ConstantString.RESULT, result);
        startActivity(intent);
    }

    @Override
    public void onStop() {
        super.onStop();
        listenerRegistration.remove();
    }
}
