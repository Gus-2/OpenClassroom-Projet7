package com.openclassroom.go4lunch.ui.detailsRestaurant;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.openclassroom.go4lunch.BuildConfig;
import com.openclassroom.go4lunch.R;
import com.openclassroom.go4lunch.database.FirebaseHelper;
import com.openclassroom.go4lunch.models.DataUserConnected;
import com.openclassroom.go4lunch.models.DetailsPlaces;
import com.openclassroom.go4lunch.models.Result;
import com.openclassroom.go4lunch.utils.Checks;
import com.openclassroom.go4lunch.utils.ConstantString;
import com.openclassroom.go4lunch.utils.RestaurantDetailFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.Manifest.permission.CALL_PHONE;

public class DetailsRestaurantFragment extends Fragment {

    private Result result;
    private DetailsPlaces detailPlace;
    private DataUserConnected dataUserConnected;
    private TextView tvRestaurantLike;
    private ListenerRegistration listenerRegistration;
    private List<DataUserConnected> dataUserConnecteds;
    private String address;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    @SuppressWarnings("ConstantConditions")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dataUserConnecteds = new ArrayList<>();
        View view = inflater.inflate(R.layout.details_description_restaurant, container, false);
        TextView tvRestaurantTitle = view.findViewById(R.id.tv_info_restaurant_title);
        TextView tvRestaurantAddress = view.findViewById(R.id.tv_info_restaurant_address);
        TextView tvInfoRestaurantCall = view.findViewById(R.id.tv_info_restaurant_phone);
        TextView tvInfoRestaurantWeb = view.findViewById(R.id.tv_info_restaurant_website);
        tvRestaurantLike = view.findViewById(R.id.tv_info_restaurant_like);
        FloatingActionButton floatingActionButtonChoosenRestaurant = view.findViewById(R.id.fab_choosen_restaurant_for_the_day);
        ImageView ivRestaurant = view.findViewById(R.id.iv_restaurant_picture_detail);
        ImageView ivDetailStar1 = view.findViewById(R.id.iv_detail_star_1);
        ImageView ivDetailStar2 = view.findViewById(R.id.iv_detail_star_2);
        ImageView ivDetailStar3 = view.findViewById(R.id.iv_detail_star_3);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        RecyclerView recyclerView = view.findViewById(R.id.rv_info_restaurant_joining_colleagues);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        InfoRestaurantAdapter mAdapter = new InfoRestaurantAdapter(getActivity(), dataUserConnecteds);
        recyclerView.setAdapter(mAdapter);

        Bundle bundle = getArguments();

