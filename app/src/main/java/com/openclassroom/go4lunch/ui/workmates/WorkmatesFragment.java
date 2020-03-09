package com.openclassroom.go4lunch.ui.workmates;

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
import com.openclassroom.go4lunch.ui.Go4Lunch;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by de Meeûs Augustin on 2020-01-20
 **/
public class WorkmatesFragment extends Fragment {
    private List<DataUserConnected> dataUserConnecteds = new ArrayList<>();
    private ListenerRegistration listenerRegistration;
    private WorkmatesAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.workmates_fragment, container, false);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        RecyclerView recyclerView = view.findViewById(R.id.rv_where_colleagues_are_eating);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new WorkmatesAdapter(dataUserConnecteds, ((Go4Lunch) getActivity()).getNearbyLocations());
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        CollectionReference collectionReference = FirebaseHelper.getUserCollection();
        listenerRegistration = collectionReference.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if(e != null) Log.e("Error :", "Retrieving wormates list !");
            dataUserConnecteds.clear();
            for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                dataUserConnecteds.add(documentSnapshot.toObject(DataUserConnected.class));
                mAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onStop() {
        super.onStop();
        listenerRegistration.remove();
    }
}
