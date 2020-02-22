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
import com.openclassroom.go4lunch.R;
import com.openclassroom.go4lunch.models.DetailsPlaces;
import com.openclassroom.go4lunch.models.Example;
import com.openclassroom.go4lunch.ui.Go4Lunch;
import com.openclassroom.go4lunch.utils.RestaurantDetailFormat;

import static android.Manifest.permission.CALL_PHONE;

public class DetailsRestaurantFragment extends Fragment {

    private TextView tvRestaurantTitle;
    private TextView tvRestaurantAddress;
    private TextView tvInfoRestaurantCall;
    private TextView tvInfoRestaurantWeb;
    private ImageView ivRestaurant;
    private ImageView ivDetailStar1;
    private ImageView ivDetailStar2;
    private ImageView ivDetailStar3;
    private int position;
    private DetailsPlaces detailsPlaces;
    private Example nearbyPlaces;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt("Position");
        detailsPlaces = ((Go4Lunch) getActivity()).getDetailsPlaces(position);
        nearbyPlaces = ((Go4Lunch) getActivity()).getNearbyLocations();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.details_description_restaurant, container, false);
        tvRestaurantTitle = view.findViewById(R.id.tv_info_restaurant_title);
        tvRestaurantAddress = view.findViewById(R.id.tv_info_restaurant_address);
        tvInfoRestaurantCall = view.findViewById(R.id.tv_info_restaurant_phone);
        tvInfoRestaurantWeb = view.findViewById(R.id.tv_info_restaurant_website);

        ivRestaurant = view.findViewById(R.id.iv_restaurant_picture_detail);
        ivDetailStar1 = view.findViewById(R.id.iv_detail_star_1);
        ivDetailStar2 = view.findViewById(R.id.iv_detail_star_2);
        ivDetailStar3 = view.findViewById(R.id.iv_detail_star_3);

        tvRestaurantTitle.setText(((Go4Lunch) getActivity()).getNearbyLocations().getResults().get(position).getName());
        tvRestaurantAddress.setText(RestaurantDetailFormat.parseAddress("", detailsPlaces.getResult().getFormattedAddress()));

        ((Go4Lunch) getActivity()).getSupportActionBar().hide();

        try{
            String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&maxHeight=400&" +
                    "photoreference=" + nearbyPlaces.getResults().get(position).getPhotos().get(0).getPhotoReference() +
                    "&key=AIzaSyAuYS7_WKfOe_Fztg-KdOoai7idmVrCWn8";

            Glide.with(getActivity())
                    .load(url)
                    .into(ivRestaurant);
        }catch(NullPointerException e){

        }

        int rating = (int) Math.round(nearbyPlaces.getResults().get(position).getRating());
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

        tvInfoRestaurantCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPhonePermission();
            }
        });

        tvInfoRestaurantWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Hi", "hpzede");
                startWebsite();
            }
        });
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        ((Go4Lunch) getActivity()).getSupportActionBar().show();
    }

    public void startPhoneCall(String phoneNumber){
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+phoneNumber));
        startActivity(callIntent);
    }

    public void startWebsite(){
        try{
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(detailsPlaces.getResult().getWebsite())));
        }catch(NullPointerException e){
            Toast.makeText(getActivity(), "Pas de site web disponible", Toast.LENGTH_SHORT);
        }

    }

    public void requestPhonePermission(){
        if (ContextCompat.checkSelfPermission(getActivity(), CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startPhoneCall(detailsPlaces.getResult().getInternationalPhoneNumber());
        } else {
            requestPermissions(new String[]{CALL_PHONE}, 1);
        }
    }
}
