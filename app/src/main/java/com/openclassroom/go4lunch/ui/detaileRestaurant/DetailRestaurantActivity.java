package com.openclassroom.go4lunch.ui.detaileRestaurant;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.openclassroom.go4lunch.R;

public class DetailRestaurantActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_restaurant);
        Bundle extras = getIntent().getExtras();
        if(extras != null){

            Bundle bundle = new Bundle();
            bundle.putParcelable("DetailPlace", extras.getParcelable("DetailPlace"));
            bundle.putParcelable("Result", extras.getParcelable("Result"));

            DetailsRestaurantFragment detailsRestaurantFragment = new DetailsRestaurantFragment();
            detailsRestaurantFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().replace(R.id.details_fragment_container, detailsRestaurantFragment).commit();
        }else{
            Toast.makeText(getApplicationContext(), "Unable to pass data between activities !", Toast.LENGTH_SHORT).show();
        }

    }
}
