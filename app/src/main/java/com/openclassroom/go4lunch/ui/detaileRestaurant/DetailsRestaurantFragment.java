package com.openclassroom.go4lunch.ui.detaileRestaurant;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.UserDataConverter;
import com.openclassroom.go4lunch.R;
import com.openclassroom.go4lunch.database.FirebaseHelper;
import com.openclassroom.go4lunch.models.DataUserConnected;
import com.openclassroom.go4lunch.models.DetailsPlaces;
import com.openclassroom.go4lunch.models.NearbyPlaces;
import com.openclassroom.go4lunch.models.Result;
import com.openclassroom.go4lunch.ui.Go4Lunch;
import com.openclassroom.go4lunch.utils.RestaurantDetailFormat;

import java.util.Objects;

import static android.Manifest.permission.CALL_PHONE;

public class DetailsRestaurantFragment extends Fragment {

    private Result result;
    private DetailsPlaces detailPlace;
    private DataUserConnected dataUserConnected;
    private TextView tvRestaurantLike;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.details_description_restaurant, container, false);
        TextView tvRestaurantTitle = view.findViewById(R.id.tv_info_restaurant_title);
        TextView tvRestaurantAddress = view.findViewById(R.id.tv_info_restaurant_address);
        TextView tvInfoRestaurantCall = view.findViewById(R.id.tv_info_restaurant_phone);
        TextView tvInfoRestaurantWeb = view.findViewById(R.id.tv_info_restaurant_website);
        tvRestaurantLike = view.findViewById(R.id.tv_info_restaurant_like);

        ImageView ivRestaurant = view.findViewById(R.id.iv_restaurant_picture_detail);
        ImageView ivDetailStar1 = view.findViewById(R.id.iv_detail_star_1);
        ImageView ivDetailStar2 = view.findViewById(R.id.iv_detail_star_2);
        ImageView ivDetailStar3 = view.findViewById(R.id.iv_detail_star_3);

        Bundle bundle = getArguments();

        FirebaseHelper.getUserData(FirebaseAuth.getInstance().getUid()).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        dataUserConnected = document.toObject(DataUserConnected.class);

                        if(dataUserConnected.getLikedRestaurants().contains(detailPlace.getResult().getPlaceId())){
                            tvRestaurantLike.setCompoundDrawables(null, getResources().getDrawable(R.drawable.ic_star_blue_24dp), null, null);
                        }
                        setOnClickListenerOnLikeButton();
                    }
                }
            }
        });



        try {
            detailPlace = bundle.getParcelable("DetailPlace");
            result = bundle.getParcelable("Result");
        }catch (NullPointerException e){
            Toast.makeText(getActivity(), "Error retrieving data", Toast.LENGTH_SHORT).show();
        }

        tvRestaurantTitle.setText(result.getName());
        tvRestaurantAddress.setText(RestaurantDetailFormat.parseAddress("", detailPlace.getResult().getFormattedAddress()));

        try{
            String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&maxHeight=400&" +
                    "photoreference=" + result.getPhotos().get(0).getPhotoReference() +
                    "&key=AIzaSyAuYS7_WKfOe_Fztg-KdOoai7idmVrCWn8";

            Glide.with(getActivity())
                    .load(url)
                    .into(ivRestaurant);
        }catch(NullPointerException e){
            Toast.makeText(getActivity(), "Image unavailable !", Toast.LENGTH_SHORT).show();
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



        return view;
    }

    public void setOnClickListenerOnLikeButton(){
        tvRestaurantLike.setOnClickListener(v -> {
            if(dataUserConnected.getLikedRestaurants().contains(detailPlace.getResult().getPlaceId())){
                dataUserConnected.getLikedRestaurants().remove(detailPlace.getResult().getPlaceId());
                tvRestaurantLike.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_star_orange_24dp), null, null);
                Toast.makeText(getActivity(), "Restaurant remove from your liked list !", Toast.LENGTH_SHORT).show();
            }else{
                dataUserConnected.getLikedRestaurants().add(detailPlace.getResult().getPlaceId());
                tvRestaurantLike.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.ic_star_blue_24dp), null, null);
                Toast.makeText(getActivity(), "Restaurant add to your liked list !", Toast.LENGTH_SHORT).show();
            }
            FirebaseHelper.getUserDocument(FirebaseAuth.getInstance().getUid()).update("likedRestaurants", dataUserConnected.getLikedRestaurants());
        });
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void startPhoneCall(String phoneNumber){
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+phoneNumber));
        startActivity(callIntent);
    }

    private void startWebsite(){
        try{
            String website = detailPlace.getResult().getWebsite();
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(website)));
        }catch(NullPointerException e){
            Toast.makeText(getActivity(), "Pas de site web disponible", Toast.LENGTH_SHORT).show();
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