        FirebaseHelper.getUserData(FirebaseAuth.getInstance().getUid()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if(task.getResult() != null){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        dataUserConnected = document.toObject(DataUserConnected.class);
                        if(dataUserConnected.getLikedRestaurants().contains(detailPlace.getResult().getPlaceId())){
                            tvRestaurantLike.setCompoundDrawables(null, getResources().getDrawable(R.drawable.ic_star_blue_24dp), null, null);
                            break;
                        }
                    }
                }
                setOnClickListenerOnLikeButton();
            }
        });

        CollectionReference collectionReference = FirebaseHelper.getUserCollection();

        listenerRegistration = collectionReference.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if(e != null) e.printStackTrace();
            dataUserConnecteds.clear();
            if(queryDocumentSnapshots != null){
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    DataUserConnected dataUserConnected = documentSnapshot.toObject(DataUserConnected.class);
                    if(dataUserConnected.getChoosenRestaurantForTheDay() != null && dataUserConnected.getChoosenRestaurantForTheDay().equals(detailPlace.getResult().getPlaceId()) && Checks.checkIfGoodDate(dataUserConnected.getDateOfTheChoosenRestaurant())){
                        dataUserConnecteds.add(dataUserConnected);
                        mAdapter.notifyDataSetChanged();
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

        if(bundle != null){
            detailPlace = bundle.getParcelable(ConstantString.DETAIL_PLACE);
            result = bundle.getParcelable(ConstantString.RESULT);
        }

        tvRestaurantTitle.setText(Objects.requireNonNull(result).getName());

        address = RestaurantDetailFormat.parseAddress(detailPlace.getResult().getFormattedAddress());
        tvRestaurantAddress.setText(address);

        try{
            String url = ConstantString.URL_FIRST_PICTURE + result.getPhotos().get(0).getPhotoReference() +
                    ConstantString.URL_END_PART + BuildConfig.API_KEY;
            Activity activity = getActivity();
            if(activity != null){
                Glide.with(getActivity())
                        .load(url)
                        .into(ivRestaurant);
            }
        }catch(NullPointerException e){
            Toast.makeText(getActivity(), R.string.image_unavailable, Toast.LENGTH_SHORT).show();
        }

        int rating = (int) Math.round(result.getRating());
        if(rating >= 1 && rating <= 2){
            ivDetailStar1.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_star_yellow_24dp));
        }else if(rating >= 3 && rating <= 4){
            ivDetailStar1.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_star_yellow_24dp));
            ivDetailStar2.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_star_yellow_24dp));
        }else if(rating == 5){
            ivDetailStar1.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_star_yellow_24dp));
            ivDetailStar2.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_star_yellow_24dp));
            ivDetailStar3.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_star_yellow_24dp));
        }

        tvInfoRestaurantCall.setOnClickListener(v -> requestPhonePermission());

        tvInfoRestaurantWeb.setOnClickListener(v -> startWebsite());

        floatingActionButtonChoosenRestaurant.setOnClickListener(v -> FirebaseHelper.getUserData(FirebaseAuth.getInstance().getUid()).addOnCompleteListener(task -> {
            DataUserConnected dataUserConnected = task.getResult().getDocuments().get(0).toObject(DataUserConnected.class);
            if(dataUserConnected.getChoosenRestaurantForTheDay() != null && dataUserConnected.getChoosenRestaurantForTheDay().equals(detailPlace.getResult().getPlaceId()) && Checks.checkIfGoodDate(dataUserConnected.getDateOfTheChoosenRestaurant())){
                updateChoosenRestaurantForTheDay(null, null, null, null, getString(R.string.choice_succed_removed));
            }else{
                updateChoosenRestaurantForTheDay(result.getPlaceId(), Timestamp.now(), result.getName(), address, getString(R.string.choice_success_registered));
            }
        }));

        return view;
    }

    private void updateChoosenRestaurantForTheDay(String placeId, Timestamp timestamp, String name, String address, String message){

        FirebaseHelper.getUserDocument(FirebaseAuth.getInstance().getUid())
                .update(ConstantString.CHOOSEN_RESTAURANT_FOR_THE_DAY, placeId, ConstantString.DATE_CHOOSEN_RESTAURANT_FOR_THE_DAY, timestamp,
                        ConstantString.NAME_CHOOSEN_RESTAURANT_FOR_THE_DAY, name, ConstantString.ADDRESS_CHOOSEN_RESTAURANT_FOR_THE_DAY, address)
                .addOnSuccessListener(aVoid -> Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getActivity(), getString(R.string.choice_succed_removed), Toast.LENGTH_SHORT).show());
    }

    private void setOnClickListenerOnLikeButton(){
        tvRestaurantLike.setOnClickListener(v -> {
            if(dataUserConnected.getLikedRestaurants().contains(detailPlace.getResult().getPlaceId())){
                dataUserConnected.getLikedRestaurants().remove(detailPlace.getResult().getPlaceId());
                tvRestaurantLike.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_star_orange_24dp), null, null);
                Toast.makeText(getActivity(), R.string.restaurant_removed_list, Toast.LENGTH_SHORT).show();
            }else{
                dataUserConnected.getLikedRestaurants().add(detailPlace.getResult().getPlaceId());
                tvRestaurantLike.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_star_blue_24dp), null, null);
                Toast.makeText(getActivity(), R.string.restaurant_added_list, Toast.LENGTH_SHORT).show();
            }
            FirebaseHelper.getUserDocument(FirebaseAuth.getInstance().getUid()).update(ConstantString.LIKED_RESTAURANT, dataUserConnected.getLikedRestaurants());
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        listenerRegistration.remove();
    }

    private void startPhoneCall(String phoneNumber){
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse(ConstantString.TEL + phoneNumber));
        startActivity(callIntent);
    }

    private void startWebsite(){
        try{
            String website = detailPlace.getResult().getWebsite();
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(website)));
        }catch(NullPointerException e){
            Toast.makeText(getActivity(), getString(R.string.no_website), Toast.LENGTH_SHORT).show();
        }
    }

    private void requestPhonePermission(){
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startPhoneCall(detailPlace.getResult().getInternationalPhoneNumber());
        } else {
            requestPermissions(new String[]{CALL_PHONE}, 1);
        }
    }
}
