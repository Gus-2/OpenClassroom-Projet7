package com.openclassroom.go4lunch.ui.workmates;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.openclassroom.go4lunch.R;
import com.openclassroom.go4lunch.models.DataUserConnected;
import com.openclassroom.go4lunch.models.NearbyPlaces;
import com.openclassroom.go4lunch.utils.Checks;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by de Meeûs Augustin on 2020-03-03
 **/
public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesAdapter.MyViewHolder> {
    private List<DataUserConnected> dataUserConnecteds;
    private NearbyPlaces nearbyPlaces;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_colleagues_picture)
        CircleImageView circleImageView;

        @BindView(R.id.tv_joining_colleague)
        TextView tvJoiningColleague;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public WorkmatesAdapter(List<DataUserConnected> dataUserConnecteds, NearbyPlaces nearbyPlaces) {
        this.dataUserConnecteds = dataUserConnecteds;
        this.nearbyPlaces = nearbyPlaces;
    }

    @Override
    public WorkmatesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_joining_colleagues, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext())
                .load(dataUserConnecteds.get(position).getPhotoUrl())
                .into(holder.circleImageView);
        String[] names = dataUserConnecteds.get(position).getUserFirstNameAndLastname().split(" ");


        if(dataUserConnecteds.get(position).getChoosenRestaurantForTheDay() != null && Checks.checkIfGoodDate(dataUserConnecteds.get(position).getDateOfTheChoosenRestaurant())){
            boolean found = false;
            for(int i = 0; i < nearbyPlaces.getResults().size(); i++){
                if(nearbyPlaces.getResults().get(i).getName().equals(dataUserConnecteds.get(position).getNameOfTheChoosenRestaurant())){
                    found = true;
                    break;
                }
            }
            if(found) holder.tvJoiningColleague.setText(names[0] + " is going to eat at " + dataUserConnecteds.get(position).getNameOfTheChoosenRestaurant());
            else  holder.tvJoiningColleague.setText(names[0] + " is going to eat outside of your area " );

        }else{
            holder.tvJoiningColleague.setText(names[0] + " hasn't decided yet ");
        }
    }

    @Override
    public int getItemCount() {
        return dataUserConnecteds.size();
    }
}
