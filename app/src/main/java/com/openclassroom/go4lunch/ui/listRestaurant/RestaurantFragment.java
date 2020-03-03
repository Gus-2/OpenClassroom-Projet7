package com.openclassroom.go4lunch.ui.listRestaurant;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassroom.go4lunch.R;
import com.openclassroom.go4lunch.models.DetailsPlaces;
import com.openclassroom.go4lunch.models.NearbyPlaces;
import com.openclassroom.go4lunch.models.Result;
import com.openclassroom.go4lunch.ui.Go4Lunch;
import com.openclassroom.go4lunch.ui.detaileRestaurant.DetailRestaurantActivity;
import com.openclassroom.go4lunch.ui.detaileRestaurant.DetailsRestaurantFragment;
import com.openclassroom.go4lunch.utils.RestaurantDetailFormat;

import java.util.ArrayList;

/**
 * Created by de Meeûs Augustin on 2020-01-20
 **/
public class RestaurantFragment extends Fragment implements MyRestaurantAdapter.OnRestaurantListener{

    private NearbyPlaces nearbyPlaces;
    private ArrayList<DetailsPlaces> detailsPlaces;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.restaurants_fragment, container, false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        RecyclerView recyclerView = rootView.findViewById(R.id.rv_restaurants);

        nearbyPlaces = getArguments().getParcelable("NearbyPlaces");
        detailsPlaces = getArguments().getParcelableArrayList("DetailsPlaces");
        Location lastKnownLocation = getArguments().getParcelable("Location");

        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.Adapter mAdapter = new MyRestaurantAdapter(lastKnownLocation, nearbyPlaces, detailsPlaces, getActivity(), this);
        recyclerView.setAdapter(mAdapter);

        return rootView;

    }


    @Override
    public void onRestaurantClick(int position) {
        Intent intent = new Intent(getActivity(), DetailRestaurantActivity.class);
        DetailsPlaces detailPlace = RestaurantDetailFormat.getDetailPlacesFromPlaceID(detailsPlaces, nearbyPlaces.getResults().get(position).getPlaceId());
        intent.putExtra("DetailPlace", detailPlace);
        Result result = ((Go4Lunch) getActivity()).getNearbyLocations().getResults().get(position);
        intent.putExtra("Result", result);
        startActivity(intent);
    }
}
