package com.openclassroom.go4lunch.ui.detaileRestaurant;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import com.openclassroom.go4lunch.R;
import com.openclassroom.go4lunch.utils.ConstantString;

public class DetailRestaurantActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_restaurant);
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            Bundle bundle = new Bundle();
            bundle.putParcelable(ConstantString.DETAIL_PLACE, extras.getParcelable(ConstantString.DETAIL_PLACE));
            bundle.putParcelable(ConstantString.RESULT, extras.getParcelable(ConstantString.RESULT));

            DetailsRestaurantFragment detailsRestaurantFragment = new DetailsRestaurantFragment();
            detailsRestaurantFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().replace(R.id.details_fragment_container, detailsRestaurantFragment).commit();
        }else{
            Toast.makeText(getApplicationContext(), R.string.unable_pass_data_activities, Toast.LENGTH_SHORT).show();
        }

    }
}
