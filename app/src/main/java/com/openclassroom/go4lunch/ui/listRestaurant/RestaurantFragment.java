package com.openclassroom.go4lunch.ui.listRestaurant;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.openclassroom.go4lunch.ui.detaileRestaurant.DetailsRestaurantFragment;
import com.openclassroom.go4lunch.utils.Checks;
import com.openclassroom.go4lunch.utils.RestaurantDetailFormat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by de Meeûs Augustin on 2020-01-20
 **/
public class RestaurantFragment extends Fragment implements MyRestaurantAdapter.OnRestaurantListener{

    private NearbyPlaces nearbyPlaces;
    private ArrayList<DetailsPlaces> detailsPlaces;
    private ListenerRegistration listenerRegistration;
    private List<DataUserConnected> dataUserConnecteds;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.restaurants_fragment, container, false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        RecyclerView recyclerView = rootView.findViewById(R.id.rv_restaurants);

        nearbyPlaces = getArguments().getParcelable("NearbyPlaces");
        detailsPlaces = getArguments().getParcelableArrayList("DetailsPlaces");
        Location lastKnownLocation = getArguments().getParcelable("Location");

        CollectionReference collectionReference = FirebaseHelper.getUserCollection();
        dataUserConnecteds = new ArrayList<>();
        listenerRegistration = collectionReference.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if(e != null) Log.e("Error :", "Retrieving workmates eating list !");
            dataUserConnecteds.clear();
            for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                dataUserConnecteds.add(documentSnapshot.toObject(DataUserConnected.class));

            }
            recyclerView.setLayoutManager(layoutManager);
            RecyclerView.Adapter mAdapter = new MyRestaurantAdapter(lastKnownLocation, nearbyPlaces, detailsPlaces, dataUserConnecteds, getActivity(), this);
            recyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        });
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

    @Override
    public void onStop() {
        super.onStop();
        listenerRegistration.remove();
    }
}
