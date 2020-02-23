package com.openclassroom.go4lunch.ui.listRestaurant;


import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.openclassroom.go4lunch.R;
import com.openclassroom.go4lunch.models.DetailsPlaces;
import com.openclassroom.go4lunch.models.NearbyPlaces;
import com.openclassroom.go4lunch.models.Period;
import com.openclassroom.go4lunch.utils.RestaurantDetailFormat;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by de Meeûs Augustin on 2020-02-10
 **/
public class MyRestaurantAdapter extends RecyclerView.Adapter<MyRestaurantAdapter.RestaurantViewHolder> {

    private NearbyPlaces nearbyPlaces;
    private ArrayList<DetailsPlaces> detailsPlaces;
    private Context context;
    private Location lastKnownLocation;
    private OnRestaurantListener onRestaurantListener;
    private String parsedAddress;
    private String restaurantTitle;

    public class RestaurantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.tv_restaurant_title)
        TextView tvRestaurantTitle;

        @BindView(R.id.tv_type_address)
        TextView tvTypeAddress;

        @BindView(R.id.tv_opening_time)
        TextView tvOpeningTime;

        @BindView(R.id.tv_distance)
        TextView tvDistance;

        @BindView(R.id.iv_restaurant_picture)
        ImageView ivRestaurant;

        @BindView(R.id.iv_star_1)
        ImageView ivStar1;

        @BindView(R.id.iv_star_2)
        ImageView ivStar2;

        @BindView(R.id.iv_star_3)
        ImageView ivStar3;

        OnRestaurantListener onRestaurantListener;

        public RestaurantViewHolder(View itemView, OnRestaurantListener onRestaurantListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            this.onRestaurantListener = onRestaurantListener;
        }

        @Override
        public void onClick(View v) {
            onRestaurantListener.onRestaurantClick(getAdapterPosition());
        }
    }

    public MyRestaurantAdapter(Location location, NearbyPlaces nearbyPlaces,
                               ArrayList<DetailsPlaces> detailsPlaces, Context context,
    OnRestaurantListener onRestaurantListener) {
        this.nearbyPlaces = nearbyPlaces;
        this.detailsPlaces = detailsPlaces;
        this.context = context;
        this.lastKnownLocation = location;
        this.onRestaurantListener = onRestaurantListener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyRestaurantAdapter.RestaurantViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.rv_restaurants_item, parent, false);
        RestaurantViewHolder restaurantViewHolder = new RestaurantViewHolder(v, onRestaurantListener);
        return  restaurantViewHolder;

    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RestaurantViewHolder holder, int position) {

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK)-1;

        int hour = Integer.parseInt(calendar.get(Calendar.HOUR_OF_DAY) + "00");

        holder.tvRestaurantTitle.setText(nearbyPlaces.getResults().get(position).getName());
        try{
            DetailsPlaces detailPlace = getGoodDetailPlace(nearbyPlaces.getResults().get(position).getPlaceId());
            parsedAddress = RestaurantDetailFormat.parseAddress("", detailPlace.getResult().getFormattedAddress());
            holder.tvTypeAddress.setText(parsedAddress);

            if(nearbyPlaces.getResults().get(position).getOpeningHours().getOpenNow()){
                Log.d("a", "" + 1);
                for(Period period : detailPlace.getResult().getOpeningHours().getPeriods()){
                    if(period.getOpen().getDay() == day){
                        if((hour >= Integer.parseInt(period.getOpen().getTime())) &&  (hour <= Integer.parseInt(period.getClose().getTime()))){
                            String time = period.getClose().getTime();
                            holder.tvOpeningTime.setText("Open until " + (time.substring(0,2) + "h" + time.substring(2,4)));
                            holder.tvOpeningTime.setTextColor(context.getResources().getColor(R.color.police_color));
                            break;
                        }
                    }
                }
            }else{
                holder.tvOpeningTime.setText("Closed");
                holder.tvOpeningTime.setTextColor(context.getResources().getColor(R.color.red));
            }
        }catch (NullPointerException e){
            holder.tvOpeningTime.setText("Schedule not available");
            holder.tvOpeningTime.setTextColor(ContextCompat.getColor(context, R.color.police_color));
        }

        holder.tvDistance.setText("" + getDistanceBetweenTwoPoints(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), nearbyPlaces.getResults().get(position).getGeometry().getLocation().getLat(), nearbyPlaces.getResults().get(position).getGeometry().getLocation().getLng()) + " m");

        try{
            String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&maxHeight=400&" +
                    "photoreference=" + nearbyPlaces.getResults().get(position).getPhotos().get(0).getPhotoReference() +
                    "&key=AIzaSyAuYS7_WKfOe_Fztg-KdOoai7idmVrCWn8";

            Glide.with(context)
                    .load(url)
                    .into(holder.ivRestaurant);
        }catch(NullPointerException e){

        }


        int rating = (int) Math.round(nearbyPlaces.getResults().get(position).getRating());
        if(rating >= 1 && rating <= 2){
            holder.ivStar1.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_yellow_24dp));
        }else if(rating >= 3 && rating <= 4){
            holder.ivStar1.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_yellow_24dp));
            holder.ivStar2.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_yellow_24dp));
        }else if(rating == 5){
            holder.ivStar1.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_yellow_24dp));
            holder.ivStar2.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_yellow_24dp));
            holder.ivStar3.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_yellow_24dp));
        }

    }

    public DetailsPlaces getGoodDetailPlace(String id){
        for(DetailsPlaces detailPlace : detailsPlaces){
            if(detailPlace.getResult().getPlace_id().equals(id)) return detailPlace;
        }
        return null;
    }

    private int getDistanceBetweenTwoPoints(double lat1,double lon1,double lat2,double lon2) {

        float[] distance = new float[2];

        Location.distanceBetween( lat1, lon1,
                lat2, lon2, distance);

        return (int) distance[0];
    }

    @Override
    public int getItemCount() {
        return nearbyPlaces.getResults().size();
    }

    public interface OnRestaurantListener{
        void onRestaurantClick(int position);
    }
}
