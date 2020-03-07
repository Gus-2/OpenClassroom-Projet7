package com.openclassroom.go4lunch.ui.detaileRestaurant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.openclassroom.go4lunch.R;
import com.openclassroom.go4lunch.models.DataUserConnected;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by de Meeûs Augustin on 2020-02-17
 **/
public class InfoRestaurantAdapter extends RecyclerView.Adapter<InfoRestaurantAdapter.InfoRestaurantHolder> {

    private List<DataUserConnected> dataUserConnecteds;

    public class InfoRestaurantHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_colleagues_picture)
        ImageView ivColleaguePicture;

        @BindView(R.id.tv_joining_colleague)
        TextView tvJoiningColleague;

        public InfoRestaurantHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public InfoRestaurantAdapter(List<DataUserConnected> dataUserConnecteds) {
        this.dataUserConnecteds = dataUserConnecteds;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public InfoRestaurantAdapter.InfoRestaurantHolder onCreateViewHolder(ViewGroup parent,
                                                                       int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_joining_colleagues, parent, false);

        InfoRestaurantAdapter.InfoRestaurantHolder infoViewHolder = new InfoRestaurantAdapter.InfoRestaurantHolder(v);
        return  infoViewHolder;

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(InfoRestaurantAdapter.InfoRestaurantHolder holder, int position) {
        Glide.with(holder.itemView.getContext())
                .load(dataUserConnecteds.get(position).getPhotoUrl())
                .into(holder.ivColleaguePicture);

        String[] names = dataUserConnecteds.get(position).getUserFirstNameAndLastname().split(" ");
        holder.tvJoiningColleague.setText(names[0] + " is joining !");
    }


    @Override
    public int getItemCount() {
        return dataUserConnecteds.size();
    }
}
