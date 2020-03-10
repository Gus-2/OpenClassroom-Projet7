package com.openclassroom.go4lunch.ui.listRestaurant;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.openclassroom.go4lunch.BuildConfig;
import com.openclassroom.go4lunch.R;
import com.openclassroom.go4lunch.models.DataUserConnected;
import com.openclassroom.go4lunch.models.DetailsPlaces;
import com.openclassroom.go4lunch.models.NearbyPlaces;
import com.openclassroom.go4lunch.models.Period;
import com.openclassroom.go4lunch.utils.Checks;
import com.openclassroom.go4lunch.utils.ConstantString;
import com.openclassroom.go4lunch.utils.RestaurantDetailFormat;
import com.openclassroom.go4lunch.utils.Tools;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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
    private List<DataUserConnected> dataUserConnecteds;

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

        @BindView(R.id.tv_number_colleague)
        TextView tvNumberColleagueJoining;

        OnRestaurantListener onRestaurantListener;

        RestaurantViewHolder(View itemView, OnRestaurantListener onRestaurantListener) {
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

    MyRestaurantAdapter(Location location, NearbyPlaces nearbyPlaces,
                        ArrayList<DetailsPlaces> detailsPlaces, List<DataUserConnected> dataUserConnecteds, Context context,
                        OnRestaurantListener onRestaurantListener) {
        this.nearbyPlaces = nearbyPlaces;
        this.detailsPlaces = detailsPlaces;
        this.context = context;
        this.dataUserConnecteds = dataUserConnecteds;
        this.lastKnownLocation = location;
        this.onRestaurantListener = onRestaurantListener;
    }

    @Override
    @NonNull
    public MyRestaurantAdapter.RestaurantViewHolder onCreateViewHolder(ViewGroup parent,
                                                                       int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_restaurants_item, parent, false);
        return new RestaurantViewHolder(v, onRestaurantListener);

    }

    @Override
    public void onBindViewHolder(RestaurantViewHolder holder, int position) {

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK)-1;

        int hour = Integer.parseInt(calendar.get(Calendar.HOUR_OF_DAY) + "00");

        holder.tvRestaurantTitle.setText(nearbyPlaces.getResults().get(position).getName());
        try{
            DetailsPlaces detailPlace = getGoodDetailPlace(nearbyPlaces.getResults().get(position).getPlaceId());
            String parsedAddress = RestaurantDetailFormat.parseAddress(detailPlace.getResult().getFormattedAddress());
            holder.tvTypeAddress.setText(parsedAddress);

            if(nearbyPlaces.getResults().get(position).getOpeningHours().getOpenNow()){
                for(Period period : detailPlace.getResult().getOpeningHours().getPeriods()){
                    if(period.getOpen().getDay() == day){

                        String time = period.getClose().getTime();
                        holder.tvOpeningTime.setText(String.format(context.getResources().getString(R.string.open_until), time.substring(0,2), time.substring(2,4)));
                        holder.tvOpeningTime.setTextColor(context.getResources().getColor(R.color.police_color));
                        break;

                    }
                }
            }else{
                holder.tvOpeningTime.setText(R.string.closed);
                holder.tvOpeningTime.setTextColor(context.getResources().getColor(R.color.red));
            }
        }catch (NullPointerException e){
            holder.tvOpeningTime.setText(R.string.schedule_unavailable);
            holder.tvOpeningTime.setTextColor(ContextCompat.getColor(context, R.color.police_color));
        }

        holder.tvDistance.setText(String.format(context.getResources().getString(R.string.distance_between_two_points),
                Tools.getDistanceBetweenTwoPoints(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), nearbyPlaces.getResults().get(position).getGeometry().getLocation().getLat(), nearbyPlaces.getResults().get(position).getGeometry().getLocation().getLng())));

        try{
            String url = ConstantString.URL_FIRST_PART + nearbyPlaces.getResults().get(position).getPhotos().get(0).getPhotoReference() + ConstantString.URL_END_PART + BuildConfig.API_KEY;
            Glide.with(context)
                    .load(url)
                    .into(holder.ivRestaurant);
        }catch(NullPointerException e){
            e.printStackTrace();
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
        int i = 0;
        for(DataUserConnected dataUserConnected : dataUserConnecteds){
            if(dataUserConnected.getChoosenRestaurantForTheDay() != null && dataUserConnected.getChoosenRestaurantForTheDay().equals(nearbyPlaces.getResults().get(position).getPlaceId()) && Checks.checkIfGoodDate(dataUserConnected.getDateOfTheChoosenRestaurant())){
                i++;
            }
        }
        if(i == 0){
            holder.tvNumberColleagueJoining.setVisibility(View.GONE);
        }else{
            holder.tvNumberColleagueJoining.setText(String.format(context.getResources().getString(R.string.joining_colleagues), i));
            holder.tvNumberColleagueJoining.setVisibility(View.VISIBLE);
        }

    }

    private DetailsPlaces getGoodDetailPlace(String id){
        for(DetailsPlaces detailPlace : detailsPlaces){
            if(detailPlace.getResult().getPlaceId().equals(id)) return detailPlace;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return nearbyPlaces.getResults().size();
    }

    public interface OnRestaurantListener{
        void onRestaurantClick(int position);
    }
}

