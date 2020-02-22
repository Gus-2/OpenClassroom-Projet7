package com.openclassroom.go4lunch.ui.listRestaurant;

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
import com.openclassroom.go4lunch.models.Example;
import com.openclassroom.go4lunch.ui.detaileRestaurant.DetailsRestaurantFragment;

import java.util.ArrayList;

/**
 * Created by de Meeûs Augustin on 2020-01-20
 **/
public class RestaurantFragment extends Fragment implements MyRestaurantAdapter.OnRestaurantListener{

    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter mAdapter;
    private Example nearbyPlaces;
    private RecyclerView recyclerView;
    private ArrayList<DetailsPlaces> detailsPlaces;
    private Location lastKnownLocation;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.restaurants_fragment, container, false);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView = rootView.findViewById(R.id.rv_restaurants);

        nearbyPlaces = getArguments().getParcelable("NearbyPlaces");
        detailsPlaces = getArguments().getParcelableArrayList("DetailsPlaces");
        lastKnownLocation = getArguments().getParcelable("Location");

        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new MyRestaurantAdapter(lastKnownLocation, nearbyPlaces, detailsPlaces, getActivity(), this);
        recyclerView.setAdapter(mAdapter);

        return rootView;

    }


    @Override
    public void onRestaurantClick(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("Position", position);
        DetailsRestaurantFragment detailsRestaurantFragment = new DetailsRestaurantFragment();
        detailsRestaurantFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, detailsRestaurantFragment).commit();
    }
}
