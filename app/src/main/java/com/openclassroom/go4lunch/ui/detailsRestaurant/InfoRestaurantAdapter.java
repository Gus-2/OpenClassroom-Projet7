package com.openclassroom.go4lunch.ui.detailsRestaurant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
    private Context context;

    class InfoRestaurantHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_colleagues_picture)
        ImageView ivColleaguePicture;

        @BindView(R.id.tv_joining_colleague)
        TextView tvJoiningColleague;

        InfoRestaurantHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    InfoRestaurantAdapter(Context context, List<DataUserConnected> dataUserConnecteds) {
        this.context = context;
        this.dataUserConnecteds = dataUserConnecteds;
    }
    @NonNull
    @Override
    public InfoRestaurantAdapter.InfoRestaurantHolder onCreateViewHolder(ViewGroup parent,
                                                                       int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_joining_colleagues, parent, false);

        return new InfoRestaurantHolder(v);

    }

    @Override
    public void onBindViewHolder(InfoRestaurantAdapter.InfoRestaurantHolder holder, int position) {
        Glide.with(holder.itemView.getContext())
                .load(dataUserConnecteds.get(position).getPhotoUrl())
                .into(holder.ivColleaguePicture);

        String[] names = dataUserConnecteds.get(position).getUserFirstNameAndLastname().split(" ");
        holder.tvJoiningColleague.setText(String.format(context.getResources().getString(R.string.joining_colleague), names[0]));
    }


    @Override
    public int getItemCount() {
        return dataUserConnecteds.size();
    }
}
