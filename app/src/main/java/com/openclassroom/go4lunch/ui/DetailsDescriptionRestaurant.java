package com.openclassroom.go4lunch.ui;

import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.openclassroom.go4lunch.R;

import java.util.Objects;

public class DetailsDescriptionRestaurant extends AppCompatActivity {

    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerViewJoiningColleagues;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_description_restaurant);
        Toolbar toolbar = findViewById(R.id.toolbar);
        floatingActionButton = findViewById(R.id.fab);
        recyclerViewJoiningColleagues = findViewById(R.id.rv_info_restaurant_joining_colleagues);

        setSupportActionBar(toolbar);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        setAppBarLayoutParams( findViewById(R.id.app_bar));

        recyclerViewJoiningColleagues.setHasFixedSize(true);

        InfoRestaurantAdapter adapter = new InfoRestaurantAdapter();

        recyclerViewJoiningColleagues.setAdapter(adapter);

    }

    private void setAppBarLayoutParams(AppBarLayout appBar){
        float heightDp = getResources().getDisplayMetrics().heightPixels / 3;
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)appBar.getLayoutParams();
        lp.height = (int)heightDp;
    }

}
