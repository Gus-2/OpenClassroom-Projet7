package com.openclassroom.go4lunch.ui;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassroom.go4lunch.R;
import com.openclassroom.go4lunch.models.Example;
import com.openclassroom.go4lunch.models.Result;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by de Meeûs Augustin on 2020-02-10
 **/
public class MyRestaurantAdapter extends RecyclerView.Adapter<MyRestaurantAdapter.RestaurantViewHolder> {

    private Example example;

    public class RestaurantViewHolder extends RecyclerView.ViewHolder {

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

        public RestaurantViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public MyRestaurantAdapter(Example example) {
        this.example = example;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyRestaurantAdapter.RestaurantViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.rv_restaurants_item, parent, false);
        RestaurantViewHolder restaurantViewHolder = new RestaurantViewHolder(v);
        return  restaurantViewHolder;

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RestaurantViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Log.d("key", example.getResults().get(position).getPlaceId());
        holder.tvRestaurantTitle.setText(example.getResults().get(position).getPlaceId());
    }


    @Override
    public int getItemCount() {
        return example.getResults().size();
    }
}
